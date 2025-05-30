package com.gym.service;

import com.gym.model.Training;
import com.gym.dao.impl.TrainingDao;
import com.gym.dao.impl.BookingDao;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
public class TrainingService {
    
    private final TrainingDao trainingDao;
    private final BookingDao bookingDao;
    
    public TrainingService() {
        this.trainingDao = new TrainingDao();
        this.bookingDao = new BookingDao();
    }
    

    public TrainingService(TrainingDao trainingDao, BookingDao bookingDao) {
        this.trainingDao = trainingDao;
        this.bookingDao = bookingDao;
    }
    

    public List<Training> getAllTrainings() {
        log.info("Retrieving all trainings");
        return trainingDao.findAll();
    }
    

    public Optional<Training> getTrainingById(Long id) {
        log.info("Finding training with ID: {}", id);
        return trainingDao.findById(id);
    }
    

    public List<Training> getUpcomingTrainings() {
        log.info("Finding upcoming trainings");
        return trainingDao.findUpcomingTrainings();
    }
    

    public List<Training> getTrainingsByTrainerId(Long trainerId) {
        log.info("Finding trainings for trainer ID: {}", trainerId);
        return trainingDao.findByTrainerId(trainerId);
    }
    

    public List<Training> getAvailableTrainings() {
        log.info("Finding available trainings");
        return trainingDao.findAvailableTrainings();
    }
    

    public Training saveTraining(Training training) throws Exception {
        if (training.getId() == null) {
            log.info("Creating new training: {}", training.getName());
        } else {
            log.info("Updating existing training ID: {}", training.getId());
        }
        
        Training savedTraining = trainingDao.save(training);
        if (savedTraining == null) {
            throw new Exception("Failed to save training");
        }
        
        return savedTraining;
    }

    public void deleteTraining(Long id) throws Exception {
        log.info("Deleting training with ID: {}", id);
        
        try {
            bookingDao.cancelBookingsForTraining(id);
            log.info("Cancelled all bookings for training ID: {}", id);
        } catch (Exception e) {
            log.error("Error cancelling bookings for training ID {}: {}", id, e.getMessage());
            throw new Exception("Failed to cancel bookings for training: " + e.getMessage());
        }
        
        try {
            trainingDao.deleteById(id);
            log.info("Training deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting training ID {}: {}", id, e.getMessage());
            throw new Exception("Failed to delete training: " + e.getMessage());
        }
    }
    

    public int getBookedCount(Long trainingId) {
        log.info("Getting booked count for training ID: {}", trainingId);
        return bookingDao.getBookedCount(trainingId);
    }
    

    public boolean hasAvailableSpots(Long trainingId) {
        Optional<Training> trainingOpt = trainingDao.findById(trainingId);
        if (trainingOpt.isEmpty()) {
            log.warn("Training with ID {} not found", trainingId);
            return false;
        }
        
        Training training = trainingOpt.get();
        int bookedCount = bookingDao.getBookedCount(trainingId);
        
        boolean hasSpots = bookedCount < training.getCapacity();
        log.info("Training ID {}: {} spots booked out of {} capacity, available: {}", 
                trainingId, bookedCount, training.getCapacity(), hasSpots);
        
        return hasSpots;
    }
    

    public boolean isUpcoming(Long trainingId) {
        Optional<Training> trainingOpt = trainingDao.findById(trainingId);
        if (trainingOpt.isEmpty()) {
            log.warn("Training with ID {} not found", trainingId);
            return false;
        }
        
        Training training = trainingOpt.get();
        boolean isUpcoming = training.getScheduledAt().isAfter(LocalDateTime.now());
        
        log.info("Training ID {}: scheduled at {}, is upcoming: {}", 
                trainingId, training.getScheduledAt(), isUpcoming);
        
        return isUpcoming;
    }
}
