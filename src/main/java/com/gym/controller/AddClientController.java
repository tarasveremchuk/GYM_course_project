package com.gym.controller;

import com.gym.model.Client;
import com.gym.model.User;
import com.gym.model.ClientStatus;
import com.gym.dao.impl.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

@Slf4j
public class AddClientController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final UserDao userDao = new UserDao();
    private Stage dialogStage;
    private Client client;
    private ClientsManagementController parentController;

    public void setClientsManagementController(ClientsManagementController controller) {
        this.parentController = controller;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Client getClient() {
        return client;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setPasswordHash(BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt()));
                user.setRole(User.UserRole.client);
                User savedUser = userDao.save(user);

                client = new Client();
                client.setFullName(nameField.getText());
                client.setEmail(emailField.getText());
                client.setPhone(phoneField.getText());
                client.setBirthDate(birthDatePicker.getValue());
                client.setStatus(ClientStatus.ACTIVE); 
                client.setUser(savedUser); 
                
                if (parentController != null) {
                    parentController.refreshClients();
                }
                
                dialogStage.close();
            } catch (Exception e) {
                log.error("Error saving client", e);
                showError("Error", "Could not save client: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        client = null;
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Invalid name!\n";
        }
        if (emailField.getText() == null || !emailField.getText().matches("[^@]+@[^@]+\\.[^@]+")) {
            errorMessage += "Invalid email!\n";
        }
        if (phoneField.getText() == null || !phoneField.getText().matches("\\d{10}")) {
            errorMessage += "Invalid phone number (must be 10 digits)!\n";
        }
        if (birthDatePicker.getValue() == null) {
            errorMessage += "Invalid birth date!\n";
        }
        if (usernameField.getText() == null || usernameField.getText().isEmpty()) {
            errorMessage += "Username is required!\n";
        }
        if (passwordField.getText() == null || passwordField.getText().length() < 6) {
            errorMessage += "Password must be at least 6 characters!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError("Invalid Fields", errorMessage);
            return false;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
