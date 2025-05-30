package com.gym.controller;

import com.gym.model.StaffDTO;
import com.gym.model.StaffRole;
import com.gym.repository.StaffRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class StaffDialogController {

    @FXML private Text dialogTitle;
    @FXML private ImageView photoPreview;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<StaffRole> roleComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField salaryField;
    @FXML private TextField mondayScheduleField;
    @FXML private TextField tuesdayScheduleField;
    @FXML private TextField wednesdayScheduleField;
    @FXML private TextField thursdayScheduleField;
    @FXML private TextField fridayScheduleField;
    @FXML private TextField saturdayScheduleField;
    @FXML private TextField sundayScheduleField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private StaffDTO staff;
    private boolean isNewStaff;
    private String selectedPhotoPath;
    private Stage dialogStage;
    private final StaffRepository staffRepository;

    public StaffDialogController() {
        this.staffRepository = new StaffRepository();
    }

    @FXML
    public void initialize() {
        
        roleComboBox.setItems(javafx.collections.FXCollections.observableArrayList(StaffRole.values()));
        
        
        fullNameField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        
        
        setDefaultPhoto();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
        this.isNewStaff = (staff == null);
        
        if (isNewStaff) {
            log.info("Creating new staff member");
            dialogTitle.setText("Add New Staff Member");
            this.staff = new StaffDTO();
            roleComboBox.setValue(StaffRole.TRAINER);
            log.info("Set default role to: {}", StaffRole.TRAINER);
        } else {
            log.info("Editing existing staff member: {}", staff.getFullName());
            dialogTitle.setText("Edit Staff Member");
            fillFields();
        }
    }

    private void fillFields() {
        fullNameField.setText(staff.getFullName());
        roleComboBox.setValue(staff.getRole());
        phoneField.setText(staff.getPhone());
        emailField.setText(staff.getEmail());
        salaryField.setText(staff.getSalary().toString());
        
        
        mondayScheduleField.setText(staff.getMondaySchedule());
        tuesdayScheduleField.setText(staff.getTuesdaySchedule());
        wednesdayScheduleField.setText(staff.getWednesdaySchedule());
        thursdayScheduleField.setText(staff.getThursdaySchedule());
        fridayScheduleField.setText(staff.getFridaySchedule());
        saturdayScheduleField.setText(staff.getSaturdaySchedule());
        sundayScheduleField.setText(staff.getSundaySchedule());
        
        if (staff.getPhotoUrl() != null) {
            loadStaffPhoto(staff.getPhotoUrl());
        }
    }

    @FXML
    private void handlePhotoUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(dialogStage);
        if (selectedFile != null) {
            try {
                
                Image image = new Image(selectedFile.toURI().toString());
                photoPreview.setImage(image);
                selectedPhotoPath = selectedFile.getAbsolutePath();
            } catch (Exception e) {
                log.error("Error loading photo: ", e);
                errorLabel.setText("Error loading photo");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            
            if (staff == null) {
                staff = new StaffDTO();
            }

            log.info("Saving staff data: fullName={}, role={}", 
                    fullNameField.getText(), 
                    roleComboBox.getValue());

            
            staff.setFullName(getTextSafely(fullNameField));
            staff.setRole(roleComboBox.getValue());
            staff.setPhone(getTextSafely(phoneField));
            staff.setEmail(getTextSafely(emailField));
            staff.setSalary(new BigDecimal(getTextSafely(salaryField)));
            
            
            staff.setMondaySchedule(getTextSafely(mondayScheduleField));
            staff.setTuesdaySchedule(getTextSafely(tuesdayScheduleField));
            staff.setWednesdaySchedule(getTextSafely(wednesdayScheduleField));
            staff.setThursdaySchedule(getTextSafely(thursdayScheduleField));
            staff.setFridaySchedule(getTextSafely(fridayScheduleField));
            staff.setSaturdaySchedule(getTextSafely(saturdayScheduleField));
            staff.setSundaySchedule(getTextSafely(sundayScheduleField));

            
            if (selectedPhotoPath != null) {
                String photoFileName = savePhoto();
                log.info("Saved photo with filename: {}", photoFileName);
                staff.setPhotoUrl(photoFileName);
            }

            
            StaffDTO savedStaff = staffRepository.save(staff);
            if (savedStaff != null) {
                log.info("Successfully saved staff member to database");
                dialogStage.close();
            } else {
                log.error("Failed to save staff member to database");
                errorLabel.setText("Failed to save staff member to database");
            }
        } catch (Exception e) {
            log.error("Error saving staff member: ", e);
            errorLabel.setText("Error saving staff data: " + e.getMessage());
        }
    }

    
    private String getTextSafely(TextField field) {
        return field != null && field.getText() != null ? field.getText().trim() : "";
    }

    @FXML
    private void handleCancel() {
        staff = null;
        dialogStage.close();
    }

    private boolean validateInput() {
        String errorMessage = "";

        if (fullNameField.getText() == null || fullNameField.getText().trim().isEmpty()) {
            errorMessage += "Full name is required\n";
        }

        if (roleComboBox.getValue() == null) {
            errorMessage += "Role must be selected\n";
        }

        if (phoneField.getText() == null || !phoneField.getText().trim().matches("\\+?\\d{10,13}")) {
            errorMessage += "Valid phone number is required (10-13 digits)\n";
        }

        if (emailField.getText() != null && !emailField.getText().trim().isEmpty() &&
            !emailField.getText().trim().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            errorMessage += "Invalid email format\n";
        }

        try {
            if (salaryField.getText() != null && !salaryField.getText().trim().isEmpty()) {
                BigDecimal salary = new BigDecimal(salaryField.getText().trim());
                if (salary.compareTo(BigDecimal.ZERO) < 0) {
                    errorMessage += "Salary cannot be negative\n";
                }
            } else {
                errorMessage += "Salary is required\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Invalid salary format\n";
        }

        errorLabel.setText(errorMessage);
        return errorMessage.isEmpty();
    }

    private String savePhoto() {
        try {
            
            Path photosDir = Paths.get("photos");
            if (!Files.exists(photosDir)) {
                Files.createDirectory(photosDir);
            }

            
            String extension = selectedPhotoPath.substring(selectedPhotoPath.lastIndexOf('.'));
            String fileName = "staff_" + System.currentTimeMillis() + extension;
            Path targetPath = photosDir.resolve(fileName);

            
            Files.copy(Paths.get(selectedPhotoPath), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception e) {
            log.error("Error saving photo: ", e);
            return null;
        }
    }

    private void setDefaultPhoto() {
        try {
            Image defaultPhoto = new Image(getClass().getResourceAsStream("/com/gym/images/default-avatar.png"));
            photoPreview.setImage(defaultPhoto);
        } catch (Exception e) {
            log.error("Error loading default photo: ", e);
        }
    }

    private void loadStaffPhoto(String photoUrl) {
        try {
            Path photoPath = Paths.get("photos", photoUrl);
            if (Files.exists(photoPath)) {
                Image photo = new Image(photoPath.toUri().toString());
                photoPreview.setImage(photo);
            } else {
                setDefaultPhoto();
            }
        } catch (Exception e) {
            log.error("Error loading staff photo: ", e);
            setDefaultPhoto();
        }
    }

    public StaffDTO getStaff() {
        return staff;
    }
} 