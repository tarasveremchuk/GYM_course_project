package com.gym.controller;

import com.gym.model.Training;
import com.gym.model.Staff;
import com.gym.model.Booking;
import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.BookingDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;
import java.util.List;

@Slf4j
public class TrainingScheduleController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Staff> trainerFilter;
    @FXML private TextField searchField;
    @FXML private TableView<Training> trainingsTable;
    @FXML private TableColumn<Training, String> nameColumn;
    @FXML private TableColumn<Training, String> descriptionColumn;
    @FXML private TableColumn<Training, String> trainerColumn;
    @FXML private TableColumn<Training, LocalDateTime> dateTimeColumn;
    @FXML private TableColumn<Training, Integer> durationColumn;
    @FXML private TableColumn<Training, String> capacityColumn;
    @FXML private TableColumn<Training, String> statusColumn;
    @FXML private TableColumn<Training, Void> actionsColumn;
    @FXML private Label totalTrainingsLabel;
    @FXML private Label upcomingTrainingsLabel;
    @FXML private Label todayTrainingsLabel;

    private final TrainingDao trainingDao;
    private final StaffDao staffDao;
    private final BookingDao bookingDao;
    private ObservableList<Training> trainings;
    private FilteredList<Training> filteredTrainings;

    public TrainingScheduleController() {
        this.trainingDao = new TrainingDao();
        this.staffDao = new StaffDao();
        this.bookingDao = new BookingDao();
    }

    @FXML
    public void initialize() {
        initializeFilters();
        initializeTable();
        loadTrainings();
        setupSearch();
    }

    private void initializeFilters() {
        
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        
        List<Staff> trainers = staffDao.findTrainers();
        trainerFilter.setItems(FXCollections.observableArrayList(trainers));
        trainerFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        
        trainerFilter.setCellFactory(param -> new ListCell<Staff>() {
            @Override
            protected void updateItem(Staff item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
    }

    private void initializeTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        trainerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTrainer().getFullName()
            ));
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        dateTimeColumn.setCellFactory(column -> new TableCell<Training, LocalDateTime>() {
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

        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        durationColumn.setCellFactory(column -> new TableCell<Training, Integer>() {
            @Override
            protected void updateItem(Integer duration, boolean empty) {
                super.updateItem(duration, empty);
                if (empty || duration == null) {
                    setText(null);
                } else {
                    setText(duration + " min");
                }
            }
        });

        
        capacityColumn.setCellFactory(column -> new TableCell<Training, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Training training = getTableRow().getItem();
                    if (training != null) {
                        int bookedCount = bookingDao.getBookedCount(training.getId());
                        int availableSlots = training.getCapacity() - bookedCount;
                        setText(availableSlots + "/" + training.getCapacity());
                    }
                }
            }
        });

        
        statusColumn.setCellFactory(column -> new TableCell<Training, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Training training = getTableRow().getItem();
                    if (training != null) {
                        LocalDateTime now = LocalDateTime.now();
                        if (training.getScheduledAt().isBefore(now)) {
                            setText("Completed");
                            getStyleClass().add("status-completed");
                        } else {
                            setText("Active");
                            getStyleClass().add("status-active");
                        }
                    }
                }
            }
        });

        
        actionsColumn.setCellFactory(column -> new TableCell<Training, Void>() {
            private final Button detailsButton = createActionButton("INFO_CIRCLE", "info-icon");
            private final Button editButton = createActionButton("EDIT", "edit-icon");
            private final Button deleteButton = createActionButton("TRASH", "delete-icon");

            {
                detailsButton.setOnAction(event -> handleViewDetails(getTableRow().getItem()));
                editButton.setOnAction(event -> handleEditTraining(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDeleteTraining(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10);
                    actions.getChildren().addAll(detailsButton, editButton, deleteButton);
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

    private void loadTrainings() {
        try {
            trainings = FXCollections.observableArrayList(trainingDao.findAll());
            filteredTrainings = new FilteredList<>(trainings);
            trainingsTable.setItems(filteredTrainings);
            updateStatusLabels();
        } catch (Exception e) {
            log.error("Error loading trainings: ", e);
            showError("Error", "Could not load trainings", e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredTrainings.setPredicate(training -> {
            boolean matchesSearch = true;
            boolean matchesDate = true;
            boolean matchesTrainer = true;

            
            String searchText = searchField.getText().toLowerCase();
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = training.getName().toLowerCase().contains(searchText) ||
                              training.getDescription().toLowerCase().contains(searchText) ||
                              training.getTrainer().getFullName().toLowerCase().contains(searchText);
            }

            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                matchesDate = training.getScheduledAt().toLocalDate().equals(selectedDate);
            }

            
            Staff selectedTrainer = trainerFilter.getValue();
            if (selectedTrainer != null) {
                matchesTrainer = training.getTrainer().getId().equals(selectedTrainer.getId());
            }

            return matchesSearch && matchesDate && matchesTrainer;
        });

        updateStatusLabels();
    }

    private void updateStatusLabels() {
        int total = trainings.size();
        long upcoming = trainings.stream()
            .filter(t -> t.getScheduledAt().isAfter(LocalDateTime.now()))
            .count();
        long today = trainings.stream()
            .filter(t -> t.getScheduledAt().toLocalDate().equals(LocalDate.now()))
            .count();

        totalTrainingsLabel.setText(String.format("Total: %d", total));
        upcomingTrainingsLabel.setText(String.format("Upcoming: %d", upcoming));
        todayTrainingsLabel.setText(String.format("Today: %d", today));
    }

    @FXML
    private void handleAddTraining() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/training-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Training");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(trainingsTable.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            TrainingDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTraining(null);
            
            dialogStage.showAndWait();
            
            Training newTraining = controller.getTraining();
            if (newTraining != null) {
                Training savedTraining = trainingDao.save(newTraining);
                trainings.add(savedTraining);
                updateStatusLabels();
            }
        } catch (Exception e) {
            log.error("Error opening add training dialog: ", e);
            showError("Error", "Could not open add training window", e.getMessage());
        }
    }

    private void handleViewDetails(Training training) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/training-details.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Training Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(trainingsTable.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            TrainingDetailsController controller = loader.getController();
            controller.setTraining(training);
            
            dialogStage.showAndWait();
        } catch (Exception e) {
            log.error("Error opening training details: ", e);
            showError("Error", "Could not open training details", e.getMessage());
        }
    }

    private void handleEditTraining(Training training) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/training-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Training");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(trainingsTable.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            TrainingDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTraining(training);
            
            dialogStage.showAndWait();
            
            Training updatedTraining = controller.getTraining();
            if (updatedTraining != null) {
                Training savedTraining = trainingDao.save(updatedTraining);
                int index = trainings.indexOf(training);
                if (index >= 0) {
                    trainings.set(index, savedTraining);
                }
                updateStatusLabels();
            }
        } catch (Exception e) {
            log.error("Error opening edit training dialog: ", e);
            showError("Error", "Could not open edit training window", e.getMessage());
        }
    }

    private void handleDeleteTraining(Training training) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Training");
        alert.setHeaderText("Delete Training");
        alert.setContentText("Are you sure you want to delete this training? All related bookings will be cancelled.");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    
                    bookingDao.cancelBookingsForTraining(training.getId());
                    
                    
                    trainingDao.delete(training);
                    trainings.remove(training);
                    updateStatusLabels();
                } catch (Exception e) {
                    log.error("Error deleting training: ", e);
                    showError("Error", "Could not delete training", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/admin-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) trainingsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            log.error("Error returning to dashboard: ", e);
            showError("Error", "Could not return to dashboard", e.getMessage());
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