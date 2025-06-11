package com.gym.controller;

import com.gym.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import javafx.scene.control.Alert;

@Slf4j
public class AdminDashboardController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Button logoutButton;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    private void handleClientsManagement() {
        try {
            log.info("Opening Clients Management...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/clients-management.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1200);  
            stage.setHeight(800); 
            stage.centerOnScreen(); 
            
            log.info("Opened Clients Management view");
        } catch (Exception e) {
            log.error("Error opening Clients Management view: ", e);
        }
    }

    @FXML
    private void handleStaffManagement() {
        try {
            log.info("Opening Staff Management...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/staff-management.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1200);  
            stage.setHeight(800); 
            stage.centerOnScreen(); 
            
            log.info("Opened Staff Management view");
        } catch (Exception e) {
            log.error("Error opening Staff Management view: ", e);
        }
    }

    @FXML
    private void handleMemberships() {
        try {
            log.info("Opening Memberships...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/memberships-management.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            
            log.info("Opened Memberships Management view");
        } catch (Exception e) {
            log.error("Error opening Memberships view: ", e);
        }
    }

    @FXML
    private void handleTrainingSchedule() {
        try {
            log.info("Opening Training Schedule...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/training-schedule.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
           
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            

            stage.setWidth(1200.0);
            stage.setHeight(800.0);
            
            stage.centerOnScreen();

            
            log.info("Opened Training Schedule view");
        } catch (Exception e) {
            log.error("Error opening Training Schedule view: ", e);
        }
    }

    @FXML
    private void handleReports() {
        log.info("Opening Reports...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/reports.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Gym Reports");
            stage.setScene(new Scene(root));
            
            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/com/gym/styles/reports.css").toExternalForm());
            
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(usernameLabel.getScene().getWindow());
            
            stage.show();
        } catch (IOException e) {
            log.error("Error loading reports view", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Loading Reports");
            alert.setContentText("Could not load the reports view. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/login.fxml"));
            Parent root = loader.load();
            
            double prefWidth = root.prefWidth(-1);
            double prefHeight = root.prefHeight(-1);
            
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            
            stage.setScene(scene);
            
            stage.sizeToScene();
            stage.centerOnScreen();
            
            stage.show();
            
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout: ", e);
        }
    }
} 