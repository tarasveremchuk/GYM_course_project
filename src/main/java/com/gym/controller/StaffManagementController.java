package com.gym.controller;

import com.gym.model.StaffDTO;
import com.gym.model.StaffRole;
import com.gym.repository.StaffRepository;
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
import java.math.BigDecimal;
import java.util.Optional;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;

@Slf4j
public class StaffManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<StaffRole> roleFilter;
    @FXML private TableView<StaffDTO> staffTable;
    @FXML private TableColumn<StaffDTO, String> photoColumn;
    @FXML private TableColumn<StaffDTO, String> nameColumn;
    @FXML private TableColumn<StaffDTO, StaffRole> roleColumn;
    @FXML private TableColumn<StaffDTO, String> phoneColumn;
    @FXML private TableColumn<StaffDTO, String> emailColumn;
    @FXML private TableColumn<StaffDTO, BigDecimal> salaryColumn;
    @FXML private TableColumn<StaffDTO, String> scheduleColumn;
    @FXML private TableColumn<StaffDTO, Void> actionsColumn;

    private final StaffRepository staffRepository;
    private ObservableList<StaffDTO> staffList;
    private FilteredList<StaffDTO> filteredStaff;

    public StaffManagementController() {
        this.staffRepository = new StaffRepository();
    }

    @FXML
    public void initialize() {
        
        roleFilter.setItems(FXCollections.observableArrayList(StaffRole.values()));
        
        
        staffList = FXCollections.observableArrayList();
        filteredStaff = new FilteredList<>(staffList, p -> true);
        
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStaff.setPredicate(staff -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                return staff.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                       (staff.getPhone() != null && staff.getPhone().toLowerCase().contains(lowerCaseFilter)) ||
                       (staff.getEmail() != null && staff.getEmail().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredStaff.setPredicate(staff -> {
                if (newValue == null) {
                    return true;
                }
                return staff.getRole() == newValue;
            });
        });
        
        
        nameColumn.setCellValueFactory(data -> data.getValue().fullNameProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        phoneColumn.setCellValueFactory(data -> data.getValue().phoneProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        salaryColumn.setCellValueFactory(data -> data.getValue().salaryProperty());
        
        
        scheduleColumn.setCellValueFactory(data -> {
            StaffDTO staff = data.getValue();
            StringBuilder schedule = new StringBuilder();
            if (staff.getMondaySchedule() != null && !staff.getMondaySchedule().isEmpty()) {
                schedule.append("Mon: ").append(staff.getMondaySchedule()).append(", ");
            }
            if (staff.getTuesdaySchedule() != null && !staff.getTuesdaySchedule().isEmpty()) {
                schedule.append("Tue: ").append(staff.getTuesdaySchedule()).append(", ");
            }
            if (staff.getWednesdaySchedule() != null && !staff.getWednesdaySchedule().isEmpty()) {
                schedule.append("Wed: ").append(staff.getWednesdaySchedule()).append(", ");
            }
            if (staff.getThursdaySchedule() != null && !staff.getThursdaySchedule().isEmpty()) {
                schedule.append("Thu: ").append(staff.getThursdaySchedule()).append(", ");
            }
            if (staff.getFridaySchedule() != null && !staff.getFridaySchedule().isEmpty()) {
                schedule.append("Fri: ").append(staff.getFridaySchedule()).append(", ");
            }
            if (staff.getSaturdaySchedule() != null && !staff.getSaturdaySchedule().isEmpty()) {
                schedule.append("Sat: ").append(staff.getSaturdaySchedule()).append(", ");
            }
            if (staff.getSundaySchedule() != null && !staff.getSundaySchedule().isEmpty()) {
                schedule.append("Sun: ").append(staff.getSundaySchedule());
            }
            
            String scheduleStr = schedule.toString();
            if (scheduleStr.endsWith(", ")) {
                scheduleStr = scheduleStr.substring(0, scheduleStr.length() - 2);
            }
            
            return new SimpleStringProperty(scheduleStr);
        });
        
        
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = createActionButton("EDIT", "edit-icon");
            private final Button deleteButton = createActionButton("TRASH", "delete-icon");
            private final Button scheduleButton = createActionButton("CALENDAR", "schedule-icon");
            
            {
                HBox actions = new HBox(10, editButton, deleteButton, scheduleButton);
                
                editButton.setOnAction(event -> handleEditStaff(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDeleteStaff(getTableRow().getItem()));
                scheduleButton.setOnAction(event -> handleViewSchedule(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, editButton, deleteButton, scheduleButton);
                    setGraphic(actions);
                }
            }
        });
        
        
        staffTable.setItems(filteredStaff);
        
        
        loadStaffData();
    }

    private void loadStaffData() {
        try {
            staffList.clear();
            staffList.addAll(staffRepository.findAll());
            log.info("Loaded {} staff members from database", staffList.size());
        } catch (Exception e) {
            log.error("Error loading staff data: ", e);
            showErrorAlert("Error", "Could not load staff data", e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/admin-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            log.error("Error returning to dashboard: ", e);
            showErrorAlert("Error", "Could not return to dashboard", e.getMessage());
        }
    }

    @FXML
    private void handleAddStaff() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/staff-dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Add New Staff Member");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            
            
            stage.setWidth(800);
            stage.setHeight(1000);  
            stage.setResizable(false);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            StaffDialogController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setStaff(null);
            
            stage.showAndWait();
            
            
            loadStaffData();
        } catch (Exception e) {
            log.error("Error opening staff dialog: ", e);
            showErrorAlert("Error", "Could not open staff dialog", e.getMessage());
        }
    }

    private void handleEditStaff(StaffDTO staff) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/staff-dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Edit Staff Member");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            
            
            stage.setWidth(800);
            stage.setHeight(1000);  
            stage.setResizable(false);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            StaffDialogController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setStaff(staff);
            
            stage.showAndWait();
            
            
            loadStaffData();
        } catch (Exception e) {
            log.error("Error opening staff dialog: ", e);
            showErrorAlert("Error", "Could not open staff dialog", e.getMessage());
        }
    }

    private void handleDeleteStaff(StaffDTO staff) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Staff Member");
        alert.setHeaderText("Delete " + staff.getFullName());
        alert.setContentText("Are you sure you want to delete this staff member?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (staffRepository.delete(staff.getId())) {
                    staffList.remove(staff);
                    log.info("Staff member deleted: {}", staff.getFullName());
                }
            } catch (SQLException e) {
                log.error("Error deleting staff member: ", e);
                if (e.getMessage().contains("active trainings")) {
                    showErrorAlert("Cannot Delete Staff Member",
                                  "Staff Member Has Active Trainings",
                                  "This staff member cannot be deleted because they have active trainings assigned to them. " +
                                  "Please reassign or delete their trainings first.");
                } else {
                    showErrorAlert("Error", "Could not delete staff member", e.getMessage());
                }
            } catch (Exception e) {
                log.error("Error deleting staff member: ", e);
                showErrorAlert("Error", "Could not delete staff member", e.getMessage());
            }
        }
    }

    private void handleViewSchedule(StaffDTO staff) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/schedule-dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Schedule - " + staff.getFullName());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchField.getScene().getWindow());
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            ScheduleDialogController controller = loader.getController();
            controller.setStaff(staff);
            
            stage.showAndWait();
            
            
            StaffDTO updatedStaff = staffRepository.findById(staff.getId());
            if (updatedStaff != null) {
                int index = staffList.indexOf(staff);
                if (index >= 0) {
                    staffList.set(index, updatedStaff);
                }
            }
        } catch (Exception e) {
            log.error("Error opening schedule dialog: ", e);
            showErrorAlert("Error", "Could not open schedule window", e.getMessage());
        }
    }

    private Button createActionButton(String icon, String styleClass) {
        Button button = new Button();
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
        iconView.setGlyphSize(16);
        button.setGraphic(iconView);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 