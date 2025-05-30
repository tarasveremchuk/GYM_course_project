package com.gym.service;

import com.gym.dao.impl.BookingDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.MembershipDao;
import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.model.Client;
import com.gym.model.Training;
import com.gym.model.Membership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingDao bookingDao;

    @Mock
    private ClientDao clientDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private MembershipDao membershipDao;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private Client testClient;
    private Training testTraining;
    private Membership testMembership;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Test Client");

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setName("Test Training");
        testTraining.setCapacity(10);
        testTraining.setScheduledAt(LocalDateTime.now().plusDays(1));
        testTraining.setDurationMinutes(60);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setClient(testClient);
        testBooking.setTraining(testTraining);
        testBooking.setBookingTime(LocalDateTime.now());
        testBooking.setStatus(BookingStatus.BOOKED);

        testMembership = new Membership();
        testMembership.setId(1L);
        testMembership.setClient(testClient);
        testMembership.setType(Membership.MembershipType.unlimited);
        testMembership.setStartDate(LocalDate.now());
        testMembership.setEndDate(LocalDate.now().plusMonths(1));
        testMembership.setVisitsLeft(10);
        testMembership.setPaid(true);
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking, new Booking());
        when(bookingDao.findAll()).thenReturn(expectedBookings);

        
        List<Booking> actualBookings = bookingService.getAllBookings();

        
        assertEquals(expectedBookings.size(), actualBookings.size());
        assertEquals(expectedBookings, actualBookings);
        verify(bookingDao).findAll();
    }

    @Test
    void getBookingById_WhenBookingExists_ShouldReturnBooking() {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(testBooking));

        
        Optional<Booking> result = bookingService.getBookingById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testBooking, result.get());
        verify(bookingDao).findById(1L);
    }

    @Test
    void getBookingById_WhenBookingDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<Booking> result = bookingService.getBookingById(999L);

        
        assertFalse(result.isPresent());
        verify(bookingDao).findById(999L);
    }

    @Test
    void getBookingsByClientId_ShouldReturnClientBookings() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingDao.findByClientId(anyLong())).thenReturn(expectedBookings);

        
        List<Booking> result = bookingService.getBookingsByClientId(1L);

        
        assertEquals(expectedBookings.size(), result.size());
        assertEquals(expectedBookings, result);
        verify(bookingDao).findByClientId(1L);
    }

    @Test
    void getBookingsByTrainingId_ShouldReturnTrainingBookings() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingDao.findByTrainingId(anyLong())).thenReturn(expectedBookings);

        
        List<Booking> result = bookingService.getBookingsByTrainingId(1L);

        
        assertEquals(expectedBookings.size(), result.size());
        assertEquals(expectedBookings, result);
        verify(bookingDao).findByTrainingId(1L);
    }

    @Test
    void getUpcomingBookings_ShouldReturnUpcomingBookings() {
        
        List<Booking> expectedBookings = Arrays.asList(testBooking);
        when(bookingDao.findUpcomingBookings(anyLong())).thenReturn(expectedBookings);

        
        List<Booking> result = bookingService.getUpcomingBookings(1L);

        
        assertEquals(expectedBookings.size(), result.size());
        assertEquals(expectedBookings, result);
        verify(bookingDao).findUpcomingBookings(1L);
    }

    @Test
    void createBooking_WhenValidInputs_ShouldCreateBooking() throws Exception {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(trainingDao.findById(anyLong())).thenReturn(Optional.of(testTraining));
        when(bookingDao.countActiveBookingsForTraining(anyLong())).thenReturn(5L); 
        when(membershipDao.findActiveByClientId(anyLong())).thenReturn(Collections.singletonList(testMembership));
        when(bookingDao.save(any(Booking.class))).thenReturn(testBooking);

        
        Booking result = bookingService.createBooking(1L, 1L);

        
        assertNotNull(result);
        assertEquals(testBooking, result);
        verify(clientDao).findById(1L);
        verify(trainingDao).findById(1L);
        verify(bookingDao).countActiveBookingsForTraining(1L);
        verify(membershipDao).findActiveByClientId(1L);
        verify(bookingDao).save(any(Booking.class));
        verify(membershipDao).save(testMembership); 
    }

    @Test
    void createBooking_WhenClientNotFound_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.createBooking(999L, 1L);
        });

        assertEquals("Client not found", exception.getMessage());
        verify(clientDao).findById(999L);
        verify(trainingDao, never()).findById(anyLong());
        verify(bookingDao, never()).countActiveBookingsForTraining(anyLong());
        verify(membershipDao, never()).findActiveByClientId(anyLong());
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenTrainingNotFound_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(trainingDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.createBooking(1L, 999L);
        });

        assertEquals("Training not found", exception.getMessage());
        verify(clientDao).findById(1L);
        verify(trainingDao).findById(999L);
        verify(bookingDao, never()).countActiveBookingsForTraining(anyLong());
        verify(membershipDao, never()).findActiveByClientId(anyLong());
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenTrainingInPast_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));

        Training pastTraining = new Training();
        pastTraining.setId(2L);
        pastTraining.setScheduledAt(LocalDateTime.now().minusDays(1)); 
        when(trainingDao.findById(eq(2L))).thenReturn(Optional.of(pastTraining));

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.createBooking(1L, 2L);
        });

        assertEquals("Cannot book a past training", exception.getMessage());
        verify(clientDao).findById(1L);
        verify(trainingDao).findById(2L);
        verify(bookingDao, never()).countActiveBookingsForTraining(anyLong());
        verify(membershipDao, never()).findActiveByClientId(anyLong());
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenTrainingFull_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(trainingDao.findById(anyLong())).thenReturn(Optional.of(testTraining));
        when(bookingDao.countActiveBookingsForTraining(anyLong())).thenReturn(10L); 

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.createBooking(1L, 1L);
        });

        assertEquals("Training is fully booked", exception.getMessage());
        verify(clientDao).findById(1L);
        verify(trainingDao).findById(1L);
        verify(bookingDao).countActiveBookingsForTraining(1L);
        verify(membershipDao, never()).findActiveByClientId(anyLong());
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenNoActiveMembership_ShouldThrowException() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        when(trainingDao.findById(anyLong())).thenReturn(Optional.of(testTraining));
        when(bookingDao.countActiveBookingsForTraining(anyLong())).thenReturn(5L);
        when(membershipDao.findActiveByClientId(anyLong())).thenReturn(Collections.emptyList());

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.createBooking(1L, 1L);
        });

        assertEquals("Client does not have an active membership", exception.getMessage());
        verify(clientDao).findById(1L);
        verify(trainingDao).findById(1L);
        verify(bookingDao).countActiveBookingsForTraining(1L);
        verify(membershipDao).findActiveByClientId(1L);
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_WhenBookingExists_ShouldCancelBooking() throws Exception {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingDao.save(any(Booking.class))).thenReturn(testBooking);
        when(membershipDao.findActiveByClientId(anyLong())).thenReturn(Collections.singletonList(testMembership));

        
        bookingService.cancelBooking(1L);

        
        assertEquals(BookingStatus.CANCELLED, testBooking.getStatus());
        verify(bookingDao).findById(1L);
        verify(bookingDao).save(testBooking);
        verify(membershipDao).findActiveByClientId(anyLong());
        verify(membershipDao).save(testMembership); 
    }

    @Test
    void cancelBooking_WhenBookingDoesNotExist_ShouldThrowException() {
        
        when(bookingDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.cancelBooking(999L);
        });

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingDao).findById(999L);
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    void getBookedCount_ShouldReturnNumberOfBookings() {
        
        when(bookingDao.getBookedCount(anyLong())).thenReturn(5);

        
        int result = bookingService.getBookedCount(1L);

        
        assertEquals(5, result);
        verify(bookingDao).getBookedCount(1L);
    }

    @Test
    void hasBooking_WhenClientHasBooking_ShouldReturnTrue() {
        
        List<Booking> clientBookings = Collections.singletonList(testBooking);
        when(bookingDao.findByClientId(anyLong())).thenReturn(clientBookings);

        
        boolean result = bookingService.hasBooking(1L, 1L);

        
        assertTrue(result);
        verify(bookingDao).findByClientId(1L);
    }

    @Test
    void hasBooking_WhenClientDoesNotHaveBooking_ShouldReturnFalse() {
        
        when(bookingDao.findByClientId(anyLong())).thenReturn(Collections.emptyList());

        
        boolean result = bookingService.hasBooking(1L, 1L);

        
        assertFalse(result);
        verify(bookingDao).findByClientId(1L);
    }

    @Test
    void hasBooking_WhenClientHasCancelledBooking_ShouldReturnFalse() {
        
        Booking cancelledBooking = new Booking();
        cancelledBooking.setId(2L);
        cancelledBooking.setClient(testClient);
        cancelledBooking.setTraining(testTraining);
        cancelledBooking.setStatus(BookingStatus.CANCELLED);

        List<Booking> clientBookings = Collections.singletonList(cancelledBooking);
        when(bookingDao.findByClientId(anyLong())).thenReturn(clientBookings);

        
        boolean result = bookingService.hasBooking(1L, 1L);

        
        assertFalse(result);
        verify(bookingDao).findByClientId(1L);
    }
}
