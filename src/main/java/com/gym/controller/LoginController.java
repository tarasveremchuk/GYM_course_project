package com.gym.controller;

import com.gym.dao.impl.UserDao;
import com.gym.model.User;
import com.gym.model.User.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {
        
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
        
        
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> 
            errorLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> 
            errorLabel.setText(""));
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        userDao.findByUsername(username).ifPresentOrElse(
            user -> {
                if (verifyPassword(password, user.getPasswordHash())) {
                    onSuccessfulLogin(user);
                } else {
                    errorLabel.setText("Invalid password");
                }
            },
            () -> errorLabel.setText("User not found")
        );
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private void onSuccessfulLogin(User user) {
        try {
            String fxmlPath;
            
            switch (user.getRole()) {
                case admin:
                    fxmlPath = "/com/gym/view/admin-dashboard.fxml";
                    break;
                case client:
                    fxmlPath = "/com/gym/view/client-dashboard.fxml";
                    break;
                default:
                    log.error("Unknown role: {}", user.getRole());
                    errorLabel.setText("Unknown role: " + user.getRole());
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            
            Object controller = loader.getController();
            if (controller instanceof ClientDashboardController && user.getRole() == UserRole.client) {
                ClientDashboardController clientDashboardController = loader.getController();
                clientDashboardController.initialize(user.getId(), user.getUsername());
            }

            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();
            log.info("User {} logged in successfully with role {}", user.getUsername(), user.getRole());
        } catch (Exception e) {
            log.error("Error loading dashboard for role {}: {}", user.getRole(), e);
            errorLabel.setText("Error loading dashboard");
        }
    }
      
} 