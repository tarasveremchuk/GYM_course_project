package com.gym.service;

import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.UserDao;
import com.gym.model.Staff;
import com.gym.model.StaffRole;
import com.gym.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    @Mock
    private StaffDao staffDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        testStaff = new Staff();
        testStaff.setId(1L);
        testStaff.setFullName("Test Trainer");
        testStaff.setRole(StaffRole.TRAINER);
        testStaff.setEmail("trainer@gym.com");
        testStaff.setPhone("+1234567890");
        testStaff.setSalary(new BigDecimal("2500.00"));
    }

    @Test
    void getAllStaff_ShouldReturnAllStaff() {
        
        List<Staff> expectedStaff = Arrays.asList(testStaff, new Staff());
        when(staffDao.findAll()).thenReturn(expectedStaff);

        
        List<Staff> actualStaff = staffService.getAllStaff();

        
        assertEquals(expectedStaff.size(), actualStaff.size());
        assertEquals(expectedStaff, actualStaff);
        verify(staffDao).findAll();
    }

    @Test
    void getActiveTrainers_ShouldReturnOnlyActiveTrainers() {
        
        List<Staff> expectedTrainers = Arrays.asList(testStaff);
        when(staffDao.findActiveTrainers()).thenReturn(expectedTrainers);

        
        List<Staff> actualTrainers = staffService.getActiveTrainers();

        
        assertEquals(expectedTrainers.size(), actualTrainers.size());
        assertEquals(expectedTrainers, actualTrainers);
        verify(staffDao).findActiveTrainers();
    }

    @Test
    void getTrainerByClientId_WhenTrainerExists_ShouldReturnTrainer() {
        
        when(staffDao.findTrainerByClientId(anyLong())).thenReturn(Optional.of(testStaff));

        
        Optional<Staff> result = staffService.getTrainerByClientId(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testStaff, result.get());
        verify(staffDao).findTrainerByClientId(1L);
    }

    @Test
    void getTrainerByClientId_WhenTrainerDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(staffDao.findTrainerByClientId(anyLong())).thenReturn(Optional.empty());

        
        Optional<Staff> result = staffService.getTrainerByClientId(999L);

        
        assertFalse(result.isPresent());
        verify(staffDao).findTrainerByClientId(999L);
    }

    @Test
    void getAllTrainers_ShouldReturnAllTrainers() {
        
        List<Staff> expectedTrainers = Arrays.asList(testStaff);
        when(staffDao.findTrainers()).thenReturn(expectedTrainers);

        
        List<Staff> actualTrainers = staffService.getAllTrainers();

        
        assertEquals(expectedTrainers.size(), actualTrainers.size());
        assertEquals(expectedTrainers, actualTrainers);
        verify(staffDao).findTrainers();
    }

    @Test
    void getStaffById_WhenStaffExists_ShouldReturnStaff() {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testStaff));

        
        Optional<Staff> result = staffService.getStaffById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testStaff, result.get());
        verify(staffDao).findById(1L);
    }

    @Test
    void getStaffById_WhenStaffDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<Staff> result = staffService.getStaffById(999L);

        
        assertFalse(result.isPresent());
        verify(staffDao).findById(999L);
    }

    @Test
    void saveStaff_NewStaff_ShouldSaveAndReturnStaff() throws Exception {
        
        testStaff.setId(null); 
        when(staffDao.save(any(Staff.class))).thenReturn(testStaff);
        
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("trainer");
        mockUser.setRole(User.UserRole.trainer);
        when(userDao.save(any(User.class))).thenReturn(mockUser);

        
        Staff result = staffService.saveStaff(testStaff, "trainer", "password");

        
        assertNotNull(result);
        assertEquals(testStaff, result);
        verify(staffDao).save(testStaff);
        verify(userDao).save(any(User.class)); 
    }

    @Test
    void saveStaff_ExistingStaff_ShouldUpdateAndReturnStaff() throws Exception {
        
        when(staffDao.save(any(Staff.class))).thenReturn(testStaff);

        
        Staff result = staffService.saveStaff(testStaff, null, null);

        
        assertNotNull(result);
        assertEquals(testStaff, result);
        verify(staffDao).save(testStaff);
        verify(userDao, never()).save(any(User.class)); 
    }

    @Test
    void saveStaff_WhenSaveFails_ShouldThrowException() {
        
        when(staffDao.save(any(Staff.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            staffService.saveStaff(testStaff, null, null);
        });
        
        assertEquals("Failed to update staff member", exception.getMessage());
        verify(staffDao).save(any(Staff.class));
    }

    @Test
    void updateStaffStatus_WhenStaffExists_ShouldUpdateAndReturnStaff() throws Exception {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testStaff));
        when(staffDao.save(any(Staff.class))).thenReturn(testStaff);

        
        Staff result = staffService.updateStaffStatus(1L, "On vacation");

        
        assertNotNull(result);
        assertEquals(testStaff, result);
        verify(staffDao).findById(1L);
        verify(staffDao).save(testStaff);
    }

    @Test
    void updateStaffStatus_WhenStaffDoesNotExist_ShouldThrowException() {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            staffService.updateStaffStatus(999L, "On vacation");
        });
        
        assertEquals("Staff member not found", exception.getMessage());
        verify(staffDao).findById(999L);
        verify(staffDao, never()).save(any(Staff.class));
    }

    @Test
    void deleteStaff_WhenStaffExists_ShouldDeleteStaff() throws Exception {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testStaff));
        doNothing().when(staffDao).deleteById(anyLong());

        
        staffService.deleteStaff(1L);

        
        verify(staffDao).findById(1L);
        verify(staffDao).deleteById(1L);
    }

    @Test
    void deleteStaff_WhenStaffDoesNotExist_ShouldThrowException() {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            staffService.deleteStaff(999L);
        });
        
        assertEquals("Staff member not found", exception.getMessage());
        verify(staffDao).findById(999L);
        verify(staffDao, never()).deleteById(anyLong());
    }
    
    @Test
    void deleteStaff_WhenDeleteFails_ShouldThrowException() {
        
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testStaff));
        doThrow(new RuntimeException("Database error")).when(staffDao).deleteById(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            staffService.deleteStaff(1L);
        });
        
        assertTrue(exception.getMessage().contains("Failed to delete staff member"));
        verify(staffDao).findById(1L);
        verify(staffDao).deleteById(1L);
    }

    @Test
    void searchStaff_ShouldReturnMatchingStaff() {
        
        String query = "Test";
        List<Staff> expectedStaff = Arrays.asList(testStaff);
        when(staffDao.searchStaff(query)).thenReturn(expectedStaff);

        
        List<Staff> result = staffService.searchStaff(query);

        
        assertEquals(expectedStaff.size(), result.size());
        assertEquals(expectedStaff, result);
        verify(staffDao).searchStaff(query);
    }
}
