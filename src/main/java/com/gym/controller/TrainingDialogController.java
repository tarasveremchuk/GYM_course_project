package com.gym.controller;

import com.gym.model.Training;
import com.gym.model.Staff;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.TrainingDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class TrainingDialogController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Staff> trainerComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourComboBox;
    @FXML private ComboBox<String> minuteComboBox;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private CheckBox recurringCheckBox;
    @FXML private HBox recurringOptionsBox;
    @FXML private ComboBox<String> recurringTypeComboBox;
    @FXML private Spinner<Integer> recurringCountSpinner;
    @FXML private TextArea notesArea;

    private Stage dialogStage;
    private Training training;
    private boolean saveClicked = false;
    private final StaffDao staffDao;
    private final TrainingDao trainingDao;

    public TrainingDialogController() {
        this.staffDao = new StaffDao();
        this.trainingDao = new TrainingDao();
    }

    @FXML
    private void initialize() {
        setupTimeControls();
        setupSpinners();
        setupRecurringOptions();
        loadTrainers();
    }

    private void setupTimeControls() {
        
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourComboBox.setItems(FXCollections.observableArrayList(hours));

        
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i += 5) {
            minutes.add(String.format("%02d", i));
        }
        minuteComboBox.setItems(FXCollections.observableArrayList(minutes));

        
        LocalTime now = LocalTime.now();
        hourComboBox.setValue(String.format("%02d", now.getHour()));
        minuteComboBox.setValue(String.format("%02d", (now.getMinute() / 5) * 5));
    }

    private void setupSpinners() {
        
        SpinnerValueFactory<Integer> durationFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 240, 60, 15);
        durationSpinner.setValueFactory(durationFactory);
        durationSpinner.setEditable(true);

        
        SpinnerValueFactory<Integer> capacityFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10);
        capacitySpinner.setValueFactory(capacityFactory);
        capacitySpinner.setEditable(true);

        
        SpinnerValueFactory<Integer> recurringFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 52, 4);
        recurringCountSpinner.setValueFactory(recurringFactory);
        recurringCountSpinner.setEditable(true);
    }

    private void setupRecurringOptions() {
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            recurringOptionsBox.setVisible(newVal);
            recurringOptionsBox.setManaged(newVal);
        });

        recurringTypeComboBox.setItems(FXCollections.observableArrayList(
            "Daily", "Weekly", "Monthly"
        ));
        recurringTypeComboBox.setValue("Weekly");
    }

    private void loadTrainers() {
        try {
            List<Staff> trainers = staffDao.findTrainers();
            trainerComboBox.setItems(FXCollections.observableArrayList(trainers));
            
            
            trainerComboBox.setCellFactory(param -> new ListCell<Staff>() {
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
            
            
            trainerComboBox.setButtonCell(new ListCell<Staff>() {
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
        } catch (Exception e) {
            log.error("Error loading trainers: ", e);
            showError("Error", "Could not load trainers", e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTraining(Training training) {
        this.training = training;

        if (training != null) {
            
            nameField.setText(training.getName());
            descriptionArea.setText(training.getDescription());
            trainerComboBox.setValue(training.getTrainer());
            
            LocalDateTime scheduledAt = training.getScheduledAt();
            datePicker.setValue(scheduledAt.toLocalDate());
            hourComboBox.setValue(String.format("%02d", scheduledAt.getHour()));
            minuteComboBox.setValue(String.format("%02d", scheduledAt.getMinute()));
            
            durationSpinner.getValueFactory().setValue(training.getDurationMinutes());
            capacitySpinner.getValueFactory().setValue(training.getCapacity());
        } else {
            
            datePicker.setValue(LocalDate.now());
            durationSpinner.getValueFactory().setValue(60);
            capacitySpinner.getValueFactory().setValue(10);
        }
    }

    public Training getTraining() {
        return saveClicked ? training : null;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (training == null) {
                training = new Training();
            }

            training.setName(nameField.getText());
            training.setDescription(descriptionArea.getText());
            training.setTrainer(trainerComboBox.getValue());
            
            
            LocalDateTime scheduledAt = LocalDateTime.of(
                datePicker.getValue(),
                LocalTime.of(
                    Integer.parseInt(hourComboBox.getValue()),
                    Integer.parseInt(minuteComboBox.getValue())
                )
            );
            training.setScheduledAt(scheduledAt);
            
            training.setDurationMinutes(durationSpinner.getValue());
            training.setCapacity(capacitySpinner.getValue());

            saveClicked = true;
            dialogStage.close();

            
            if (recurringCheckBox.isSelected()) {
                createRecurringTrainings();
            }
        }
    }

    private void createRecurringTrainings() {
        int count = recurringCountSpinner.getValue();
        String type = recurringTypeComboBox.getValue();
        LocalDateTime baseDateTime = training.getScheduledAt();

        for (int i = 1; i < count; i++) {
            Training recurringTraining = new Training();
            recurringTraining.setName(training.getName());
            recurringTraining.setDescription(training.getDescription());
            recurringTraining.setTrainer(training.getTrainer());
            recurringTraining.setDurationMinutes(training.getDurationMinutes());
            recurringTraining.setCapacity(training.getCapacity());

            
            LocalDateTime nextDateTime;
            switch (type) {
                case "Daily":
                    nextDateTime = baseDateTime.plusDays(i);
                    break;
                case "Weekly":
                    nextDateTime = baseDateTime.plusWeeks(i);
                    break;
                case "Monthly":
                    nextDateTime = baseDateTime.plusMonths(i);
                    break;
                default:
                    nextDateTime = baseDateTime;
                    break;
            }

            recurringTraining.setScheduledAt(nextDateTime);
            
            try {
                
                Training savedTraining = trainingDao.save(recurringTraining);
                log.info("Created recurring training: {}", savedTraining.getId());
            } catch (Exception e) {
                log.error("Error creating recurring training: ", e);
                showError("Error", "Could not create recurring training", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Training name is required\n";
        }

        if (trainerComboBox.getValue() == null) {
            errorMessage += "Please select a trainer\n";
        }

        if (datePicker.getValue() == null) {
            errorMessage += "Please select a date\n";
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage += "Training date cannot be in the past\n";
        }

        if (hourComboBox.getValue() == null || minuteComboBox.getValue() == null) {
            errorMessage += "Please select a valid time\n";
        }

        if (durationSpinner.getValue() == null || durationSpinner.getValue() < 15) {
            errorMessage += "Duration must be at least 15 minutes\n";
        }

        if (capacitySpinner.getValue() == null || capacitySpinner.getValue() < 1) {
            errorMessage += "Capacity must be at least 1\n";
        }

        if (recurringCheckBox.isSelected()) {
            if (recurringTypeComboBox.getValue() == null) {
                errorMessage += "Please select a recurring frequency\n";
            }
            if (recurringCountSpinner.getValue() == null || recurringCountSpinner.getValue() < 2) {
                errorMessage += "Number of occurrences must be at least 2\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError("Invalid Input", "Please correct the following errors:", errorMessage);
            return false;
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