package com.gym.controller;

import com.gym.model.Client;
import com.gym.model.ClientStatus;
import com.gym.service.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;

@Slf4j
public class ClientsManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<ClientStatus> statusFilter;

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, String> photoColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, LocalDate> birthDateColumn;

    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private TableColumn<Client, String> emailColumn;

    @FXML
    private TableColumn<Client, ClientStatus> statusColumn;

    @FXML
    private TableColumn<Client, Void> actionsColumn;

    private ObservableList<Client> clients = FXCollections.observableArrayList();
    private FilteredList<Client> filteredClients;
    private final ClientService clientService;

    public ClientsManagementController() {
        this.clientService = new ClientService();
    }

    @FXML
    public void initialize() {
        
        statusFilter.setItems(FXCollections.observableArrayList(ClientStatus.values()));
        
        
        filteredClients = new FilteredList<>(clients, p -> true);
        
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredClients.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                return client.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                       (client.getPhone() != null && client.getPhone().toLowerCase().contains(lowerCaseFilter)) ||
                       (client.getEmail() != null && client.getEmail().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredClients.setPredicate(client -> {
                if (newValue == null) {
                    return true;
                }
                return client.getStatus() == newValue;
            });
        });
        
        
        nameColumn.setCellValueFactory(data -> data.getValue().fullNameProperty());
        birthDateColumn.setCellValueFactory(data -> data.getValue().birthDateProperty());
        phoneColumn.setCellValueFactory(data -> data.getValue().phoneProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        
        
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ClientStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().clear();
                } else {
                    setText(status.toString());
                    getStyleClass().setAll("status-" + status.toString().toLowerCase());
                }
            }
        });
        
        
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = createActionButton("EDIT", "edit-icon");
            private final Button deleteButton = createActionButton("TRASH", "delete-icon");
            private final Button viewButton = createActionButton("SEARCH", "view-icon");
            
            {
                editButton.setOnAction(event -> handleEditClient(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDeleteClient(getTableRow().getItem()));
                viewButton.setOnAction(event -> handleViewClient(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, editButton, deleteButton, viewButton);
                    setGraphic(actions);
                }
            }
        });
        
        
        clientsTable.setItems(filteredClients);
        
        
        loadClients();
    }

    private Button createActionButton(String icon, String styleClass) {
        Button button = new Button();
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
        iconView.getStyleClass().add(styleClass);
        button.setGraphic(iconView);
        button.getStyleClass().add("action-button");
        return button;
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/admin-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();
        } catch (Exception e) {
            log.error("Error returning to dashboard: ", e);
        }
    }

    private void loadClients() {
        try {
            clients.setAll(clientService.getAllClients());
            clientsTable.setItems(filteredClients);
            log.info("Loaded {} clients from database", clients.size());
        } catch (Exception e) {
            log.error("Error loading clients: {}", e.getMessage());
            showErrorAlert("Error", "Database Error", "Could not load clients from database");
        }
    }

    @FXML
    private void handleAddClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/client-dialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Client");
            stage.setScene(new Scene(root));

            ClientDialogController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setClient(null); 

            stage.showAndWait();
            loadClients(); 
        } catch (Exception e) {
            log.error("Error opening add client dialog: ", e);
            showErrorAlert("Error", "Could not open add client dialog", e.getMessage());
        }
    }

    private void handleEditClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/client-dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Edit Client - " + client.getFullName());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            
            stage.setMinWidth(600);
            stage.setMinHeight(800);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            ClientDialogController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setClient(client);
            
            stage.showAndWait();
            
            Client updatedClient = controller.getClient();
            if (updatedClient != null) {
                Client savedClient = clientService.saveClient(updatedClient, null, null);
                if (savedClient != null) {
                    
                    clientService.initializeClientProperties(savedClient);
                    
                    
                    int index = clients.indexOf(client);
                    if (index >= 0) {
                        clients.set(index, savedClient);
                    }
                    log.info("Client updated: {}", savedClient.getFullName());
                    
                    
                    clientsTable.refresh();
                }
            }
        } catch (Exception e) {
            log.error("Error opening edit client dialog: ", e);
            showErrorAlert("Error", "Could not open edit client window", e.getMessage());
        }
    }
    
    private void handleDeleteClient(Client client) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Client");
        alert.setHeaderText("Delete Client and Related Data");
        alert.setContentText("Are you sure you want to delete " + client.getFullName() + "?\n\n" +
                           "This will also delete:\n" +
                           "- All bookings and training records\n" +
                           "- All payment history\n" +
                           "- All visit records\n" +
                           "- All other related data\n\n" +
                           "This action cannot be undone.");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    clientService.deleteClient(client.getId());
                    clients.remove(client);
                    log.info("Client and all related data deleted: {}", client.getFullName());
                } catch (Exception e) {
                    log.error("Error deleting client: {}", e.getMessage());
                    if (e.getMessage() != null && (e.getMessage().contains("active memberships") || e.getMessage().contains("active bookings"))) {
                        showErrorAlert("Delete Failed", 
                                      "Cannot Delete Client", 
                                      e.getMessage());
                    } else {
                        showErrorAlert("Error", "Delete Failed", "Could not delete client from database");
                    }
                }
            }
        });
    }

    private void handleViewClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/client-details-dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Client Details - " + client.getFullName());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            
            stage.setMinWidth(400);
            stage.setMinHeight(600);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            ClientDetailsDialogController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setClient(client);
            
            stage.showAndWait();
            
            log.info("Showing details for client: {}", client.getFullName());
        } catch (Exception e) {
            log.error("Error showing client details: ", e);
            showErrorAlert("Error", "Could not open client details window", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshClients() {
        loadClients();
    }
}