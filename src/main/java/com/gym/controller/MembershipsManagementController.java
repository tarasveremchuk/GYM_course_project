package com.gym.controller;

import com.gym.model.Membership;
import com.gym.service.MembershipService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;

@Slf4j
public class MembershipsManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Membership> membershipsTable;
    @FXML private TableColumn<Membership, String> clientNameColumn;
    @FXML private TableColumn<Membership, Membership.MembershipType> typeColumn;
    @FXML private TableColumn<Membership, LocalDate> startDateColumn;
    @FXML private TableColumn<Membership, LocalDate> endDateColumn;
    @FXML private TableColumn<Membership, Integer> visitsLeftColumn;
    @FXML private TableColumn<Membership, Boolean> isPaidColumn;
    @FXML private TableColumn<Membership, Void> actionsColumn;
    @FXML private Label totalMembershipsLabel;
    @FXML private Label activeMembershipsLabel;
    @FXML private Label unpaidMembershipsLabel;

    private final MembershipService membershipService;
    private ObservableList<Membership> memberships;
    private FilteredList<Membership> filteredMemberships;

    public MembershipsManagementController() {
        this.membershipService = new MembershipService();
    }

    @FXML
    public void initialize() {
        initializeFilters();
        initializeTable();
        loadMemberships();
        setupSearch();
    }

    private void initializeFilters() {
        
        statusFilter.setItems(FXCollections.observableArrayList("All", "Active", "Expired", "Unpaid"));
        statusFilter.setValue("All");
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void initializeTable() {
        
        clientNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClient().getFullName()));
        
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        visitsLeftColumn.setCellValueFactory(new PropertyValueFactory<>("visitsLeft"));
        isPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));

        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        startDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        endDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });

        
        isPaidColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isPaid, boolean empty) {
                super.updateItem(isPaid, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontAwesomeIconView icon = new FontAwesomeIconView(
                        isPaid ? FontAwesomeIcon.CHECK_CIRCLE : FontAwesomeIcon.TIMES_CIRCLE
                    );
                    icon.getStyleClass().add(isPaid ? "paid-icon" : "unpaid-icon");
                    setGraphic(icon);
                }
            }
        });

        
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = createActionButton("EDIT", "edit-icon");
            private final Button deleteButton = createActionButton("TRASH", "delete-icon");
            private final Button payButton = createActionButton("MONEY", "pay-icon");

            {
                editButton.setOnAction(event -> handleEditMembership(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDeleteMembership(getTableRow().getItem()));
                payButton.setOnAction(event -> handlePayMembership(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Membership membership = getTableRow().getItem();
                    HBox actions = new HBox(10);
                    actions.getChildren().addAll(editButton, deleteButton);
                    
                    if (!membership.isPaid()) {
                        actions.getChildren().add(payButton);
                    }
                    
                    setGraphic(actions);
                }
            }
        });
    }

    private Button createActionButton(String icon, String styleClass) {
        Button button = new Button();
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
        iconView.getStyleClass().add(styleClass);
        button.setGraphic(iconView);
        button.getStyleClass().add("action-button");
        return button;
    }

    private void loadMemberships() {
        try {
            memberships = FXCollections.observableArrayList(membershipService.getAllMemberships());
            filteredMemberships = new FilteredList<>(memberships, p -> true);
            membershipsTable.setItems(filteredMemberships);
            updateStatusLabels();
        } catch (Exception e) {
            log.error("Error loading memberships: {}", e.getMessage());
            showError("Error", "Database Error", "Could not load memberships from database");
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredMemberships.setPredicate(membership -> {
            boolean matchesSearch = true;
            boolean matchesStatus = true;

            
            String searchText = searchField.getText().toLowerCase();
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = membership.getClient().getFullName().toLowerCase().contains(searchText);
            }

            
            String status = statusFilter.getValue();
            if (status != null && !status.equals("All")) {
                LocalDate now = LocalDate.now();
                switch (status) {
                    case "Active":
                        if (membership.getType() == Membership.MembershipType.unlimited) {
                            matchesStatus = true;
                        } else if (membership.getType() == Membership.MembershipType.limited) {
                            matchesStatus = membership.getVisitsLeft() > 0;
                        } else {
                            matchesStatus = membership.getEndDate() != null && 
                                          membership.getEndDate().isAfter(now);
                        }
                        break;
                    case "Expired":
                        if (membership.getType() == Membership.MembershipType.unlimited) {
                            matchesStatus = false;
                        } else if (membership.getType() == Membership.MembershipType.limited) {
                            matchesStatus = membership.getVisitsLeft() <= 0;
                        } else {
                            matchesStatus = membership.getEndDate() != null && 
                                          membership.getEndDate().isBefore(now);
                        }
                        break;
                    case "Unpaid":
                        matchesStatus = !membership.isPaid();
                        break;
                }
            }

            return matchesSearch && matchesStatus;
        });

        updateStatusLabels();
    }

    private void updateStatusLabels() {
        int total = memberships.size();
        int active = (int) memberships.stream()
                .filter(m -> {
                    LocalDate now = LocalDate.now();
                    return m.getEndDate().isAfter(now) || m.getEndDate().isEqual(now);
                })
                .count();
        int unpaid = (int) memberships.stream()
                .filter(m -> !m.isPaid())
                .count();
        
        totalMembershipsLabel.setText(String.valueOf(total));
        activeMembershipsLabel.setText(String.valueOf(active));
        unpaidMembershipsLabel.setText(String.valueOf(unpaid));
    }

    @FXML
    private void handleAddMembership() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/membership-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Membership");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(membershipsTable.getScene().getWindow());
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            MembershipDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMembership(null);
            
            dialogStage.showAndWait();
            
            Membership newMembership = controller.getMembership();
            if (newMembership != null) {
                Membership savedMembership = membershipService.saveMembership(newMembership);
                memberships.add(savedMembership);
                updateStatusLabels();
            }
        } catch (Exception e) {
            log.error("Error opening add membership dialog: ", e);
            showError("Error", "Could not open add membership window", e.getMessage());
        }
    }

    private void handleEditMembership(Membership membership) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/membership-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Membership");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(membershipsTable.getScene().getWindow());
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            MembershipDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMembership(membership);
            
            dialogStage.showAndWait();
            
            Membership updatedMembership = controller.getMembership();
            if (updatedMembership != null) {
                try {
                    Membership savedMembership = membershipService.saveMembership(updatedMembership);
                    int index = memberships.indexOf(membership);
                    if (index >= 0) {
                        memberships.set(index, savedMembership);
                    }
                    updateStatusLabels();
                } catch (Exception e) {
                    log.error("Error saving membership: {}", e.getMessage());
                    showError("Error", "Could not save membership", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error opening edit membership dialog: ", e);
            showError("Error", "Could not open edit membership window", e.getMessage());
        }
    }

    private void handleDeleteMembership(Membership membership) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Membership");
        alert.setHeaderText("Delete Membership");
        alert.setContentText("Are you sure you want to delete this membership?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    membershipService.deleteMembership(membership.getId());
                    memberships.remove(membership);
                    updateStatusLabels();
                } catch (Exception e) {
                    log.error("Error deleting membership: ", e);
                    showError("Error", "Could not delete membership", e.getMessage());
                }
            }
        });
    }

    private void handlePayMembership(Membership membership) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mark as Paid");
        alert.setHeaderText("Mark Membership as Paid");
        alert.setContentText("Are you sure you want to mark this membership as paid?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Membership savedMembership = membershipService.markAsPaid(membership.getId());
                    int index = memberships.indexOf(membership);
                    if (index >= 0) {
                        memberships.set(index, savedMembership);
                    }
                    updateStatusLabels();
                } catch (Exception e) {
                    log.error("Error updating payment status: ", e);
                    showError("Error", "Could not update payment status", e.getMessage());
                }
            }
        });
    }



    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/admin-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) membershipsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            log.error("Error returning to dashboard: ", e);
            showError("Error", "Could not return to dashboard", e.getMessage());
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