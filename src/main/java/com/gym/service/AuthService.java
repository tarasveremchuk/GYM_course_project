package com.gym.service;

import com.gym.model.User;
import com.gym.model.Client;
import com.gym.model.Staff;
import com.gym.dao.impl.UserDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.StaffDao;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.List;


@Slf4j
public class AuthService {
    
    private final UserDao userDao;
    private final ClientDao clientDao;
    private final StaffDao staffDao;
    
    private User currentUser;
    
    public AuthService() {
        this.userDao = new UserDao();
        this.clientDao = new ClientDao();
        this.staffDao = new StaffDao();
        this.currentUser = null;
    }
    

    public AuthService(UserDao userDao, ClientDao clientDao, StaffDao staffDao) {
        this.userDao = userDao;
        this.clientDao = clientDao;
        this.staffDao = staffDao;
        this.currentUser = null;
    }
    

    public boolean login(String username, String password) {
        log.info("Attempting login for user: {}", username);
        
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("Login failed: user not found: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            log.info("Login successful for user: {} with role: {}", username, user.getRole());
            this.currentUser = user;
            return true;
        } else {
            log.warn("Login failed: invalid password for user: {}", username);
            return false;
        }
    }
    

    public void logout() {
        if (currentUser != null) {
            log.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }
    

    public User getCurrentUser() {
        return currentUser;
    }
    

    public boolean isAuthenticated() {
        return currentUser != null;
    }
    

    public boolean hasRole(User.UserRole role) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole() == role;
    }
    

    public boolean isAdmin() {
        return hasRole(User.UserRole.admin);
    }
    

    public boolean isTrainer() {
        return hasRole(User.UserRole.trainer);
    }
    

    public boolean isClient() {
        return hasRole(User.UserRole.client);
    }
    

    public Optional<Client> getCurrentClient() {
        if (currentUser == null || currentUser.getRole() != User.UserRole.client || currentUser.getClientId() == null) {
            return Optional.empty();
        }
        
        return clientDao.findById(currentUser.getClientId());
    }
    
 

    public Optional<Staff> getCurrentStaff() {
        if (currentUser == null || (currentUser.getRole() != User.UserRole.admin && currentUser.getRole() != User.UserRole.trainer)) {
            return Optional.empty();
        }
        

        List<Staff> allStaff = staffDao.findAll();
        return allStaff.stream()
                .filter(staff -> {
                    return staff.getEmail() != null && staff.getEmail().equalsIgnoreCase(currentUser.getUsername());
                })
                .findFirst();
    }
    

    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            log.warn("Cannot change password: no user is authenticated");
            return false;
        }
        
        if (!BCrypt.checkpw(currentPassword, currentUser.getPasswordHash())) {
            log.warn("Cannot change password: current password is incorrect");
            return false;
        }
        
        currentUser.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        
        User savedUser = userDao.save(currentUser);
        if (savedUser == null) {
            log.error("Failed to update user password");
            return false;
        }
        
        log.info("Password changed successfully for user: {}", currentUser.getUsername());
        return true;
    }
    

    public Client registerClient(Client client, String username, String password) throws Exception {
        log.info("Registering new client: {}", client.getFullName());
        
        if (userDao.existsByUsername(username)) {
            log.warn("Username already exists: {}", username);
            throw new Exception("Username already exists");
        }
        
        Client savedClient = clientDao.save(client);
        if (savedClient == null) {
            log.error("Failed to save client");
            throw new Exception("Failed to save client");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole(User.UserRole.client);
        user.setClientId(savedClient.getId());
        
        User savedUser = userDao.save(user);
        if (savedUser == null) {
            try {
                clientDao.deleteById(savedClient.getId());
            } catch (Exception e) {
                log.error("Failed to rollback client creation: {}", e.getMessage());
            }
            
            log.error("Failed to save user account");
            throw new Exception("Failed to save user account");
        }
        
        log.info("Client registered successfully with ID: {} and username: {}", savedClient.getId(), username);
        return savedClient;
    }
}
