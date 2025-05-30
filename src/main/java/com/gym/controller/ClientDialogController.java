package com.gym.controller;

import com.gym.model.Client;
import com.gym.model.ClientStatus;
import com.gym.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class ClientDialogController {

    @FXML private Text dialogTitle;
    @FXML private ImageView photoPreview;
    @FXML private TextField fullNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea medicalNotesArea;
    @FXML private ComboBox<ClientStatus> statusComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private Client client;
    private boolean isNewClient;
    private byte[] selectedPhoto;
    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        statusComboBox.setItems(javafx.collections.FXCollections.observableArrayList(ClientStatus.values()));
        
        
        fullNameField.textProperty().addListener((observable, oldValue, newValue) -> validateAndUpdateUI());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> validateAndUpdateUI());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateAndUpdateUI());
        
        setDefaultPhoto();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClient(Client client) {
        if (client == null) {
            this.client = new Client();
            this.isNewClient = true;
            dialogTitle.setText("Add New Client");
            statusComboBox.setValue(ClientStatus.ACTIVE);
        } else {
            this.client = client;
            this.isNewClient = false;
            dialogTitle.setText("Edit Client");
            fillFields();
        }
    }

    private void fillFields() {
        try {
            fullNameField.setText(client.getFullName());
            birthDatePicker.setValue(client.getBirthDate());
            phoneField.setText(client.getPhone());
            emailField.setText(client.getEmail());
            medicalNotesArea.setText(client.getMedicalNotes());
            statusComboBox.setValue(client.getStatus());

            
            byte[] photo = client.getPhoto();
            if (photo != null) {
                selectedPhoto = photo;
                displayPhoto(photo);
            }
        } catch (Exception e) {
            log.error("Error filling client fields: ", e);
            showError("Error loading client data: " + e.getMessage());
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
                
                if (selectedFile.length() > 5 * 1024 * 1024) {
                    showError("File is too large. Maximum size is 5MB");
                    return;
                }

                
                selectedPhoto = Files.readAllBytes(selectedFile.toPath());
                
                
                displayPhoto(selectedPhoto);
                log.info("Photo uploaded successfully. Size: {} bytes", selectedPhoto.length);
            } catch (IOException e) {
                log.error("Error loading photo: ", e);
                showError("Error loading photo: " + e.getMessage());
            }
        }
    }

    private void displayPhoto(byte[] photoData) {
        try {
            Image image = new Image(new ByteArrayInputStream(photoData));
            if (image.isError()) {
                throw new Exception("Invalid image data");
            }
            photoPreview.setImage(image);
        } catch (Exception e) {
            log.error("Error displaying photo: ", e);
            showError("Error displaying photo: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                
                if (isNewClient) {
                    
                    client = new Client();
                    
                    client.setFullName(fullNameField.getText());
                    client.setBirthDate(birthDatePicker.getValue());
                    client.setPhone(phoneField.getText());
                    client.setEmail(emailField.getText());
                    client.setMedicalNotes(medicalNotesArea.getText());
                    client.setStatus(statusComboBox.getValue());
                    
                    if (selectedPhoto != null) {
                        client.setPhoto(selectedPhoto);
                    }

                    
                    log.info("Saving new client...");
                    Client savedClient = clientService.saveClient(client, usernameField.getText(), passwordField.getText());
                    if (savedClient == null) {
                        throw new Exception("Failed to save client");
                    }
                    
                    
                    client = savedClient;
                    
                    log.info("Client saved successfully with ID: {}", savedClient.getId());
                } else {
                    
                    client.setFullName(fullNameField.getText());
                    client.setBirthDate(birthDatePicker.getValue());
                    client.setPhone(phoneField.getText());
                    client.setEmail(emailField.getText());
                    client.setMedicalNotes(medicalNotesArea.getText());
                    client.setStatus(statusComboBox.getValue());
                    
                    if (selectedPhoto != null) {
                        client.setPhoto(selectedPhoto);
                    }

                    
                    log.info("Updating existing client...");
                    Client savedClient = clientService.saveClient(client, null, null);
                    if (savedClient == null) {
                        throw new Exception("Failed to update client");
                    }
                    
                    
                    client = savedClient;
                    
                    log.info("Client updated successfully with ID: {}", savedClient.getId());
                }

                dialogStage.close();
            } catch (Exception e) {
                log.error("Error saving client data: ", e);
                showError("Error saving client data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        client = null;
        dialogStage.close();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (fullNameField.getText() == null || fullNameField.getText().isEmpty()) {
            errorMessage.append("Invalid name!\n");
        }
        if (phoneField.getText() == null || !phoneField.getText().matches("\\d{10}")) {
            errorMessage.append("Invalid phone number (must be 10 digits)!\n");
        }
        if (emailField.getText() == null || !emailField.getText().matches("[^@]+@[^@]+\\.[^@]+")) {
            errorMessage.append("Invalid email format!\n");
        }

        if (birthDatePicker.getValue() == null) {
            errorMessage.append("Invalid birth date!\n");
        }
        if (statusComboBox.getValue() == null) {
            errorMessage.append("Please select a status!\n");
        }
        if (isNewClient) {
            if (usernameField.getText() == null || usernameField.getText().isEmpty()) {
                errorMessage.append("Username is required!\n");
            }
            if (passwordField.getText() == null || passwordField.getText().length() < 6) {
                errorMessage.append("Password must be at least 6 characters!\n");
            }
        }

        if (errorMessage.length() > 0) {
            showError(errorMessage.toString());
            return false;
        }
        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void validateAndUpdateUI() {
        if (validateInput()) {
            clearError();
        }
    }

    private void setDefaultPhoto() {
        
    }

    public Client getClient() {
        return client;
    }
} 