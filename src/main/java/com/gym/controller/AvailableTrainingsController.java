package com.gym.controller;

import com.gym.model.Training;
import com.gym.model.Staff;
import com.gym.model.Client;
import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.BookingDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
public class AvailableTrainingsController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Staff> trainerFilter;
    @FXML private TextField searchField;
    @FXML private TableView<Training> trainingsTable;
    @FXML private TableColumn<Training, LocalDateTime> dateColumn;
    @FXML private TableColumn<Training, LocalDateTime> timeColumn;
    @FXML private TableColumn<Training, String> nameColumn;
    @FXML private TableColumn<Training, String> descriptionColumn;
    @FXML private TableColumn<Training, String> trainerColumn;
    @FXML private TableColumn<Training, Integer> durationColumn;
    @FXML private TableColumn<Training, String> availableSlotsColumn;
    @FXML private TableColumn<Training, Void> actionsColumn;
    @FXML private Label statusLabel;
    @FXML private Button closeButton;

    private final TrainingDao trainingDao;
    private final StaffDao staffDao;
    private final BookingDao bookingDao;
    private ObservableList<Training> trainings;
    private FilteredList<Training> filteredTrainings;
    private Client client;
    private Stage dialogStage;
    private boolean bookingMade = false;
    private Map<Long, Integer> trainingBookedCounts = new HashMap<>();
    private Set<Long> clientBookedTrainings = new HashSet<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public AvailableTrainingsController() {
        this.trainingDao = new TrainingDao();
        this.staffDao = new StaffDao();
        this.bookingDao = new BookingDao();
    }

    @FXML
    public void initialize() {
        initializeFilters();
        initializeTable();
        setupSearch();
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            try {
                List<Booking> clientBookings = bookingDao.findByClientId(client.getId());
                clientBookedTrainings = clientBookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.BOOKED)
                    .map(booking -> booking.getTraining().getId())
                    .collect(Collectors.toSet());
                log.info("Loaded {} booked trainings for client {}", clientBookedTrainings.size(), client.getFullName());
            } catch (Exception e) {
                log.error("Error loading client bookings: ", e);
            }
        }
        loadAvailableTrainings();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBookingMade() {
        return bookingMade;
    }

    private void initializeFilters() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        try {
            List<Staff> trainers = staffDao.findTrainers();
            trainerFilter.setItems(FXCollections.observableArrayList(trainers));
            trainerFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

            trainerFilter.setCellFactory(param -> new ListCell<Staff>() {
                @Override
                protected void updateItem(Staff item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName());
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error loading trainers: ", e);
            showError("Error", "Could not load trainers", e.getMessage());
        }
    }

    private void initializeTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        dateColumn.setCellFactory(column -> new TableCell<Training, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(dateTime.format(dateFormatter));
                }
            }
        });

        
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        timeColumn.setCellFactory(column -> new TableCell<Training, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(dateTime.format(timeFormatter));
                }
            }
        });

        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        
        trainerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTrainer().getFullName()
            ));

        
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        durationColumn.setCellFactory(column -> new TableCell<Training, Integer>() {
            @Override
            protected void updateItem(Integer duration, boolean empty) {
                super.updateItem(duration, empty);
                if (empty || duration == null) {
                    setText(null);
                } else {
                    setText(duration + " min");
                }
            }
        });

        
        availableSlotsColumn.setCellFactory(column -> new TableCell<Training, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Training training = getTableRow().getItem();
                    if (training != null) {
                        int bookedCount = trainingBookedCounts.getOrDefault(training.getId(), 0);
                        int availableSlots = training.getCapacity() - bookedCount;
                        setText(availableSlots + "/" + training.getCapacity());
                        
                        
                        if (availableSlots <= 2 && availableSlots > 0) {
                            setStyle("-fx-text-fill: orange;");
                        } else if (availableSlots == 0) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            }
        });

        
        actionsColumn.setCellFactory(column -> new TableCell<Training, Void>() {
            private final Button bookButton = createActionButton("CHECK_CIRCLE", "book-icon", "Book");

            {
                bookButton.setOnAction(event -> handleBookTraining(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Training training = getTableRow().getItem();
                    if (training != null) {
                        int bookedCount = trainingBookedCounts.getOrDefault(training.getId(), 0);
                        int availableSlots = training.getCapacity() - bookedCount;
                        
                        
                        boolean alreadyBooked = isClientAlreadyBooked(training);
                        
                        if (availableSlots > 0 && !alreadyBooked) {
                            setGraphic(bookButton);
                        } else {
                            Button disabledButton = createActionButton("TIMES_CIRCLE", "disabled-icon", 
                                alreadyBooked ? "Booked" : "Full");
                            disabledButton.setDisable(true);
                            setGraphic(disabledButton);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private boolean isClientAlreadyBooked(Training training) {
        if (client == null || training == null) return false;
        return clientBookedTrainings.contains(training.getId());
    }

    private Button createActionButton(String icon, String styleClass, String text) {
        Button button = new Button(text);
        
        
        if (!text.equals("Book")) {
            FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon));
            iconView.getStyleClass().add(styleClass);
            button.setGraphic(iconView);
        }
        
        button.getStyleClass().add("action-button");
        
        
        if (text.equals("Book")) {
            button.setStyle("-fx-background-color: #FFD700;");
        }
        
        return button;
    }

    private void loadAvailableTrainings() {
        try {
            
            LocalDateTime now = LocalDateTime.now();
            List<Training> futureTrainings = trainingDao.findAll().stream()
                .filter(training -> training.getScheduledAt().isAfter(now))
                .collect(Collectors.toList());
            
            
            trainingBookedCounts.clear();
            for (Training training : futureTrainings) {
                try {
                    int bookedCount = bookingDao.getBookedCount(training.getId());
                    trainingBookedCounts.put(training.getId(), bookedCount);
                } catch (Exception e) {
                    log.error("Error getting booked count for training {}: {}", training.getId(), e.getMessage());
                }
            }
            
            trainings = FXCollections.observableArrayList(futureTrainings);
            filteredTrainings = new FilteredList<>(trainings);
            trainingsTable.setItems(filteredTrainings);
            
            updateStatusLabel();
        } catch (Exception e) {
            log.error("Error loading trainings: ", e);
            showError("Error", "Could not load trainings", e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredTrainings.setPredicate(training -> {
            boolean matchesSearch = true;
            boolean matchesDate = true;
            boolean matchesTrainer = true;

            
            String searchText = searchField.getText().toLowerCase();
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = training.getName().toLowerCase().contains(searchText) ||
                              training.getDescription().toLowerCase().contains(searchText) ||
                              training.getTrainer().getFullName().toLowerCase().contains(searchText);
            }

            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                matchesDate = training.getScheduledAt().toLocalDate().equals(selectedDate);
            }

            
            Staff selectedTrainer = trainerFilter.getValue();
            if (selectedTrainer != null) {
                matchesTrainer = training.getTrainer().getId().equals(selectedTrainer.getId());
            }

            return matchesSearch && matchesDate && matchesTrainer;
        });

        updateStatusLabel();
    }

    private void updateStatusLabel() {
        int totalCount = filteredTrainings.size();
        statusLabel.setText(String.format("Showing %d available trainings", totalCount));
    }

    private void handleBookTraining(Training training) {
        if (client == null) {
            showError("Error", "Client not found", "Unable to book training without client information.");
            return;
        }

        try {
            
            int bookedCount = bookingDao.getBookedCount(training.getId());
            int availableSlots = training.getCapacity() - bookedCount;
            
            if (availableSlots <= 0) {
                showError("Error", "Training is full", "There are no available slots for this training.");
                return;
            }
            
            
            if (isClientAlreadyBooked(training)) {
                showError("Error", "Already booked", "You already have a booking for this training.");
                return;
            }

            
            Booking booking = new Booking();
            booking.setTraining(training);
            booking.setClient(client);
            booking.setBookingTime(LocalDateTime.now());
            booking.setStatus(BookingStatus.BOOKED);

            
            Booking savedBooking = bookingDao.save(booking);
            log.info("Created booking ID: {} for client: {} and training: {}", 
                savedBooking.getId(), client.getFullName(), training.getName());
            
            
            clientBookedTrainings.add(training.getId());
            int currentBookedCount = trainingBookedCounts.getOrDefault(training.getId(), 0);
            trainingBookedCounts.put(training.getId(), currentBookedCount + 1);
            
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Successful");
            alert.setHeaderText("Training Booked");
            alert.setContentText(String.format("You have successfully booked the '%s' training on %s at %s.", 
                training.getName(), 
                training.getScheduledAt().format(dateFormatter),
                training.getScheduledAt().format(timeFormatter)));
            alert.showAndWait();
            
            
            bookingMade = true;
            
            
            trainingsTable.refresh();
            
        } catch (Exception e) {
            log.error("Error booking training: ", e);
            showError("Error", "Could not book training", e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
