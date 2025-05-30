package com.gym.controller;

import com.gym.dao.impl.ReportDao;
import com.gym.model.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class ReportsController {
    
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TabPane reportsTabs;
    
    
    @FXML private TableView<Map.Entry<Client, Integer>> clientVisitsTable;
    @FXML private TableColumn<Map.Entry<Client, Integer>, String> clientNameColumn;
    @FXML private TableColumn<Map.Entry<Client, Integer>, Integer> visitsCountColumn;
    @FXML private BarChart<String, Number> visitsByDayChart;
    
    
    @FXML private TableView<TrainerStats> trainerStatsTable;
    @FXML private TableColumn<TrainerStats, String> trainerNameColumn;
    @FXML private TableColumn<TrainerStats, Integer> totalClientsColumn;
    @FXML private TableColumn<TrainerStats, Integer> totalBookingsColumn;
    @FXML private TableColumn<TrainerStats, Double> attendanceRateColumn;
    
    
    @FXML private VBox paymentsBox;
    @FXML private Label totalAmountLabel;
    @FXML private PieChart paymentMethodsChart;
    @FXML private LineChart<String, Number> dailyPaymentsChart;
    @FXML private TableView<OutstandingPayment> outstandingPaymentsTable;
    @FXML private TableColumn<OutstandingPayment, String> debtorNameColumn;
    @FXML private TableColumn<OutstandingPayment, String> membershipTypeColumn;
    @FXML private TableColumn<OutstandingPayment, LocalDate> startDateColumn;
    @FXML private TableColumn<OutstandingPayment, LocalDate> endDateColumn;
    
    
    @FXML private TableView<TrainingStats> topTrainingsTable;
    @FXML private TableColumn<TrainingStats, String> trainingNameColumn;
    @FXML private TableColumn<TrainingStats, String> trainingTrainerColumn;
    @FXML private TableColumn<TrainingStats, Integer> bookingsCountColumn;
    @FXML private TableColumn<TrainingStats, Double> attendanceRateColumn2;
    
    private final ReportDao reportDao;
    
    public ReportsController() {
        this.reportDao = new ReportDao();
    }
    
    @FXML
    private void initialize() {
        log.info("Initializing Reports Controller...");
        try {
            setupDatePickers();
            log.info("Date pickers setup completed");
            
            setupTables();
            log.info("Tables setup completed");
            
            setupCharts();
            log.info("Charts setup completed");
            
            
            fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> refreshData());
            toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> refreshData());
            log.info("Date picker listeners setup completed");
            
            
            refreshData();
            log.info("Initial data load completed");
        } catch (Exception e) {
            log.error("Error during Reports Controller initialization", e);
            throw e;
        }
    }
    
    private void setupDatePickers() {
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.minusMonths(1));
        toDatePicker.setValue(now);
    }
    
    private void setupTables() {
        
        clientNameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getKey().getFullName()));
        visitsCountColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getValue()).asObject());

        
        clientVisitsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clientNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 70); 
        visitsCountColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30); 

        
        trainerNameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTrainerName()));
        totalClientsColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalClients()).asObject());
        totalBookingsColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalBookings()).asObject());
        attendanceRateColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAttendanceRate()).asObject());

        
        trainerStatsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainerNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 40); 
        totalClientsColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
        totalBookingsColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
        attendanceRateColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
        
        
        debtorNameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getClientName()));
        membershipTypeColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getMembershipType()));
        startDateColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEndDate()));
        
        
        trainingNameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTrainingName()));
        trainingTrainerColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTrainerName()));
        bookingsCountColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalBookings()).asObject());
        attendanceRateColumn2.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAttendanceRate()).asObject());

        
        topTrainingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainingNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 35); 
        trainingTrainerColumn.setMaxWidth(1f * Integer.MAX_VALUE * 35); 
        bookingsCountColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15); 
        attendanceRateColumn2.setMaxWidth(1f * Integer.MAX_VALUE * 15); 
    }
    
    private void setupCharts() {
        visitsByDayChart.setTitle("Visits by Day of Week");
        visitsByDayChart.getXAxis().setLabel("Day");
        visitsByDayChart.getYAxis().setLabel("Number of Visits");
        
        paymentMethodsChart.setTitle("Membership Types Distribution");
        
        dailyPaymentsChart.setTitle("Daily Membership Sales");
        dailyPaymentsChart.getXAxis().setLabel("Date");
        dailyPaymentsChart.getYAxis().setLabel("Number of Memberships");
    }
    
    @FXML
    private void refreshData() {
        LocalDateTime fromDateTime = fromDatePicker.getValue().atStartOfDay();
        LocalDateTime toDateTime = toDatePicker.getValue().atTime(LocalTime.MAX);
        
        refreshVisitsData(fromDateTime, toDateTime);
        refreshTrainerStats(fromDateTime, toDateTime);
        refreshPaymentStats(fromDateTime, toDateTime);
        refreshTopTrainings(fromDateTime, toDateTime);
    }
    
    private void refreshVisitsData(LocalDateTime from, LocalDateTime to) {
        
        Map<Client, Integer> clientVisits = reportDao.getClientVisitStats(from, to);
        clientVisitsTable.setItems(FXCollections.observableArrayList(clientVisits.entrySet()));
        
        
        Map<String, Integer> visitsByDay = reportDao.getVisitsByDayOfWeek(from, to);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Visits");
        visitsByDay.forEach((day, count) -> series.getData().add(new XYChart.Data<>(day, count)));
        visitsByDayChart.getData().clear();
        visitsByDayChart.getData().add(series);
    }
    
    private void refreshTrainerStats(LocalDateTime from, LocalDateTime to) {
        List<TrainerStats> trainerStats = reportDao.getTrainerStats(from, to);
        trainerStatsTable.setItems(FXCollections.observableArrayList(trainerStats));
    }
    
    private void refreshPaymentStats(LocalDateTime from, LocalDateTime to) {
        PaymentStats paymentStats = reportDao.getPaymentStats(from, to);
        
        
        totalAmountLabel.setText(String.format("Total Amount: $%.2f", paymentStats.getTotalAmount()));
        
        
        paymentMethodsChart.getData().clear();
        paymentStats.getTotalMembershipCountsByType().forEach((type, count) -> 
            paymentMethodsChart.getData().add(
                new PieChart.Data(type, count)
            ));
        
        
        Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        
        
        List<LocalDate> sortedDates = new ArrayList<>(paymentStats.getDates());
        Collections.sort(sortedDates); 
        
        for (LocalDate date : sortedDates) {
            Map<String, Integer> typeCounts = paymentStats.getMembershipCountsByType(date);
            
            for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                String membershipType = entry.getKey();
                Integer count = entry.getValue();
                
                
                XYChart.Series<String, Number> typeSeries = seriesMap.computeIfAbsent(membershipType, 
                    k -> {
                        XYChart.Series<String, Number> s = new XYChart.Series<>();
                        s.setName(membershipType);
                        return s;
                    });
                
                
                String dateLabel = date.toString();
                typeSeries.getData().add(new XYChart.Data<>(dateLabel, count));
            }
        }
        
        
        dailyPaymentsChart.getData().clear();
        seriesMap.values().forEach(series -> dailyPaymentsChart.getData().add(series));
        
        
        List<OutstandingPayment> outstandingPayments = reportDao.getOutstandingPayments();
        outstandingPaymentsTable.setItems(FXCollections.observableArrayList(outstandingPayments));
    }
    
    private void refreshTopTrainings(LocalDateTime from, LocalDateTime to) {
        List<TrainingStats> topTrainings = reportDao.getTopTrainings(5, from, to);
        topTrainingsTable.setItems(FXCollections.observableArrayList(topTrainings));
    }
} 