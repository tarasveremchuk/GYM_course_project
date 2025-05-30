package com.gym.service;

import com.gym.dao.impl.UserDao;
import com.gym.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String plainPassword = "password123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPasswordHash(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        testUser.setRole(User.UserRole.admin);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        
        List<User> expectedUsers = Arrays.asList(testUser, new User());
        when(userDao.findAll()).thenReturn(expectedUsers);

        
        List<User> actualUsers = userService.getAllUsers();

        
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userDao).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.of(testUser));

        
        Optional<User> result = userService.getUserById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userDao).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<User> result = userService.getUserById(999L);

        
        assertFalse(result.isPresent());
        verify(userDao).findById(999L);
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        
        Optional<User> result = userService.getUserByUsername("testuser");

        
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userDao).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());

        
        Optional<User> result = userService.getUserByUsername("nonexistent");

        
        assertFalse(result.isPresent());
        verify(userDao).findByUsername("nonexistent");
    }

    @Test
    void createUser_ShouldHashPasswordAndSaveUser() throws Exception {
        
        String username = "newuser";
        String password = "plainPassword";
        User.UserRole role = User.UserRole.client;
        
        when(userDao.existsByUsername(anyString())).thenReturn(false);
        when(userDao.save(any(User.class))).thenReturn(testUser);

        
        User result = userService.createUser(username, password, role);

        
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userDao).existsByUsername(username);
        verify(userDao).save(any(User.class));
        
        
        verify(userDao).save(argThat(user -> 
            BCrypt.checkpw(password, user.getPasswordHash())
        ));
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrowException() {
        
        String username = "existinguser";
        String password = "password";
        User.UserRole role = User.UserRole.client;
        
        when(userDao.existsByUsername(username)).thenReturn(true);

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.createUser(username, password, role);
        });
        
        assertEquals("Username already exists", exception.getMessage());
        verify(userDao).existsByUsername(username);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void createUser_WhenSaveFails_ShouldThrowException() {
        
        String username = "newuser";
        String password = "password";
        User.UserRole role = User.UserRole.client;
        
        when(userDao.existsByUsername(anyString())).thenReturn(false);
        when(userDao.save(any(User.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.createUser(username, password, role);
        });
        
        assertEquals("Failed to save user", exception.getMessage());
        verify(userDao).existsByUsername(username);
        verify(userDao).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() throws Exception {
        
        Long userId = 1L;
        String newUsername = "updateduser";
        String newPassword = "updatedpassword";
        User.UserRole newRole = User.UserRole.client;
        
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(newUsername);
        updatedUser.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        updatedUser.setRole(newRole);
        
        when(userDao.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userDao.save(any(User.class))).thenReturn(updatedUser);
        
        
        User result = userService.updateUser(userId, newUsername, newPassword, newRole);

        
        assertEquals(newUsername, result.getUsername());
        assertEquals(newRole, result.getRole());
        verify(userDao).findById(userId);
        verify(userDao).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateUser(999L, "newUsername", "newPassword", User.UserRole.client);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userDao).findById(999L);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void changePassword_WhenUserExists_ShouldUpdatePassword() throws Exception {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userDao.save(any(User.class))).thenReturn(testUser);

        String currentPassword = plainPassword;
        String newPassword = "newPassword123";
        
        
        userService.changePassword(1L, currentPassword, newPassword);

        
        verify(userDao).findById(1L);
        verify(userDao).save(argThat(user -> 
            BCrypt.checkpw(newPassword, user.getPasswordHash())
        ));
    }

    @Test
    void changePassword_WhenUserDoesNotExist_ShouldThrowException() {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword(999L, "currentPassword", "newPassword");
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userDao).findById(999L);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_WhenCredentialsAreValid_ShouldReturnUser() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        
        Optional<User> result = userService.authenticate("testuser", plainPassword);

        
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userDao).findByUsername("testuser");
    }

    @Test
    void authenticateUser_WhenUsernameIsInvalid_ShouldReturnEmptyOptional() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());

        
        Optional<User> result = userService.authenticate("nonexistent", plainPassword);

        
        assertFalse(result.isPresent());
        verify(userDao).findByUsername("nonexistent");
    }

    @Test
    void authenticateUser_WhenPasswordIsInvalid_ShouldReturnEmptyOptional() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        
        Optional<User> result = userService.authenticate("testuser", "wrongpassword");

        
        assertFalse(result.isPresent());
        verify(userDao).findByUsername("testuser");
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() throws Exception {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.of(testUser));
        doNothing().when(userDao).deleteById(anyLong());

        
        userService.deleteUser(1L);

        
        verify(userDao).findById(1L);
        verify(userDao).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.deleteUser(999L);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userDao).findById(999L);
        verify(userDao, never()).deleteById(anyLong());
    }
}
