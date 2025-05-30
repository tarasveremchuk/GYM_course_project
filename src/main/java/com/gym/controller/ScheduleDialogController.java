package com.gym.controller;

import com.gym.model.StaffDTO;
import com.gym.repository.StaffRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ScheduleDialogController {

    @FXML private Text staffNameText;
    @FXML private TextField mondayField;
    @FXML private TextField tuesdayField;
    @FXML private TextField wednesdayField;
    @FXML private TextField thursdayField;
    @FXML private TextField fridayField;
    @FXML private TextField saturdayField;
    @FXML private TextField sundayField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private StaffDTO staff;
    private Map<String, TextField> dayFields;
    private final StaffRepository staffRepository;

    public ScheduleDialogController() {
        this.staffRepository = new StaffRepository();
    }

    @FXML
    public void initialize() {
        
        dayFields = new HashMap<>();
        dayFields.put("Monday", mondayField);
        dayFields.put("Tuesday", tuesdayField);
        dayFields.put("Wednesday", wednesdayField);
        dayFields.put("Thursday", thursdayField);
        dayFields.put("Friday", fridayField);
        dayFields.put("Saturday", saturdayField);
        dayFields.put("Sunday", sundayField);

        
        dayFields.values().forEach(field -> 
            field.textProperty().addListener((observable, oldValue, newValue) -> validateSchedule())
        );
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
        staffNameText.setText(staff.getFullName() + "'s Schedule");
        loadSchedule();
    }

    private void loadSchedule() {
        
        mondayField.setText(staff.getMondaySchedule() != null ? staff.getMondaySchedule() : "");
        tuesdayField.setText(staff.getTuesdaySchedule() != null ? staff.getTuesdaySchedule() : "");
        wednesdayField.setText(staff.getWednesdaySchedule() != null ? staff.getWednesdaySchedule() : "");
        thursdayField.setText(staff.getThursdaySchedule() != null ? staff.getThursdaySchedule() : "");
        fridayField.setText(staff.getFridaySchedule() != null ? staff.getFridaySchedule() : "");
        saturdayField.setText(staff.getSaturdaySchedule() != null ? staff.getSaturdaySchedule() : "");
        sundayField.setText(staff.getSundaySchedule() != null ? staff.getSundaySchedule() : "");
    }

    @FXML
    private void handleSave() {
        if (!validateSchedule()) {
            return;
        }

        try {
            
            staff.setMondaySchedule(getFieldText(mondayField));
            staff.setTuesdaySchedule(getFieldText(tuesdayField));
            staff.setWednesdaySchedule(getFieldText(wednesdayField));
            staff.setThursdaySchedule(getFieldText(thursdayField));
            staff.setFridaySchedule(getFieldText(fridayField));
            staff.setSaturdaySchedule(getFieldText(saturdayField));
            staff.setSundaySchedule(getFieldText(sundayField));
            
            
            StaffDTO updatedStaff = staffRepository.save(staff);
            if (updatedStaff != null) {
                log.info("Schedule updated for staff member: {}", staff.getFullName());
                closeDialog();
            } else {
                log.error("Failed to update schedule in database for staff member: {}", staff.getFullName());
                errorLabel.setText("Failed to save schedule to database");
            }
        } catch (Exception e) {
            log.error("Error saving schedule: ", e);
            errorLabel.setText("Error saving schedule: " + e.getMessage());
        }
    }

    private String getFieldText(TextField field) {
        String text = field.getText();
        return text != null ? text.trim() : "";
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateSchedule() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        for (Map.Entry<String, TextField> entry : dayFields.entrySet()) {
            String time = getFieldText(entry.getValue());
            if (!time.isEmpty() && !isValidTimeFormat(time)) {
                if (errorMessage.length() > 0) {
                    errorMessage.append("\n");
                }
                errorMessage.append("Invalid time format for ").append(entry.getKey())
                          .append(" (Use format: HH:MM-HH:MM)");
                isValid = false;
            }
        }

        errorLabel.setText(errorMessage.toString());
        saveButton.setDisable(!isValid);
        return isValid;
    }

    private boolean isValidTimeFormat(String time) {
        
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
} 