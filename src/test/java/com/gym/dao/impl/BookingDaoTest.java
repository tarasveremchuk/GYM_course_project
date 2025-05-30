package com.gym.dao.impl;

import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.model.Client;
import com.gym.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Booking> mockTypedQuery;
    
    @Mock
    private TypedQuery<Long> mockLongQuery;
    
    @Mock
    private Query mockQuery;
    
    @Spy
    @InjectMocks
    private BookingDao bookingDao;
    
    private Booking testBooking;
    private Client testClient;
    private Training testTraining;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("John Doe");
        
        
        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setName("Yoga Class");
        testTraining.setScheduledAt(LocalDateTime.now().plusDays(1));
        
        
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setClient(testClient);
        testBooking.setTraining(testTraining);
        testBooking.setBookingTime(LocalDateTime.now());
        testBooking.setStatus(BookingStatus.BOOKED);
        
        
        doReturn(mockEm).when(bookingDao).getEntityManager();
    }
    
    @Test
    void testFindByClientId() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(mockEm.createQuery(anyString(), eq(Booking.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedBookings);
        
        
        List<Booking> result = bookingDao.findByClientId(1L);
        
        
        assertEquals(expectedBookings, result);
        verify(mockEm).createQuery(anyString(), eq(Booking.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByTrainingId() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(mockEm.createQuery(anyString(), eq(Booking.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("trainingId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedBookings);
        
        
        List<Booking> result = bookingDao.findByTrainingId(1L);
        
        
        assertEquals(expectedBookings, result);
        verify(mockEm).createQuery(anyString(), eq(Booking.class));
        verify(mockTypedQuery).setParameter("trainingId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindUpcomingBookings() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(mockEm.createQuery(anyString(), eq(Booking.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("now"), any(LocalDateTime.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("status"), any(BookingStatus.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedBookings);
        
        
        List<Booking> result = bookingDao.findUpcomingBookings(1L);
        
        
        assertEquals(expectedBookings, result);
        verify(mockEm).createQuery(anyString(), eq(Booking.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).setParameter(eq("now"), any(LocalDateTime.class));
        verify(mockTypedQuery).setParameter("status", BookingStatus.BOOKED);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testCountActiveBookingsForTraining() {
        
        when(mockEm.createQuery(anyString(), eq(Long.class))).thenReturn(mockLongQuery);
        when(mockLongQuery.setParameter(eq("trainingId"), anyLong())).thenReturn(mockLongQuery);
        when(mockLongQuery.setParameter(eq("status"), any(BookingStatus.class))).thenReturn(mockLongQuery);
        when(mockLongQuery.getSingleResult()).thenReturn(5L);
        
        
        long result = bookingDao.countActiveBookingsForTraining(1L);
        
        
        assertEquals(5L, result);
        verify(mockEm).createQuery(anyString(), eq(Long.class));
        verify(mockLongQuery).setParameter("trainingId", 1L);
        verify(mockLongQuery).setParameter("status", BookingStatus.BOOKED);
        verify(mockLongQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testGetBookedCount() {
        
        when(mockEm.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("trainingId"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("status"), any(BookingStatus.class))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(5);
        
        
        int result = bookingDao.getBookedCount(1L);
        
        
        assertEquals(5, result);
        verify(mockEm).createQuery(anyString());
        verify(mockQuery).setParameter("trainingId", 1L);
        verify(mockQuery).setParameter("status", BookingStatus.BOOKED);
        verify(mockQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testCancelBookingsForTraining() {
        
        when(mockEm.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("trainingId"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("oldStatus"), any(BookingStatus.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("newStatus"), any(BookingStatus.class))).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(2); 
        
        
        bookingDao.cancelBookingsForTraining(1L);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).createQuery(anyString());
        verify(mockQuery).setParameter("trainingId", 1L);
        verify(mockQuery).setParameter("oldStatus", BookingStatus.BOOKED);
        verify(mockQuery).setParameter("newStatus", BookingStatus.CANCELLED);
        verify(mockQuery).executeUpdate();
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testCancelBookingsForTraining_Exception() {
        
        when(mockEm.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("trainingId"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("oldStatus"), any(BookingStatus.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("newStatus"), any(BookingStatus.class))).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenThrow(new RuntimeException("Test exception"));
        when(mockTransaction.isActive()).thenReturn(true);
        
        
        assertThrows(RuntimeException.class, () -> bookingDao.cancelBookingsForTraining(1L));
        
        
        verify(mockTransaction).begin();
        verify(mockTransaction).rollback();
        verify(mockEm).close();
    }
}
