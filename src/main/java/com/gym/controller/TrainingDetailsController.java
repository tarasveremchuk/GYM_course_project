package com.gym.controller;

import com.gym.model.Training;
import com.gym.model.Booking;
import com.gym.model.Client;
import com.gym.model.Visit;
import com.gym.model.BookingStatus;
import com.gym.dao.impl.BookingDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.VisitDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;
import java.util.List;

@Slf4j
public class TrainingDetailsController {

    @FXML private Text trainingNameText;
    @FXML private Text descriptionText;
    @FXML private Text trainerText;
    @FXML private Text dateTimeText;
    @FXML private Text durationText;
    @FXML private Text capacityText;
    @FXML private Button addBookingButton;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> clientNameColumn;
    @FXML private TableColumn<Booking, LocalDateTime> bookingTimeColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Void> actionsColumn;
    @FXML private Label totalBookingsLabel;
    @FXML private Label availableSlotsLabel;

    private Training training;
    private final BookingDao bookingDao;
    private final ClientDao clientDao;
    private final VisitDao visitDao;
    private ObservableList<Booking> bookings;

    public TrainingDetailsController() {
        this.bookingDao = new BookingDao();
        this.clientDao = new ClientDao();
        this.visitDao = new VisitDao();
    }

    @FXML
    private void initialize() {
        initializeTable();
    }

