package com.gym.service;

import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.model.Client;
import com.gym.model.Training;
import com.gym.model.Membership;
import com.gym.dao.impl.BookingDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.MembershipDao;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
public class BookingService {
    
    private final BookingDao bookingDao;
    private final ClientDao clientDao;
    private final TrainingDao trainingDao;
    private final MembershipDao membershipDao;
    
    public BookingService() {
        this.bookingDao = new BookingDao();
        this.clientDao = new ClientDao();
        this.trainingDao = new TrainingDao();
        this.membershipDao = new MembershipDao();
    }
    

    public BookingService(BookingDao bookingDao, ClientDao clientDao, TrainingDao trainingDao, MembershipDao membershipDao) {
        this.bookingDao = bookingDao;
        this.clientDao = clientDao;
        this.trainingDao = trainingDao;
        this.membershipDao = membershipDao;
    }
    

    public List<Booking> getAllBookings() {
        log.info("Retrieving all bookings");
        return bookingDao.findAll();
    }
    

    public Optional<Booking> getBookingById(Long id) {
        log.info("Finding booking with ID: {}", id);
        return bookingDao.findById(id);
    }
    

    public List<Booking> getBookingsByClientId(Long clientId) {
        log.info("Finding bookings for client ID: {}", clientId);
        return bookingDao.findByClientId(clientId);
    }
    

    public List<Booking> getBookingsByTrainingId(Long trainingId) {
        log.info("Finding bookings for training ID: {}", trainingId);
        return bookingDao.findByTrainingId(trainingId);
    }
    

    public List<Booking> getUpcomingBookings(Long clientId) {
        log.info("Finding upcoming bookings for client ID: {}", clientId);
        return bookingDao.findUpcomingBookings(clientId);
    }
    

    public Booking createBooking(Long clientId, Long trainingId) throws Exception {
        log.info("Creating booking for client ID: {} and training ID: {}", clientId, trainingId);
        
        Optional<Client> clientOpt = clientDao.findById(clientId);
        if (clientOpt.isEmpty()) {
            throw new Exception("Client not found");
        }
        Client client = clientOpt.get();
        
        Optional<Training> trainingOpt = trainingDao.findById(trainingId);
        if (trainingOpt.isEmpty()) {
            throw new Exception("Training not found");
        }
        Training training = trainingOpt.get();
        
        if (training.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new Exception("Cannot book a past training");
        }
        
        long bookedCount = bookingDao.countActiveBookingsForTraining(trainingId);
        if (bookedCount >= training.getCapacity()) {
            throw new Exception("Training is fully booked");
        }
        
        List<Membership> activeMemberships = membershipDao.findActiveByClientId(clientId);
        if (activeMemberships.isEmpty()) {
            throw new Exception("Client does not have an active membership");
        }
        
        List<Booking> existingBookings = bookingDao.findByClientId(clientId);
        for (Booking booking : existingBookings) {
            if (booking.getTraining().getId().equals(trainingId) && 
                booking.getStatus() == BookingStatus.BOOKED) {
                throw new Exception("Client already has a booking for this training");
            }
        }
        
        Booking booking = new Booking();
        booking.setClient(client);
        booking.setTraining(training);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setBookingTime(LocalDateTime.now());
        
        Booking savedBooking = bookingDao.save(booking);
        if (savedBooking == null) {
            throw new Exception("Failed to save booking");
        }
        

        for (Membership membership : activeMemberships) {
            if (membership.getVisitsLeft() != null && membership.getVisitsLeft() > 0) {
                membership.setVisitsLeft(membership.getVisitsLeft() - 1);
                membershipDao.save(membership);
                log.info("Updated membership ID: {}, visits left: {}", 
                        membership.getId(), membership.getVisitsLeft());
                break;
            }
        }
        
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return savedBooking;
    }
    

    public void cancelBooking(Long bookingId) throws Exception {
        log.info("Cancelling booking with ID: {}", bookingId);
        
        Optional<Booking> bookingOpt = bookingDao.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new Exception("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.info("Booking is already cancelled");
            return;
        }
        
        if (booking.getTraining().getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new Exception("Cannot cancel a past booking");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        
        Booking savedBooking = bookingDao.save(booking);
        if (savedBooking == null) {
            throw new Exception("Failed to cancel booking");
        }
        

        List<Membership> activeMemberships = membershipDao.findActiveByClientId(booking.getClient().getId());
        if (!activeMemberships.isEmpty()) {
            Membership membership = activeMemberships.get(0);
            if (membership.getVisitsLeft() != null) {
                membership.setVisitsLeft(membership.getVisitsLeft() + 1);
                membershipDao.save(membership);
                log.info("Refunded visit to membership ID: {}, visits left: {}", 
                        membership.getId(), membership.getVisitsLeft());
            }
        }
        
        log.info("Booking cancelled successfully");
    }
    

    public int getBookedCount(Long trainingId) {
        log.info("Getting booked count for training ID: {}", trainingId);
        return bookingDao.getBookedCount(trainingId);
    }
    
 
    public boolean hasBooking(Long clientId, Long trainingId) {
        List<Booking> clientBookings = bookingDao.findByClientId(clientId);
        
        for (Booking booking : clientBookings) {
            if (booking.getTraining().getId().equals(trainingId) && 
                booking.getStatus() == BookingStatus.BOOKED) {
                return true;
            }
        }
        
        return false;
    }
}
