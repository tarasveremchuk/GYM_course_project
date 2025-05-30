package com.gym.service;

import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.BookingDao;
import com.gym.dao.impl.StaffDao;
import com.gym.model.Training;
import com.gym.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;
    
    @Mock
    private BookingDao bookingDao;
    
    @Mock
    private StaffDao staffDao;

    @InjectMocks
    private TrainingService trainingService;

    private Training testTraining;
    private Staff testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = new Staff();
        testTrainer.setId(1L);
        testTrainer.setFullName("Test Trainer");
        
        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setName("Test Training");
        testTraining.setDescription("Test Description");
        testTraining.setTrainer(testTrainer);
        testTraining.setScheduledAt(LocalDateTime.now().plusDays(1));
        testTraining.setCapacity(10);
        testTraining.setDurationMinutes(60);
    }

    @Test
    void getAllTrainings_ShouldReturnAllTrainings() {
        
        List<Training> expectedTrainings = Arrays.asList(testTraining, new Training());
        when(trainingDao.findAll()).thenReturn(expectedTrainings);

        
        List<Training> actualTrainings = trainingService.getAllTrainings();

        
        assertEquals(expectedTrainings.size(), actualTrainings.size());
        assertEquals(expectedTrainings, actualTrainings);
        verify(trainingDao).findAll();
    }

    @Test
    void saveTraining_NewTraining_ShouldSaveAndReturnTraining() throws Exception {
        
        testTraining.setId(null); 
        when(trainingDao.save(any(Training.class))).thenReturn(testTraining);

        
        Training result = trainingService.saveTraining(testTraining);

        
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingDao).save(testTraining);
    }

    @Test
    void saveTraining_ExistingTraining_ShouldUpdateAndReturnTraining() throws Exception {
        
        when(trainingDao.save(any(Training.class))).thenReturn(testTraining);

        
        Training result = trainingService.saveTraining(testTraining);

        
        assertNotNull(result);
        assertEquals(testTraining, result);
        verify(trainingDao).save(testTraining);
    }

    @Test
    void saveTraining_WhenSaveFails_ShouldThrowException() {
        
        when(trainingDao.save(any(Training.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            trainingService.saveTraining(testTraining);
        });
        
        assertEquals("Failed to save training", exception.getMessage());
        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void deleteTraining_ShouldCancelBookingsAndDeleteTraining() throws Exception {
        
        doNothing().when(bookingDao).cancelBookingsForTraining(anyLong());
        doNothing().when(trainingDao).deleteById(anyLong());

        
        trainingService.deleteTraining(1L);

        
        verify(bookingDao).cancelBookingsForTraining(1L);
        verify(trainingDao).deleteById(1L);
    }

    @Test
    void deleteTraining_WhenCancelBookingsFails_ShouldThrowException() {
        
        doThrow(new RuntimeException("Booking cancellation error")).when(bookingDao).cancelBookingsForTraining(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            trainingService.deleteTraining(1L);
        });
        
        assertTrue(exception.getMessage().contains("Failed to cancel bookings"));
        verify(bookingDao).cancelBookingsForTraining(1L);
        verify(trainingDao, never()).deleteById(anyLong());
    }
    
    @Test
    void deleteTraining_WhenDeleteTrainingFails_ShouldThrowException() {
        
        doNothing().when(bookingDao).cancelBookingsForTraining(anyLong());
        doThrow(new RuntimeException("Delete training error")).when(trainingDao).deleteById(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            trainingService.deleteTraining(1L);
        });
        
        assertTrue(exception.getMessage().contains("Failed to delete training"));
        verify(bookingDao).cancelBookingsForTraining(1L);
        verify(trainingDao).deleteById(1L);
    }

    @Test
    void hasAvailableSpots_WhenTrainingHasAvailableSpots_ShouldReturnTrue() {
        
        when(trainingDao.findById(anyLong())).thenReturn(Optional.of(testTraining));
        when(bookingDao.getBookedCount(anyLong())).thenReturn(5); 

        
        boolean result = trainingService.hasAvailableSpots(1L);

        
        assertTrue(result);
        verify(trainingDao).findById(1L);
        verify(bookingDao).getBookedCount(1L);
    }

    @Test
    void hasAvailableSpots_WhenTrainingIsFull_ShouldReturnFalse() {
        
        when(trainingDao.findById(anyLong())).thenReturn(Optional.of(testTraining));
        when(bookingDao.getBookedCount(anyLong())).thenReturn(10); 

        
        boolean result = trainingService.hasAvailableSpots(1L);

        
        assertFalse(result);
        verify(trainingDao).findById(1L);
        verify(bookingDao).getBookedCount(1L);
    }

    @Test
    void hasAvailableSpots_WhenTrainingNotFound_ShouldReturnFalse() {
        
        when(trainingDao.findById(anyLong())).thenReturn(Optional.empty());

        
        boolean result = trainingService.hasAvailableSpots(999L);

        
        assertFalse(result);
        verify(trainingDao).findById(999L);
        verify(bookingDao, never()).getBookedCount(anyLong());
    }
}
