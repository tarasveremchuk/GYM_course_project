package com.gym.service;

import com.gym.model.Visit;
import com.gym.model.Client;
import com.gym.model.Staff;
import com.gym.model.Booking;
import com.gym.dao.impl.VisitDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.BookingDao;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class VisitService {
    
    private final VisitDao visitDao;
    private final ClientDao clientDao;
    private final StaffDao staffDao;
    private final BookingDao bookingDao;
    public VisitService() {
        this.visitDao = new VisitDao();
        this.clientDao = new ClientDao();
        this.staffDao = new StaffDao();
        this.bookingDao = new BookingDao();
    }
    public VisitService(VisitDao visitDao, ClientDao clientDao, StaffDao staffDao, BookingDao bookingDao) {
        this.visitDao = visitDao;
        this.clientDao = clientDao;
        this.staffDao = staffDao;
        this.bookingDao = bookingDao;
    }
    public List<Visit> getAllVisits() {
        log.info("Retrieving all visits");
        return visitDao.findAll();
    }
    public Optional<Visit> getVisitById(Long id) {
        log.info("Finding visit with ID: {}", id);
        return visitDao.findById(id);
    }
    public List<Visit> getVisitsByClientId(Long clientId) {
        log.info("Finding visits for client ID: {}", clientId);
        return visitDao.findByClientId(clientId);
    }
    public Optional<Visit> getVisitByBookingId(Long bookingId) {
        log.info("Finding visit for booking ID: {}", bookingId);
        return visitDao.findByBookingId(bookingId);
    }
    public List<Visit> getVisitsByMonth(int year, int month) {
        log.info("Finding visits for {}/{}", year, month);
        return visitDao.findVisitsByMonth(year, month);
    }
    

    public List<Visit> getRecentVisits(int limit) {
        log.info("Finding {} most recent visits", limit);
        return visitDao.findRecentVisits(limit);
    }
    

    public Visit recordVisit(Long clientId, Long trainerId, String notes) throws Exception {
        log.info("Recording visit for client ID: {}", clientId);
        
        Optional<Client> clientOpt = clientDao.findById(clientId);
        if (clientOpt.isEmpty()) {
            log.warn("Client not found with ID: {}", clientId);
            throw new Exception("Client not found");
        }
        
        Visit visit = new Visit();
        visit.setClient(clientOpt.get());
        visit.setVisitDate(LocalDateTime.now());
        
        if (trainerId != null) {
            Optional<Staff> trainerOpt = staffDao.findById(trainerId);
            if (trainerOpt.isPresent()) {
                visit.setTrainer(trainerOpt.get());
            } else {
                log.warn("Trainer not found with ID: {}", trainerId);
            }
        }
        
        if (notes != null) {
            visit.setNotes(notes);
        }
        
        Visit savedVisit = visitDao.save(visit);
        if (savedVisit == null) {
            log.error("Failed to save visit");
            throw new Exception("Failed to save visit");
        }
        
        log.info("Visit recorded successfully with ID: {}", savedVisit.getId());
        return savedVisit;
    }
    

    public Visit recordVisitForBooking(Long bookingId, String notes) throws Exception {
        log.info("Recording visit for booking ID: {}", bookingId);
        
        Optional<Booking> bookingOpt = bookingDao.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            log.warn("Booking not found with ID: {}", bookingId);
            throw new Exception("Booking not found");
        }
        
        Optional<Visit> existingVisit = visitDao.findByBookingId(bookingId);
        if (existingVisit.isPresent()) {
            log.warn("Visit already exists for booking ID: {}", bookingId);
            throw new Exception("Visit already recorded for this booking");
        }
        
        Booking booking = bookingOpt.get();
        
        Visit visit = new Visit();
        visit.setClient(booking.getClient());
        
        if (booking.getTraining() == null) {
            log.warn("Training not found for booking ID: {}", bookingId);
            throw new Exception("Training not found for this booking");
        }
        
        visit.setVisitDate(booking.getTraining().getScheduledAt());
        
        if (booking.getTraining().getTrainer() != null) {
            visit.setTrainer(booking.getTraining().getTrainer());
        }
        
        if (notes != null) {
            visit.setNotes(notes);
        }
        
        Visit savedVisit = visitDao.save(visit);
        if (savedVisit == null) {
            log.error("Failed to save visit");
            throw new Exception("Failed to save visit");
        }
        
        log.info("Visit recorded successfully with ID: {}", savedVisit.getId());
        return savedVisit;
    }

    public Visit updateVisit(Long id, Long trainerId, String notes) throws Exception {
        log.info("Updating visit with ID: {}", id);
        
        Optional<Visit> visitOpt = visitDao.findById(id);
        if (visitOpt.isEmpty()) {
            log.warn("Visit not found with ID: {}", id);
            throw new Exception("Visit not found");
        }
        
        Visit visit = visitOpt.get();
        
        if (trainerId != null) {
            Optional<Staff> trainerOpt = staffDao.findById(trainerId);
            if (trainerOpt.isPresent()) {
                visit.setTrainer(trainerOpt.get());
            } else {
                log.warn("Trainer not found with ID: {}", trainerId);
                throw new Exception("Trainer not found");
            }
        }
        
        if (notes != null) {
            visit.setNotes(notes);
        }
        
        Visit savedVisit = visitDao.save(visit);
        if (savedVisit == null) {
            log.error("Failed to update visit");
            throw new Exception("Failed to update visit");
        }
        
        log.info("Visit updated successfully");
        return savedVisit;
    }
    

    public void deleteVisit(Long id) throws Exception {
        log.info("Deleting visit with ID: {}", id);
        
        Optional<Visit> visitOpt = visitDao.findById(id);
        if (visitOpt.isEmpty()) {
            log.warn("Visit not found with ID: {}", id);
            throw new Exception("Visit not found");
        }
        
        try {
            visitDao.deleteById(id);
            log.info("Visit deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting visit: {}", e.getMessage());
            throw new Exception("Failed to delete visit: " + e.getMessage());
        }
    }
    

    public Map<Integer, Integer> getVisitStatsByDay(int year, int month) {
        log.info("Generating visit statistics by day for {}/{}", year, month);
        
        List<Visit> visits = visitDao.findVisitsByMonth(year, month);
        Map<Integer, Integer> statsByDay = new HashMap<>();
        
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            statsByDay.put(day, 0);
        }
        
        for (Visit visit : visits) {
            int day = visit.getVisitDate().getDayOfMonth();
            statsByDay.put(day, statsByDay.get(day) + 1);
        }
        
        return statsByDay;
    }
    

    public int getTotalVisitsForDateRange(LocalDate from, LocalDate to) {
        log.info("Getting total visits from {} to {}", from, to);
        
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);
        
        List<Visit> allVisits = visitDao.findAll();
        long count = allVisits.stream()
            .filter(visit -> {
                LocalDateTime visitDateTime = visit.getVisitDate();
                return !visitDateTime.isBefore(fromDateTime) && !visitDateTime.isAfter(toDateTime);
            })
            .count();
        
        log.info("Found {} visits between {} and {}", count, from, to);
        return (int) count;
    }
}
