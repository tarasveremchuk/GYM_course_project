package com.gym.service;

import com.gym.model.Staff;
import com.gym.model.StaffRole;
import com.gym.model.User;
import com.gym.dao.impl.StaffDao;
import com.gym.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

@Slf4j
public class StaffService {
    
    private final StaffDao staffDao;
    private final UserDao userDao;
    
    public StaffService() {
        this.staffDao = new StaffDao();
        this.userDao = new UserDao();
    }
    

    public StaffService(StaffDao staffDao, UserDao userDao) {
        this.staffDao = staffDao;
        this.userDao = userDao;
    }
    public List<Staff> getAllStaff() {
        log.info("Retrieving all staff members");
        return staffDao.findAll();
    }
    public Optional<Staff> getStaffById(Long id) {
        log.info("Finding staff member with ID: {}", id);
        return staffDao.findById(id);
    }
    public List<Staff> getAllTrainers() {
        log.info("Retrieving all trainers");
        return staffDao.findTrainers();
    }
    public List<Staff> getActiveTrainers() {
        log.info("Retrieving all active trainers");
        return staffDao.findActiveTrainers();
    }
    public Optional<Staff> getTrainerByClientId(Long clientId) {
        log.info("Finding trainer for client ID: {}", clientId);
        return staffDao.findTrainerByClientId(clientId);
    }
    public List<Staff> searchStaff(String query) {
        log.info("Searching for staff with query: {}", query);
        return staffDao.searchStaff(query);
    }
    public Staff saveStaff(Staff staff, String username, String password) throws Exception {
        if (staff.getId() == null) {
            log.info("Creating new staff member: {}", staff.getFullName());
            Staff savedStaff = staffDao.save(staff);
            if (savedStaff == null) {
                throw new Exception("Failed to save staff member");
            }
            if (username != null && password != null) {
                User user = new User();
                user.setUsername(username);
                user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));

                if (staff.getRole() == StaffRole.TRAINER) {
                    user.setRole(User.UserRole.trainer);
                } else {
                    user.setRole(User.UserRole.admin);
                }
                
                log.info("Creating user account for staff ID: {}", savedStaff.getId());
                User savedUser = userDao.save(user);
                if (savedUser == null) {
                    throw new Exception("Failed to create user account for staff member");
                }
            }
            
            return savedStaff;
        } else {
            log.info("Updating existing staff member ID: {}", staff.getId());
            Staff savedStaff = staffDao.save(staff);
            if (savedStaff == null) {
                throw new Exception("Failed to update staff member");
            }
            
            return savedStaff;
        }
    }
    

    public void deleteStaff(Long id) throws Exception {
        log.info("Deleting staff member with ID: {}", id);
        
        Optional<Staff> staffOpt = staffDao.findById(id);
        if (staffOpt.isEmpty()) {
            throw new Exception("Staff member not found");
        }
        
        try {
            staffDao.deleteById(id);
            log.info("Staff member deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting staff member: {}", e.getMessage());
            throw new Exception("Failed to delete staff member: " + e.getMessage());
        }
    }

    public void assignTrainerToClient(Long trainerId, Long clientId) throws Exception {
        log.info("Assigning trainer ID: {} to client ID: {}", trainerId, clientId);
        
        Optional<Staff> trainerOpt = staffDao.findById(trainerId);
        if (trainerOpt.isEmpty()) {
            throw new Exception("Trainer not found");
        }
        
        Staff trainer = trainerOpt.get();
        if (trainer.getRole() != StaffRole.TRAINER) {
            throw new Exception("Staff member is not a trainer");
        }
        
        log.info("Trainer assignment functionality would be implemented here");

    }
    

    public Staff updateStaffStatus(Long id, String notes) throws Exception {
        log.info("Updating status for staff member ID: {}", id);
        
        Optional<Staff> staffOpt = staffDao.findById(id);
        if (staffOpt.isEmpty()) {
            throw new Exception("Staff member not found");
        }
        
        Staff staff = staffOpt.get();
        
        Staff savedStaff = staffDao.save(staff);
        if (savedStaff == null) {
            throw new Exception("Failed to update staff member");
        }
        
        return savedStaff;
    }
}
