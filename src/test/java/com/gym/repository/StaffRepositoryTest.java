package com.gym.repository;

import com.gym.model.StaffDTO;
import com.gym.model.StaffRole;
import com.gym.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffRepositoryTest {

    @InjectMocks
    private StaffRepository staffRepository;

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    private StaffDTO testStaff;
    
    @BeforeEach
    void setUp() {
        testStaff = new StaffDTO();
        testStaff.setId(1L);
        testStaff.setFullName("John Doe");
        testStaff.setRole(StaffRole.TRAINER);
        testStaff.setPhone("+380501234567");
        testStaff.setEmail("john.doe@example.com");
        testStaff.setSalary(new BigDecimal("1000.00"));
        testStaff.setPhotoUrl("photo_url.jpg");
        testStaff.setMondaySchedule("9:00-18:00");
        testStaff.setTuesdaySchedule("9:00-18:00");
        testStaff.setWednesdaySchedule("9:00-18:00");
        testStaff.setThursdaySchedule("9:00-18:00");
        testStaff.setFridaySchedule("9:00-18:00");
        testStaff.setSaturdaySchedule("10:00-15:00");
        testStaff.setSundaySchedule("Off");
    }
    
    @Test
    void testFindAll() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("full_name")).thenReturn("John Doe", "Jane Smith");
            when(mockResultSet.getString("role")).thenReturn("trainer", "admin");
            when(mockResultSet.getString("phone")).thenReturn("+380501234567", "+380507654321");
            when(mockResultSet.getString("email")).thenReturn("john.doe@example.com", "jane.smith@example.com");
            when(mockResultSet.getBigDecimal("salary")).thenReturn(new BigDecimal("1000.00"), new BigDecimal("1500.00"));
            when(mockResultSet.getString("photo_url")).thenReturn("photo1.jpg", "photo2.jpg");
            when(mockResultSet.getString("monday_schedule")).thenReturn("9:00-18:00", "10:00-19:00");
            when(mockResultSet.getString("tuesday_schedule")).thenReturn("9:00-18:00", "10:00-19:00");
            when(mockResultSet.getString("wednesday_schedule")).thenReturn("9:00-18:00", "10:00-19:00");
            when(mockResultSet.getString("thursday_schedule")).thenReturn("9:00-18:00", "10:00-19:00");
            when(mockResultSet.getString("friday_schedule")).thenReturn("9:00-18:00", "10:00-19:00");
            when(mockResultSet.getString("saturday_schedule")).thenReturn("10:00-15:00", "Off");
            when(mockResultSet.getString("sunday_schedule")).thenReturn("Off", "Off");
            
            List<StaffDTO> staffList = staffRepository.findAll();
            
            assertEquals(2, staffList.size());
            assertEquals("John Doe", staffList.get(0).getFullName());
            assertEquals("Jane Smith", staffList.get(1).getFullName());
            assertEquals(StaffRole.TRAINER, staffList.get(0).getRole());
            assertEquals(StaffRole.ADMIN, staffList.get(1).getRole());
        }
    }
    
    @Test
    void testFindById() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("full_name")).thenReturn("John Doe");
            when(mockResultSet.getString("role")).thenReturn("trainer");
            when(mockResultSet.getString("phone")).thenReturn("+380501234567");
            when(mockResultSet.getString("email")).thenReturn("john.doe@example.com");
            when(mockResultSet.getBigDecimal("salary")).thenReturn(new BigDecimal("1000.00"));
            when(mockResultSet.getString("photo_url")).thenReturn("photo1.jpg");
            when(mockResultSet.getString("monday_schedule")).thenReturn("9:00-18:00");
            when(mockResultSet.getString("tuesday_schedule")).thenReturn("9:00-18:00");
            when(mockResultSet.getString("wednesday_schedule")).thenReturn("9:00-18:00");
            when(mockResultSet.getString("thursday_schedule")).thenReturn("9:00-18:00");
            when(mockResultSet.getString("friday_schedule")).thenReturn("9:00-18:00");
            when(mockResultSet.getString("saturday_schedule")).thenReturn("10:00-15:00");
            when(mockResultSet.getString("sunday_schedule")).thenReturn("Off");
            
            StaffDTO staff = staffRepository.findById(1L);
            
            assertNotNull(staff);
            assertEquals(1L, staff.getId());
            assertEquals("John Doe", staff.getFullName());
            assertEquals(StaffRole.TRAINER, staff.getRole());
            assertEquals("+380501234567", staff.getPhone());
            assertEquals("john.doe@example.com", staff.getEmail());
            assertEquals(new BigDecimal("1000.00"), staff.getSalary());
            
            verify(mockPreparedStatement).setLong(1, 1L);
        }
    }
    
    @Test
    void testFindByIdNotFound() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            StaffDTO staff = staffRepository.findById(999L);
            
            assertNull(staff);
            verify(mockPreparedStatement).setLong(1, 999L);
        }
    }
    
    @Test
    void testSaveInsert() throws SQLException {
        testStaff.setId(null);
        
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            
            StaffDTO savedStaff = staffRepository.save(testStaff);
            
            assertNotNull(savedStaff);
            assertEquals(1L, savedStaff.getId());
            verify(mockPreparedStatement).setString(1, testStaff.getFullName());
            verify(mockPreparedStatement).setString(2, testStaff.getRole().getValue());
            verify(mockPreparedStatement).setString(3, testStaff.getPhone());
            verify(mockPreparedStatement).setString(4, testStaff.getEmail());
            verify(mockPreparedStatement).setBigDecimal(5, testStaff.getSalary());
        }
    }
    
    @Test
    void testSaveUpdate() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            
            StaffDTO updatedStaff = staffRepository.save(testStaff);
            
            assertNotNull(updatedStaff);
            assertEquals(testStaff.getId(), updatedStaff.getId());
            verify(mockPreparedStatement).setString(1, testStaff.getFullName());
            verify(mockPreparedStatement).setString(2, testStaff.getRole().getValue());
            verify(mockPreparedStatement).setString(3, testStaff.getPhone());
            verify(mockPreparedStatement).setString(4, testStaff.getEmail());
            verify(mockPreparedStatement).setBigDecimal(5, testStaff.getSalary());
            verify(mockPreparedStatement).setLong(14, testStaff.getId());
        }
    }
    
    @Test
    void testHasActiveTrainings() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(2);
            
            boolean hasTrainings = staffRepository.hasActiveTrainings(1L);
            
            assertTrue(hasTrainings);
            verify(mockPreparedStatement).setLong(1, 1L);
        }
    }
    
    @Test
    void testHasNoActiveTrainings() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(0);
            
            boolean hasTrainings = staffRepository.hasActiveTrainings(1L);
            
            assertFalse(hasTrainings);
            verify(mockPreparedStatement).setLong(1, 1L);
        }
    }
    
    @Test
    void testDeleteSuccess() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            
            StaffRepository spyRepository = spy(staffRepository);
            doReturn(false).when(spyRepository).hasActiveTrainings(1L);
            
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            
            boolean deleted = spyRepository.delete(1L);
            
            assertTrue(deleted);
            verify(mockPreparedStatement).setLong(1, 1L);
        }
    }
    
    @Test
    void testDeleteWithActiveTrainings() {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            StaffRepository spyRepository = spy(staffRepository);
            doReturn(true).when(spyRepository).hasActiveTrainings(1L);
            
            assertThrows(SQLException.class, () -> spyRepository.delete(1L));
        }
    }
    
    @Test
    void testFindAllSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            List<StaffDTO> staffList = staffRepository.findAll();
            
            assertTrue(staffList.isEmpty());
        }
    }
    
    @Test
    void testFindByIdSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            StaffDTO staff = staffRepository.findById(1L);
            
            assertNull(staff);
        }
    }
    
    @Test
    void testInsertSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            testStaff.setId(null); 
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            StaffDTO result = staffRepository.save(testStaff);
            
            assertNull(result);
        }
    }
    
    @Test
    void testUpdateSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            StaffDTO result = staffRepository.save(testStaff); 
            
            assertNull(result);
        }
    }
    
    @Test
    void testUpdateNoRowsAffected() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0); 
            
            StaffDTO result = staffRepository.save(testStaff); 
            
            assertNull(result);
        }
    }
    
    @Test
    void testInsertNoResultReturned() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            testStaff.setId(null); 
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); 
            
            StaffDTO result = staffRepository.save(testStaff);
            
            assertNull(result);
        }
    }
    
    @Test
    void testHasActiveTrainingsNoResult() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); 
            
            boolean hasTrainings = staffRepository.hasActiveTrainings(1L);
            
            assertFalse(hasTrainings);
        }
    }
    
    @Test
    void testHasActiveTrainingsSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            boolean hasTrainings = staffRepository.hasActiveTrainings(1L);
            
            assertFalse(hasTrainings); 
        }
    }
    
    @Test
    void testDeleteNoRowsAffected() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            
            StaffRepository spyRepository = spy(staffRepository);
            doReturn(false).when(spyRepository).hasActiveTrainings(1L);
            
            when(mockPreparedStatement.executeUpdate()).thenReturn(0); 
            
            boolean deleted = spyRepository.delete(1L);
            
            assertFalse(deleted);
        }
    }
    
    @Test
    void testDeleteSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            
            StaffRepository spyRepository = spy(staffRepository);
            doReturn(false).when(spyRepository).hasActiveTrainings(1L);
            
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
            
            assertThrows(SQLException.class, () -> spyRepository.delete(1L));
        }
    }
}
