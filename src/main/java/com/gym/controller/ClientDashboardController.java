package com.gym.controller;

import com.gym.dao.impl.*;
import com.gym.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientDashboardController {
    @FXML private ImageView profileImage;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    
    @FXML private TextArea medicalNotesArea;
    
    @FXML private Label membershipTypeLabel;
    @FXML private Label membershipStatusLabel;
    @FXML private Label membershipDateLabel;
    @FXML private Label membershipVisitsLabel;
    
    @FXML private TableView<Booking> upcomingTrainingsTable;
    @FXML private TableColumn<Booking, String> trainingDateColumn;
    @FXML private TableColumn<Booking, String> trainingTimeColumn;
    @FXML private TableColumn<Booking, String> trainingNameColumn;
    @FXML private TableColumn<Booking, String> trainerColumn;
    @FXML private TableColumn<Booking, Integer> durationColumn;
    @FXML private TableColumn<Booking, String> upcomingStatusColumn;
    @FXML private TableColumn<Booking, Void> upcomingActionsColumn;
    
    @FXML private TableView<Booking> trainingHistoryTable;
    @FXML private TableColumn<Booking, String> historyDateColumn;
    @FXML private TableColumn<Booking, String> historyTrainingColumn;
    @FXML private TableColumn<Booking, String> historyTrainerColumn;
    @FXML private TableColumn<Booking, String> historyStatusColumn;

    @FXML private Button logoutButton;
    @FXML private Button bookTrainingButton;

    private final ClientDao clientDao = new ClientDao();
    private final BookingDao bookingDao = new BookingDao();
    private final MembershipDao membershipDao = new MembershipDao();
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private Long clientId;

    public void initialize(Long clientId, String username) {
        log.info("Initializing client dashboard for user ID: {} and username: {}", clientId, username);
        this.clientId = clientId;
        nameLabel.setText(username);
        loadClientData();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            log.error("Error loading login view", e);
        }
    }

    private void loadMembershipInfo(Client client) {
        try {
            List<Membership> memberships = membershipDao.findActiveByClientId(client.getId());
            Membership membership = memberships.isEmpty() ? null : memberships.get(0);
            if (membership != null) {
                membershipTypeLabel.setText("Type: " + membership.getType().toString().toUpperCase());
                membershipStatusLabel.setText("Status: " + (membership.isPaid() ? "PAID" : "UNPAID"));
                
                String dateInfo = "Valid: " + membership.getStartDate().format(dateFormatter);
                if (membership.getEndDate() != null) {
                    dateInfo += " to " + membership.getEndDate().format(dateFormatter);
                }
                membershipDateLabel.setText(dateInfo);
                
                if (membership.getType() == Membership.MembershipType.limited && membership.getVisitsLeft() != null) {
                    membershipVisitsLabel.setText("Visits left: " + membership.getVisitsLeft());
                    membershipVisitsLabel.setVisible(true);
                } else {
                    membershipVisitsLabel.setVisible(false);
                }
            } else {
                membershipTypeLabel.setText("No active membership");
                membershipStatusLabel.setText("");
                membershipDateLabel.setText("");
                membershipVisitsLabel.setVisible(false);
            }
        } catch (Exception e) {
            log.error("Error loading membership info", e);
            membershipTypeLabel.setText("Error loading membership info");
        }
    }

    private void loadClientData() {
        if (clientId == null) {
            log.error("Client ID is not set");
            return;
        }

        try {
            log.info("Loading client data for user ID: {}", clientId);
            
            Client client = clientDao.findByUserId(clientId);
            if (client != null) {
                log.info("Found client: {} with email: {} and phone: {}", 
                    client.getFullName(), client.getEmail(), client.getPhone());
                
                nameLabel.setText(client.getFullName());
                emailLabel.setText(client.getEmail());
                phoneLabel.setText(client.getPhone());
                
                
                if (client.getMedicalNotes() != null && !client.getMedicalNotes().isEmpty()) {
                    log.info("Setting medical notes: {}", client.getMedicalNotes());
                    medicalNotesArea.setText(client.getMedicalNotes());
                } else {
                    log.info("No medical notes found");
                    medicalNotesArea.setText("No medical notes available");
                }
                
                medicalNotesArea.setStyle("-fx-text-fill: white; -fx-control-inner-background: #1a1a1a;");

                
                loadMembershipInfo(client);

                
                if (client.getPhoto() != null) {
                    Image image = new Image(new ByteArrayInputStream(client.getPhoto()));
                    profileImage.setImage(image);
                }

                
                loadTrainingSchedules(client);
            } else {
                log.error("No client found for user ID: {}", clientId);
                
                emailLabel.setText("Email: Not available");
                phoneLabel.setText("Phone: Not available");
                medicalNotesArea.setText("No client information available");
            }
        } catch (Exception e) {
            log.error("Error loading client data", e);
        }
    }



    private void loadTrainingSchedules(Client client) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> allBookings = bookingDao.findByClientId(client.getId());
        
        
        List<Booking> upcomingBookings = allBookings.stream()
            .filter(booking -> booking.getTraining().getScheduledAt().isAfter(now))
            .sorted((b1, b2) -> b1.getTraining().getScheduledAt().compareTo(b2.getTraining().getScheduledAt()))
            .collect(Collectors.toList());
        upcomingTrainingsTable.getItems().setAll(upcomingBookings);

        
        List<Booking> pastBookings = allBookings.stream()
            .filter(booking -> booking.getTraining().getScheduledAt().isBefore(now))
            .sorted((b1, b2) -> b2.getTraining().getScheduledAt().compareTo(b1.getTraining().getScheduledAt())) 
            .collect(Collectors.toList());
        trainingHistoryTable.getItems().setAll(pastBookings);

        log.info("Found {} upcoming and {} past bookings for client {}", 
            upcomingBookings.size(), pastBookings.size(), client.getFullName());

        
        setupUpcomingTrainingsTable();
        setupTrainingHistoryTable();
    }

    private void setupUpcomingTrainingsTable() {
        trainingDateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getScheduledAt().format(dateFormatter)));
        trainingTimeColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getScheduledAt().format(timeFormatter)));
        trainingNameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getName()));
        trainerColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getTrainer().getFullName()));
        durationColumn.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getTraining().getDurationMinutes()).asObject());
        upcomingStatusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
            
        
        upcomingActionsColumn.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button cancelButton = createActionButton("TIMES_CIRCLE", "cancel-icon", "Cancel");

            {
                cancelButton.setOnAction(event -> handleCancelBooking(getTableRow().getItem()));
                cancelButton.getStyleClass().add("cancel-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableRow().getItem();
                    if (booking != null) {
                        
                        if (booking.getStatus() == BookingStatus.BOOKED) {
                            setGraphic(cancelButton);
                        } else {
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        
        upcomingTrainingsTable.setRowFactory(tv -> new TableRow<Booking>() {
            @Override
            protected void updateItem(Booking booking, boolean empty) {
                super.updateItem(booking, empty);
                if (booking == null || empty) {
                    setStyle("");
                } else {
                    
                    getStyleClass().add("upcoming-training-row");
                }
            }
        });
    }
    
    private Button createActionButton(String icon, String styleClass, String text) {
        Button button = new Button(text);
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
        iconView.getStyleClass().add(styleClass);
        button.setGraphic(iconView);
        button.getStyleClass().add("action-button");
        return button;
    }
    
    private void handleCancelBooking(Booking booking) {
        if (booking == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Training Booking");
        alert.setContentText("Are you sure you want to cancel your booking for " + 
            booking.getTraining().getName() + " on " + 
            booking.getTraining().getScheduledAt().format(dateFormatter) + " at " +
            booking.getTraining().getScheduledAt().format(timeFormatter) + "?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingDao.update(booking);
                    
                    
                    Client client = clientDao.findByUserId(clientId);
                    if (client != null) {
                        loadTrainingSchedules(client);
                    }
                    
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Booking Cancelled");
                    successAlert.setHeaderText("Booking Successfully Cancelled");
                    successAlert.setContentText("Your booking has been cancelled.");
                    successAlert.showAndWait();
                    
                } catch (Exception e) {
                    log.error("Error cancelling booking: ", e);
                    showError("Error", "Could not cancel booking", e.getMessage());
                }
            }
        });
    }

    private void setupTrainingHistoryTable() {
        historyDateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getScheduledAt().format(dateFormatter)));
        historyTrainingColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getName()));
        historyTrainerColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTraining().getTrainer().getFullName()));
        historyStatusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));

        
        trainingHistoryTable.setRowFactory(tv -> new TableRow<Booking>() {
            @Override
            protected void updateItem(Booking booking, boolean empty) {
                super.updateItem(booking, empty);
                if (booking == null || empty) {
                    setStyle("");
                } else {
                    
                    getStyleClass().add("history-training-row");
                }
            }
        });
    }
    
    @FXML
    private void handleBookTraining() {
        try {
            
            Client client = clientDao.findByUserId(clientId);
            if (client == null) {
                showError("Error", "Client not found", "Unable to find client information.");
                return;
            }
            
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/available-trainings-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Book Training");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(bookTrainingButton.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            
            AvailableTrainingsController controller = loader.getController();
            controller.setClient(client);
            controller.setDialogStage(dialogStage);
            
            
            dialogStage.showAndWait();
            
            
            if (controller.isBookingMade()) {
                loadTrainingSchedules(client);
            }
        } catch (Exception e) {
            log.error("Error opening available trainings dialog: ", e);
            showError("Error", "Could not open available trainings", e.getMessage());
        }
    }
    
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
