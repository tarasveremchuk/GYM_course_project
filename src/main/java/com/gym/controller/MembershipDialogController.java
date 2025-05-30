package com.gym.controller;

import com.gym.model.Membership;
import com.gym.model.Client;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.MembershipDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.List;

@Slf4j
public class MembershipDialogController {

    @FXML private Text dialogTitle;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Membership.MembershipType> typeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> visitsLeftSpinner;
    @FXML private TextField priceField;
    @FXML private CheckBox isPaidCheckBox;
    @FXML private TextArea commentsArea;
    @FXML private Label errorLabel;

    private Stage dialogStage;
    private Membership membership;
    private boolean isNewMembership;
    private final ClientDao clientDAO;
    private final MembershipDao membershipDao;

    public MembershipDialogController() {
        this.clientDAO = new ClientDao();
        this.membershipDao = new MembershipDao();
    }

    @FXML
    public void initialize() {
        
        typeComboBox.setItems(FXCollections.observableArrayList(Membership.MembershipType.values()));
        
        
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        visitsLeftSpinner.setValueFactory(valueFactory);
        
        
        loadClients();
        
        
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleTypeChange(newVal));
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateEndDate());
        
        
        startDatePicker.setValue(LocalDate.now());
    }

    private void loadClients() {
        try {
            List<Client> clients = clientDAO.findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
            
            
            clientComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getFullName());
                    }
                }
            });
            
            clientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getFullName());
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error loading clients: ", e);
            showError("Error loading clients: " + e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMembership(Membership membership) {
        if (membership == null) {
            this.membership = new Membership();
            this.isNewMembership = true;
            dialogTitle.setText("Add New Membership");
            priceField.setText("");
        } else {
            this.membership = membership;
            this.isNewMembership = false;
            dialogTitle.setText("Edit Membership");
            fillFields();
        }
    }

    private void fillFields() {
        try {
            if (membership.getClient() != null) {
                clientComboBox.setValue(membership.getClient());
            }
            typeComboBox.setValue(membership.getType());
            startDatePicker.setValue(membership.getStartDate());
            endDatePicker.setValue(membership.getEndDate());
            if (membership.getVisitsLeft() != null) {
                visitsLeftSpinner.getValueFactory().setValue(membership.getVisitsLeft());
            }
            if (membership.getPrice() != null) {
                priceField.setText(String.valueOf(membership.getPrice()));
            } else {
                priceField.setText("");
            }
            isPaidCheckBox.setSelected(membership.isPaid());
        } catch (Exception e) {
            log.error("Error filling membership fields: ", e);
            showError("Error loading membership data: " + e.getMessage());
        }
    }

    private void handleTypeChange(Membership.MembershipType type) {
        if (type == null) return;
        
        switch (type) {
            case fixed:
                visitsLeftSpinner.setDisable(true);
                endDatePicker.setDisable(true);
                endDatePicker.setValue(null);
                break;
            case limited:
                visitsLeftSpinner.setDisable(false);
                endDatePicker.setDisable(false);
                break;
            case unlimited:
                visitsLeftSpinner.setDisable(true);
                endDatePicker.setDisable(false);
                break;
        }
    }

    private void updateEndDate() {
        if (typeComboBox.getValue() == Membership.MembershipType.unlimited && 
            startDatePicker.getValue() != null) {
            
            endDatePicker.setValue(startDatePicker.getValue().plusMonths(1));
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            
            Client selectedClient = clientComboBox.getValue();
            if (selectedClient == null) {
                showError("Please select a client");
                return;
            }
            
            membership.setClient(selectedClient);
            membership.setType(typeComboBox.getValue());
            membership.setStartDate(startDatePicker.getValue());
            membership.setEndDate(endDatePicker.getValue());
            membership.setPaid(isPaidCheckBox.isSelected());
            
            
            try {
                String priceText = priceField.getText().trim();
                if (!priceText.isEmpty()) {
                    membership.setPrice(Double.parseDouble(priceText));
                }
            } catch (NumberFormatException e) {
                showError("Invalid price format. Please enter a valid number.");
                return;
            }
            
            
            if (typeComboBox.getValue() == Membership.MembershipType.limited) {
                membership.setVisitsLeft(visitsLeftSpinner.getValue());
            } else {
                membership.setVisitsLeft(null);
            }

            
            membershipDao.save(membership);
            dialogStage.close();
        } catch (Exception e) {
            log.error("Error saving membership: ", e);
            showError("Error saving membership: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        membership = null;
        dialogStage.close();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (clientComboBox.getValue() == null) {
            errorMessage.append("Please select a client\n");
        }

        if (typeComboBox.getValue() == null) {
            errorMessage.append("Please select membership type\n");
        }

        if (startDatePicker.getValue() == null) {
            errorMessage.append("Start date is required\n");
        } else if (startDatePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage.append("Start date cannot be in the past\n");
        }

        if (typeComboBox.getValue() != Membership.MembershipType.fixed) {
            if (endDatePicker.getValue() == null) {
                errorMessage.append("End date is required for this membership type\n");
            } else if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                errorMessage.append("End date cannot be before start date\n");
            }
        }

        if (typeComboBox.getValue() == Membership.MembershipType.limited) {
            if (visitsLeftSpinner.getValue() == null || visitsLeftSpinner.getValue() < 1) {
                errorMessage.append("Number of visits must be greater than 0\n");
            }
        }

        String errors = errorMessage.toString();
        if (!errors.isEmpty()) {
            showError(errors);
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public Membership getMembership() {
        return membership;
    }
} 