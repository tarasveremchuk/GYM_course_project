package com.gym.service;

import com.gym.dao.impl.VisitDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.BookingDao;
import com.gym.model.Visit;
import com.gym.model.Client;
import com.gym.model.Staff;
import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {

    @Mock
    private VisitDao visitDao;
    
    @Mock
    private ClientDao clientDao;
    
    @Mock
    private StaffDao staffDao;
    
    @Mock
    private BookingDao bookingDao;

    @InjectMocks
    private VisitService visitService;

    private Visit testVisit;
    private Client testClient;
    private Staff testTrainer;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Test Client");
        
        
        testTrainer = new Staff();
        testTrainer.setId(1L);
        testTrainer.setFullName("Test Trainer");
        
        
        Training testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setName("Test Training");
        testTraining.setCapacity(10);
        testTraining.setTrainer(testTrainer);
        testTraining.setScheduledAt(LocalDateTime.now());
        testTraining.setDurationMinutes(60);
        
        
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setClient(testClient);
        testBooking.setStatus(BookingStatus.BOOKED);
        testBooking.setTraining(testTraining);
        
        
        testVisit = new Visit();
        testVisit.setId(1L);
        testVisit.setClient(testClient);
        testVisit.setTrainer(testTrainer);
        testVisit.setVisitDate(LocalDateTime.now());
        testVisit.setNotes("Test visit");
    }

    @Test
    void getAllVisits_ShouldReturnAllVisits() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit, new Visit());
        when(visitDao.findAll()).thenReturn(expectedVisits);

        
        List<Visit> actualVisits = visitService.getAllVisits();

        
        assertEquals(expectedVisits.size(), actualVisits.size());
        assertEquals(expectedVisits, actualVisits);
        verify(visitDao).findAll();
    }

    @Test
    void getVisitById_WhenVisitExists_ShouldReturnVisit() {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.of(testVisit));

        
        Optional<Visit> result = visitService.getVisitById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testVisit, result.get());
        verify(visitDao).findById(1L);
    }

    @Test
    void getVisitById_WhenVisitDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<Visit> result = visitService.getVisitById(999L);

        
        assertFalse(result.isPresent());
        verify(visitDao).findById(999L);
    }

    @Test
    void getVisitsByClientId_ShouldReturnClientVisits() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(visitDao.findByClientId(anyLong())).thenReturn(expectedVisits);

        
        List<Visit> result = visitService.getVisitsByClientId(1L);

        
        assertEquals(expectedVisits.size(), result.size());
        assertEquals(expectedVisits, result);
        verify(visitDao).findByClientId(1L);
    }

    @Test
    void getVisitByBookingId_WhenVisitExists_ShouldReturnVisit() {
        
        when(visitDao.findByBookingId(anyLong())).thenReturn(Optional.of(testVisit));

        
        Optional<Visit> result = visitService.getVisitByBookingId(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testVisit, result.get());
        verify(visitDao).findByBookingId(1L);
    }

    @Test
    void getVisitByBookingId_WhenVisitDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(visitDao.findByBookingId(anyLong())).thenReturn(Optional.empty());

        
        Optional<Visit> result = visitService.getVisitByBookingId(999L);

        
        assertFalse(result.isPresent());
        verify(visitDao).findByBookingId(999L);
    }

    @Test
    void getVisitsByMonth_ShouldReturnVisitsForMonth() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(visitDao.findVisitsByMonth(anyInt(), anyInt())).thenReturn(expectedVisits);

        
        List<Visit> result = visitService.getVisitsByMonth(2025, 5);

        
        assertEquals(expectedVisits.size(), result.size());
        assertEquals(expectedVisits, result);
        verify(visitDao).findVisitsByMonth(2025, 5);
    }

    @Test
    void getRecentVisits_ShouldReturnRecentVisits() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(visitDao.findRecentVisits(anyInt())).thenReturn(expectedVisits);

        
        List<Visit> result = visitService.getRecentVisits(10);

        
        assertEquals(expectedVisits.size(), result.size());
        assertEquals(expectedVisits, result);
        verify(visitDao).findRecentVisits(10);
    }

    @Test
    void recordVisit_ShouldSaveAndReturnVisit() throws Exception {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testTrainer));
        when(visitDao.save(any(Visit.class))).thenReturn(testVisit);

        
        Visit result = visitService.recordVisit(1L, 1L, "Test notes");

        
        assertNotNull(result);
        assertEquals(testVisit, result);
        verify(clientDao).findById(1L);
        verify(staffDao).findById(1L);
        verify(visitDao).save(any(Visit.class));
    }

    @Test
    void recordVisit_WhenClientNotFound_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.recordVisit(999L, 1L, "Test notes");
        });
        
        assertEquals("Client not found", exception.getMessage());
        verify(clientDao).findById(999L);
        verify(visitDao, never()).save(any(Visit.class));
    }

    @Test
    void recordVisit_WhenSaveFails_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testTrainer));
        when(visitDao.save(any(Visit.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.recordVisit(1L, 1L, "Test notes");
        });
        
        assertEquals("Failed to save visit", exception.getMessage());
        verify(clientDao).findById(1L);
        verify(staffDao).findById(1L);
        verify(visitDao).save(any(Visit.class));
    }

    @Test
    void recordVisitForBooking_ShouldSaveAndReturnVisit() throws Exception {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(visitDao.findByBookingId(anyLong())).thenReturn(Optional.empty());
        when(visitDao.save(any(Visit.class))).thenReturn(testVisit);

        
        Visit result = visitService.recordVisitForBooking(1L, "Test notes");

        
        assertNotNull(result);
        assertEquals(testVisit, result);
        verify(bookingDao).findById(1L);
        verify(visitDao).findByBookingId(1L);
        verify(visitDao).save(any(Visit.class));
    }

    @Test
    void recordVisitForBooking_WhenBookingNotFound_ShouldThrowException() {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.recordVisitForBooking(999L, "Test notes");
        });
        
        assertEquals("Booking not found", exception.getMessage());
        verify(bookingDao).findById(999L);
        verify(visitDao, never()).save(any(Visit.class));
    }

    @Test
    void recordVisitForBooking_WhenVisitAlreadyExists_ShouldThrowException() {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(visitDao.findByBookingId(anyLong())).thenReturn(Optional.of(testVisit));

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.recordVisitForBooking(1L, "Test notes");
        });
        
        assertEquals("Visit already recorded for this booking", exception.getMessage());
        verify(bookingDao).findById(1L);
        verify(visitDao).findByBookingId(1L);
        verify(visitDao, never()).save(any(Visit.class));
    }

    @Test
    void updateVisit_WhenVisitExists_ShouldUpdateAndReturnVisit() throws Exception {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.of(testVisit));
        when(staffDao.findById(anyLong())).thenReturn(Optional.of(testTrainer));
        when(visitDao.save(any(Visit.class))).thenReturn(testVisit);

        
        Visit result = visitService.updateVisit(1L, 1L, "Updated notes");

        
        assertEquals("Updated notes", result.getNotes());
        verify(visitDao).findById(1L);
        verify(staffDao).findById(1L);
        verify(visitDao).save(testVisit);
    }

    @Test
    void updateVisit_WhenVisitDoesNotExist_ShouldThrowException() {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.updateVisit(999L, 1L, "Updated notes");
        });
        
        assertEquals("Visit not found", exception.getMessage());
        verify(visitDao).findById(999L);
        verify(visitDao, never()).save(any(Visit.class));
    }

    @Test
    void updateVisit_WhenTrainerNotFound_ShouldThrowException() {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.of(testVisit));
        when(staffDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.updateVisit(1L, 999L, "Updated notes");
        });
        
        assertEquals("Trainer not found", exception.getMessage());
        verify(visitDao).findById(1L);
        verify(staffDao).findById(999L);
        verify(visitDao, never()).save(any(Visit.class));
    }

    @Test
    void deleteVisit_WhenVisitExists_ShouldDeleteVisit() throws Exception {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.of(testVisit));
        doNothing().when(visitDao).deleteById(anyLong());

        
        visitService.deleteVisit(1L);

        
        verify(visitDao).findById(1L);
        verify(visitDao).deleteById(1L);
    }

    @Test
    void deleteVisit_WhenVisitDoesNotExist_ShouldThrowException() {
        
        when(visitDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            visitService.deleteVisit(999L);
        });
        
        assertEquals("Visit not found", exception.getMessage());
        verify(visitDao).findById(999L);
        verify(visitDao, never()).deleteById(anyLong());
    }

    @Test
    void getVisitStatsByDay_ShouldReturnVisitStats() {
        
        int year = 2025;
        int month = 5;
        List<Visit> monthVisits = Arrays.asList(
            createVisitWithDate(year, month, 1),
            createVisitWithDate(year, month, 1),
            createVisitWithDate(year, month, 5),
            createVisitWithDate(year, month, 10)
        );
        
        when(visitDao.findVisitsByMonth(year, month)).thenReturn(monthVisits);

        
        Map<Integer, Integer> result = visitService.getVisitStatsByDay(year, month);

        
        assertEquals(31, result.size()); 
        assertEquals(2, result.get(1));  
        assertEquals(1, result.get(5));  
        assertEquals(1, result.get(10)); 
        assertEquals(0, result.get(15)); 
        verify(visitDao).findVisitsByMonth(year, month);
    }

    @Test
    void getTotalVisitsForDateRange_ShouldReturnTotalVisits() {
        
        LocalDate from = LocalDate.of(2025, 5, 1);
        LocalDate to = LocalDate.of(2025, 5, 31);
        
        List<Visit> allVisits = Arrays.asList(
            createVisitWithDate(2025, 4, 30),  
            createVisitWithDate(2025, 5, 1),   
            createVisitWithDate(2025, 5, 15),  
            createVisitWithDate(2025, 5, 31),  
            createVisitWithDate(2025, 6, 1)    
        );
        
        when(visitDao.findAll()).thenReturn(allVisits);

        
        int result = visitService.getTotalVisitsForDateRange(from, to);

        
        assertEquals(3, result); 
        verify(visitDao).findAll();
    }
    
    
    private Visit createVisitWithDate(int year, int month, int day) {
        Visit visit = new Visit();
        visit.setClient(testClient);
        visit.setVisitDate(LocalDateTime.of(year, month, day, 12, 0));
        return visit;
    }
}
