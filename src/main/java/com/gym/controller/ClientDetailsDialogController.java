package com.gym.controller;

import com.gym.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ClientDetailsDialogController {
    @FXML private Text dialogTitle;
    @FXML private ImageView photoPreview;
    @FXML private Label fullNameLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private TextArea medicalNotesArea;
    @FXML private Label statusLabel;
    @FXML private Button closeButton;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClient(Client client) {
        try {
            
            fullNameLabel.setText(client.getFullName());
            
            if (client.getBirthDate() != null) {
                birthDateLabel.setText(client.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                birthDateLabel.setText("Not specified");
            }
            
            phoneLabel.setText(client.getPhone() != null ? client.getPhone() : "Not specified");
            emailLabel.setText(client.getEmail() != null ? client.getEmail() : "Not specified");
            medicalNotesArea.setText(client.getMedicalNotes() != null ? client.getMedicalNotes() : "No medical notes");
            statusLabel.setText(client.getStatus().toString());
            
            
            byte[] photo = client.getPhoto();
            if (photo != null && photo.length > 0) {
                Image image = new Image(new ByteArrayInputStream(photo));
                photoPreview.setImage(image);
            } else {
                
                photoPreview.setImage(new Image(getClass().getResourceAsStream("/com/gym/images/default-profile.png")));
            }
            
            log.info("Client details loaded successfully for: {}", client.getFullName());
        } catch (Exception e) {
            log.error("Error loading client details: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
} 