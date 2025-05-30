package com.gym.service;

import com.gym.model.User;
import com.gym.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;



@Slf4j
public class UserService {
    
    private final UserDao userDao;
    
    public UserService() {
        this.userDao = new UserDao();
    }
    

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    

    public List<User> getAllUsers() {
        log.info("Retrieving all users");
        return userDao.findAll();
    }

    public Optional<User> getUserById(Long id) {
        log.info("Finding user with ID: {}", id);
        return userDao.findById(id);
    }
    

    public Optional<User> getUserByUsername(String username) {
        log.info("Finding user with username: {}", username);
        return userDao.findByUsername(username);
    }
    
    public boolean usernameExists(String username) {
        log.info("Checking if username exists: {}", username);
        return userDao.existsByUsername(username);
    }

    public User createUser(String username, String password, User.UserRole role) throws Exception {
        log.info("Creating new user with username: {} and role: {}", username, role);
        
        if (usernameExists(username)) {
            log.warn("Username already exists: {}", username);
            throw new Exception("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole(role);
        
        User savedUser = userDao.save(user);
        if (savedUser == null) {
            log.error("Failed to save user");
            throw new Exception("Failed to save user");
        }
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    

    public User updateUser(Long id, String username, String password, User.UserRole role) throws Exception {
        log.info("Updating user with ID: {}", id);
        
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            throw new Exception("User not found");
        }
        
        User user = userOpt.get();
        
        if (username != null && !username.equals(user.getUsername())) {
            if (userDao.existsByUsername(username)) {
                log.warn("Username already exists: {}", username);
                throw new Exception("Username already exists");
            }
            user.setUsername(username);
        }
        
        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        }
        
        if (role != null) {
            user.setRole(role);
        }
        
        User savedUser = userDao.save(user);
        if (savedUser == null) {
            log.error("Failed to update user");
            throw new Exception("Failed to update user");
        }
        
        log.info("User updated successfully");
        return savedUser;
    }
    

    public void deleteUser(Long id) throws Exception {
        log.info("Deleting user with ID: {}", id);
        
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            throw new Exception("User not found");
        }
        
        try {
            userDao.deleteById(id);
            log.info("User deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new Exception("Failed to delete user: " + e.getMessage());
        }
    }
    
    

    public Optional<User> authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);
        
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: user not found: {}", username);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            log.info("Authentication successful for user: {}", username);
            return Optional.of(user);
        } else {
            log.warn("Authentication failed: invalid password for user: {}", username);
            return Optional.empty();
        }
    }
    

    public void changePassword(Long id, String currentPassword, String newPassword) throws Exception {
        log.info("Changing password for user ID: {}", id);
        
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            throw new Exception("User not found");
        }
        
        User user = userOpt.get();
        
        if (!BCrypt.checkpw(currentPassword, user.getPasswordHash())) {
            log.warn("Current password is incorrect for user ID: {}", id);
            throw new Exception("Current password is incorrect");
        }
        
        user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        
        User savedUser = userDao.save(user);
        if (savedUser == null) {
            log.error("Failed to update user password");
            throw new Exception("Failed to update user password");
        }
        
        log.info("Password changed successfully for user ID: {}", id);
    }
}
