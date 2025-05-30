package com.gym.dao.impl;

import com.gym.model.*;
import com.gym.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportDaoTest {

    @InjectMocks
    private ReportDao reportDao;

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    @BeforeEach
    void setUp() throws SQLException {
        
        fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        toDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        doNothing().when(mockPreparedStatement).setTimestamp(anyInt(), any(Timestamp.class));
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
    }
    
    @Test
    void testGetClientVisitStats() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("full_name")).thenReturn("John Doe", "Jane Smith");
            when(mockResultSet.getInt("visit_count")).thenReturn(10, 5);
            
            
            Map<Client, Integer> result = reportDao.getClientVisitStats(fromDate, toDate);
            
            
            assertEquals(2, result.size());
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(3)).next();
        }
    }
    
    @Test
    void testGetTrainerStats() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("full_name")).thenReturn("John Smith");
            when(mockResultSet.getInt("total_clients")).thenReturn(15);
            when(mockResultSet.getInt("total_bookings")).thenReturn(30);
            when(mockResultSet.getInt("attended_count")).thenReturn(25);
            when(mockResultSet.getInt("cancelled_count")).thenReturn(5);
            
            
            List<TrainerStats> result = reportDao.getTrainerStats(fromDate, toDate);
            
            
            assertEquals(1, result.size());
            TrainerStats stats = result.get(0);
            assertEquals(1L, stats.getTrainerId());
            assertEquals("John Smith", stats.getTrainerName());
            assertEquals(15, stats.getTotalClients());
            assertEquals(30, stats.getTotalBookings());
            assertEquals(25, stats.getAttendedCount());
            assertEquals(5, stats.getCancelledCount());
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(2)).next();
        }
    }
    
    @Test
    void testGetPaymentStats() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, false, true, false, true, false);
            when(mockResultSet.getString("membership_type")).thenReturn("Monthly");
            when(mockResultSet.getBoolean("is_paid")).thenReturn(true);
            when(mockResultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("100.00"));
            when(mockResultSet.getDate("start_date")).thenReturn(Date.valueOf("2023-01-15"));
            when(mockResultSet.getDate(anyString())).thenReturn(Date.valueOf("2023-01-15"));
            when(mockResultSet.getInt("membership_count")).thenReturn(5);
            
            
            when(mockResultSet.getString("payment_method")).thenReturn("CREDIT_CARD");
            when(mockResultSet.getBigDecimal("total_amount")).thenReturn(new BigDecimal("500.00"));
            when(mockResultSet.getInt("payment_count")).thenReturn(10);
            
            
            PaymentStats result = reportDao.getPaymentStats(fromDate, toDate);
            
            
            assertNotNull(result);
            
            
            verify(mockPreparedStatement, times(2)).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement, times(2)).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement, times(2)).executeQuery();
        }
    }
    
    @Test
    void testGetOutstandingPayments() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getLong("client_id")).thenReturn(1L);
            when(mockResultSet.getString("full_name")).thenReturn("John Doe");
            when(mockResultSet.getString("membership_type")).thenReturn("Annual");
            when(mockResultSet.getDate("start_date")).thenReturn(Date.valueOf("2023-01-01"));
            when(mockResultSet.getDate("end_date")).thenReturn(Date.valueOf("2023-12-31"));
            
            
            List<OutstandingPayment> result = reportDao.getOutstandingPayments();
            
            
            assertEquals(1, result.size());
            OutstandingPayment payment = result.get(0);
            assertEquals(1L, payment.getClientId());
            assertEquals("John Doe", payment.getClientName());
            assertEquals("Annual", payment.getMembershipType());
            assertEquals(LocalDate.of(2023, 1, 1), payment.getStartDate());
            assertEquals(LocalDate.of(2023, 12, 31), payment.getEndDate());
            
            
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(2)).next();
        }
    }
    
    @Test
    void testGetTopTrainings() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("name")).thenReturn("Yoga Class");
            when(mockResultSet.getInt("total_bookings")).thenReturn(20);
            when(mockResultSet.getInt("attended_count")).thenReturn(18);
            when(mockResultSet.getString("trainer_name")).thenReturn("Jane Smith");
            
            
            List<TrainingStats> result = reportDao.getTopTrainings(5, fromDate, toDate);
            
            
            assertEquals(1, result.size());
            TrainingStats stats = result.get(0);
            assertEquals(1L, stats.getTrainingId());
            assertEquals("Yoga Class", stats.getTrainingName());
            assertEquals(20, stats.getTotalBookings());
            assertEquals(18, stats.getAttendedCount());
            assertEquals("Jane Smith", stats.getTrainerName());
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).setInt(3, 5);
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(2)).next();
        }
    }
    
    @Test
    void testGetVisitsByDayOfWeek() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getInt("day_of_week")).thenReturn(1, 3); 
            when(mockResultSet.getInt("visit_count")).thenReturn(15, 20);
            
            
            Map<String, Integer> result = reportDao.getVisitsByDayOfWeek(fromDate, toDate);
            
            
            assertEquals(2, result.size());
            assertEquals(15, result.get("Monday"));
            assertEquals(20, result.get("Wednesday"));
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet, times(3)).next();
        }
    }
    
    @Test
    void testGetClientVisitStats_SQLException() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            
            Map<Client, Integer> result = reportDao.getClientVisitStats(fromDate, toDate);
            
            
            assertTrue(result.isEmpty());
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).executeQuery();
        }
    }
    
    @Test
    void testGetTrainerStats_SQLException() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            
            List<TrainerStats> result = reportDao.getTrainerStats(fromDate, toDate);
            
            
            assertTrue(result.isEmpty());
            
            
            verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(fromDate));
            verify(mockPreparedStatement).setTimestamp(2, Timestamp.valueOf(toDate));
            verify(mockPreparedStatement).executeQuery();
        }
    }
}