    private void initializeTable() {
        bookingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        clientNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getClient().getFullName()
            ));
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        bookingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingTime"));
        bookingTimeColumn.setCellFactory(column -> new TableCell<Booking, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(dateTime.format(formatter));
                }
            }
        });

        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().getValue()
            ));
        statusColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-booked", "status-attended", "status-cancelled");
                } else {
                    setText(status.substring(0, 1).toUpperCase() + status.substring(1));
                    getStyleClass().removeAll("status-booked", "status-attended", "status-cancelled");
                    getStyleClass().add("status-" + status);
                }
            }
        });

        
        actionsColumn.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button checkButton = createActionButton("CHECK", "check-icon");
            private final Button deleteButton = createActionButton("TRASH", "delete-icon");

            {
                checkButton.setOnAction(event -> handleMarkAttended(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleCancelBooking(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableRow().getItem();
                    HBox actions = new HBox(10);
                    
                    if (booking != null && booking.getStatus() == BookingStatus.BOOKED) {
                        if (training.getScheduledAt().isBefore(LocalDateTime.now())) {
                            actions.getChildren().add(checkButton);
                        }
                        actions.getChildren().add(deleteButton);
                    }
                    
                    setGraphic(actions);
                }
            }
        });
    }

    private Button createActionButton(String icon, String styleClass) {
        Button button = new Button();
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
        iconView.getStyleClass().add(styleClass);
        button.setGraphic(iconView);
        button.getStyleClass().add("action-button");
        return button;
    }

    public void setTraining(Training training) {
        this.training = training;
        updateTrainingInfo();
        loadBookings();
    }

    private void updateTrainingInfo() {
        trainingNameText.setText(training.getName());
        descriptionText.setText(training.getDescription());
        trainerText.setText(training.getTrainer().getFullName());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateTimeText.setText(training.getScheduledAt().format(formatter));
        durationText.setText(training.getDurationMinutes() + " minutes");
        
        int bookedCount = bookingDao.getBookedCount(training.getId());
        int availableSlots = training.getCapacity() - bookedCount;
        
        log.info("Training: {}", training.getName());
        log.info("Capacity: {}", training.getCapacity());
        log.info("Booked count: {}", bookedCount);
        log.info("Available slots: {}", availableSlots);
        log.info("Scheduled at: {}", training.getScheduledAt());
        log.info("Is in past: {}", training.getScheduledAt().isBefore(LocalDateTime.now()));
        
        capacityText.setText(bookedCount + "/" + training.getCapacity() + " (" + availableSlots + " available)");
        
        
        boolean isDisabled = availableSlots <= 0 || training.getScheduledAt().isBefore(LocalDateTime.now());
        addBookingButton.setDisable(isDisabled);
        log.info("Add Booking button disabled: {}", isDisabled);
    }

    private void loadBookings() {
        try {
            List<Booking> trainingBookings = bookingDao.findByTrainingId(training.getId());
            bookings = FXCollections.observableArrayList(trainingBookings);
            bookingsTable.setItems(bookings);
            updateStatusLabels();
        } catch (Exception e) {
            log.error("Error loading bookings: ", e);
            showError("Error", "Could not load bookings", e.getMessage());
        }
    }

    private void updateStatusLabels() {
        int total = bookings.size();
        int booked = (int) bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.BOOKED)
            .count();
        int available = training.getCapacity() - booked;

        totalBookingsLabel.setText(String.format("Total Bookings: %d", total));
        availableSlotsLabel.setText(String.format("Available Slots: %d", available));
    }

    @FXML
    private void handleAddBooking() {
        try {
            Dialog<Client> dialog = new Dialog<>();
            dialog.setTitle("Add Booking");
            dialog.setHeaderText("Select Client");

            
            ComboBox<Client> clientComboBox = new ComboBox<>();
            clientComboBox.setPromptText("Select a client");
            clientComboBox.setMaxWidth(Double.MAX_VALUE);
            
            
            List<Client> clients = clientDao.findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
            
            
            clientComboBox.setCellFactory(param -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName());
                    }
                }
            });

            dialog.getDialogPane().setContent(clientComboBox);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return clientComboBox.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(client -> {
                if (client != null) {
                    Booking booking = new Booking();
                    booking.setTraining(training);
                    booking.setClient(client);
                    booking.setBookingTime(LocalDateTime.now());
                    booking.setStatus(BookingStatus.BOOKED);

                    try {
                        Booking savedBooking = bookingDao.save(booking);
                        bookings.add(savedBooking);
                        updateTrainingInfo();
                        updateStatusLabels();
                    } catch (Exception e) {
                        log.error("Error saving booking: ", e);
                        showError("Error", "Could not save booking", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error showing add booking dialog: ", e);
            showError("Error", "Could not show add booking dialog", e.getMessage());
        }
    }

    private void handleMarkAttended(Booking booking) {
        try {
            
            booking.setStatus(BookingStatus.ATTENDED);
            Booking updatedBooking = bookingDao.save(booking);
            int index = bookings.indexOf(booking);
            if (index >= 0) {
                bookings.set(index, updatedBooking);
            }
            
            
            Visit visit = new Visit();
            visit.setClient(booking.getClient());
            visit.setVisitDate(LocalDateTime.now());
            visit.setTrainer(training.getTrainer());
            visit.setNotes("Training: " + training.getName());
            
            try {
                visitDao.save(visit);
                log.info("Created visit record for client {} at training {}", 
                    booking.getClient().getFullName(), training.getName());
            } catch (Exception e) {
                log.error("Error creating visit record: ", e);
                showError("Warning", "Booking marked as attended but visit record failed", 
                    "The booking was marked as attended, but there was an error creating the visit record: " + e.getMessage());
            }
            
            updateStatusLabels();
        } catch (Exception e) {
            log.error("Error marking booking as attended: ", e);
            showError("Error", "Could not update booking status", e.getMessage());
        }
    }

    private void handleCancelBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Booking");
        alert.setContentText("Are you sure you want to cancel this booking?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    
                    bookingDao.findById(booking.getId()).ifPresentOrElse(managedBooking -> {
                        try {
                            managedBooking.setStatus(BookingStatus.CANCELLED);
                            Booking updatedBooking = bookingDao.update(managedBooking);
                            int index = bookings.indexOf(booking);
                            if (index >= 0) {
                                bookings.set(index, updatedBooking);
                            }
                            updateTrainingInfo();
                            updateStatusLabels();
                        } catch (Exception e) {
                            log.error("Error updating booking: ", e);
                            showError("Error", "Could not update booking", e.getMessage());
                        }
                    }, () -> {
                        showError("Error", "Could not find booking", "The booking no longer exists in the database.");
                    });
                } catch (Exception e) {
                    log.error("Error cancelling booking: ", e);
                    showError("Error", "Could not cancel booking", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) trainingNameText.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 