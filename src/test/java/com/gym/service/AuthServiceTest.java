package com.gym.service;

import com.gym.dao.impl.UserDao;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.StaffDao;
import com.gym.model.User;
import com.gym.model.Client;
import com.gym.model.Staff;
import com.gym.model.StaffRole;
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
public class AuthServiceTest {

    @Mock
    private UserDao userDao;
    
    @Mock
    private ClientDao clientDao;
    
    @Mock
    private StaffDao staffDao;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Client testClient;
    private Staff testStaff;
    private String plainPassword = "password123";

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Test Client");
        
        testStaff = new Staff();
        testStaff.setId(1L);
        testStaff.setFullName("Test Staff");
        testStaff.setEmail("staff@example.com");
        testStaff.setRole(StaffRole.TRAINER);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPasswordHash(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        testUser.setRole(User.UserRole.client);
        testUser.setClientId(1L);
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnTrue() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        
        boolean result = authService.login("testuser", plainPassword);

        
        assertTrue(result);
        assertEquals(testUser, authService.getCurrentUser());
        verify(userDao).findByUsername("testuser");
    }

    @Test
    void login_WhenUsernameIsInvalid_ShouldReturnFalse() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());

        
        boolean result = authService.login("nonexistent", plainPassword);

        
        assertFalse(result);
        assertNull(authService.getCurrentUser());
        verify(userDao).findByUsername("nonexistent");
    }

    @Test
    void login_WhenPasswordIsInvalid_ShouldReturnFalse() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        
        boolean result = authService.login("testuser", "wrongpassword");

        
        assertFalse(result);
        assertNull(authService.getCurrentUser());
        verify(userDao).findByUsername("testuser");
    }

    @Test
    void logout_ShouldClearCurrentUser() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);
        assertNotNull(authService.getCurrentUser());

        
        authService.logout();

        
        assertNull(authService.getCurrentUser());
    }

    @Test
    void isAuthenticated_WhenUserIsLoggedIn_ShouldReturnTrue() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.isAuthenticated();

        
        assertTrue(result);
    }

    @Test
    void isAuthenticated_WhenNoUserIsLoggedIn_ShouldReturnFalse() {
        
        boolean result = authService.isAuthenticated();

        
        assertFalse(result);
    }

    @Test
    void hasRole_WhenUserHasRole_ShouldReturnTrue() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.hasRole(User.UserRole.client);

        
        assertTrue(result);
    }

    @Test
    void hasRole_WhenUserDoesNotHaveRole_ShouldReturnFalse() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.hasRole(User.UserRole.admin);

        
        assertFalse(result);
    }

    @Test
    void hasRole_WhenNoUserIsLoggedIn_ShouldReturnFalse() {
        
        boolean result = authService.hasRole(User.UserRole.client);

        
        assertFalse(result);
    }

    @Test
    void isAdmin_WhenUserIsAdmin_ShouldReturnTrue() {
        
        testUser.setRole(User.UserRole.admin);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.isAdmin();

        
        assertTrue(result);
    }

    @Test
    void isAdmin_WhenUserIsNotAdmin_ShouldReturnFalse() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.isAdmin();

        
        assertFalse(result);
    }

    @Test
    void isTrainer_WhenUserIsTrainer_ShouldReturnTrue() {
        
        testUser.setRole(User.UserRole.trainer);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.isTrainer();

        
        assertTrue(result);
    }

    @Test
    void isClient_WhenUserIsClient_ShouldReturnTrue() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.isClient();

        
        assertTrue(result);
    }

    @Test
    void getCurrentClient_WhenUserIsClient_ShouldReturnClient() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));
        authService.login("testuser", plainPassword);

        
        Optional<Client> result = authService.getCurrentClient();

        
        assertTrue(result.isPresent());
        assertEquals(testClient, result.get());
        verify(clientDao).findById(1L);
    }

    @Test
    void getCurrentClient_WhenUserIsNotClient_ShouldReturnEmptyOptional() {
        
        testUser.setRole(User.UserRole.admin);
        testUser.setClientId(null);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        Optional<Client> result = authService.getCurrentClient();

        
        assertFalse(result.isPresent());
        verify(clientDao, never()).findById(anyLong());
    }

    @Test
    void getCurrentStaff_WhenUserIsStaff_ShouldReturnStaff() {
        
        testUser.setRole(User.UserRole.trainer);
        testUser.setUsername("staff@example.com");
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffDao.findAll()).thenReturn(Arrays.asList(testStaff));
        authService.login("staff@example.com", plainPassword);

        
        Optional<Staff> result = authService.getCurrentStaff();

        
        assertTrue(result.isPresent());
        assertEquals(testStaff, result.get());
        verify(staffDao).findAll();
    }

    @Test
    void getCurrentStaff_WhenUserIsNotStaff_ShouldReturnEmptyOptional() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        Optional<Staff> result = authService.getCurrentStaff();

        
        assertFalse(result.isPresent());
        verify(staffDao, never()).findAll();
    }

    @Test
    void changePassword_WhenUserIsAuthenticated_ShouldChangePassword() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(userDao.save(any(User.class))).thenReturn(testUser);
        authService.login("testuser", plainPassword);

        
        boolean result = authService.changePassword(plainPassword, "newPassword123");

        
        assertTrue(result);
        verify(userDao).save(any(User.class));
    }

    @Test
    void changePassword_WhenCurrentPasswordIsIncorrect_ShouldReturnFalse() {
        
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        authService.login("testuser", plainPassword);

        
        boolean result = authService.changePassword("wrongpassword", "newPassword123");

        
        assertFalse(result);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void changePassword_WhenNoUserIsAuthenticated_ShouldReturnFalse() {
        
        boolean result = authService.changePassword(plainPassword, "newPassword123");

        
        assertFalse(result);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void registerClient_ShouldRegisterClientAndCreateUserAccount() throws Exception {
        
        when(userDao.existsByUsername(anyString())).thenReturn(false);
        when(clientDao.save(any(Client.class))).thenReturn(testClient);
        when(userDao.save(any(User.class))).thenReturn(testUser);

        
        Client result = authService.registerClient(testClient, "newuser", "password123");

        
        assertNotNull(result);
        assertEquals(testClient, result);
        verify(userDao).existsByUsername("newuser");
        verify(clientDao).save(testClient);
        verify(userDao).save(any(User.class));
    }

    @Test
    void registerClient_WhenUsernameExists_ShouldThrowException() {
        
        when(userDao.existsByUsername(anyString())).thenReturn(true);

        
        Exception exception = assertThrows(Exception.class, () -> {
            authService.registerClient(testClient, "existinguser", "password123");
        });
        
        assertEquals("Username already exists", exception.getMessage());
        verify(userDao).existsByUsername("existinguser");
        verify(clientDao, never()).save(any(Client.class));
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void registerClient_WhenClientSaveFails_ShouldThrowException() {
        
        when(userDao.existsByUsername(anyString())).thenReturn(false);
        when(clientDao.save(any(Client.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            authService.registerClient(testClient, "newuser", "password123");
        });
        
        assertEquals("Failed to save client", exception.getMessage());
        verify(userDao).existsByUsername("newuser");
        verify(clientDao).save(testClient);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void registerClient_WhenUserSaveFails_ShouldRollbackAndThrowException() {
        
        when(userDao.existsByUsername(anyString())).thenReturn(false);
        when(clientDao.save(any(Client.class))).thenReturn(testClient);
        when(userDao.save(any(User.class))).thenReturn(null);
        doNothing().when(clientDao).deleteById(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            authService.registerClient(testClient, "newuser", "password123");
        });
        
        assertEquals("Failed to save user account", exception.getMessage());
        verify(userDao).existsByUsername("newuser");
        verify(clientDao).save(testClient);
        verify(userDao).save(any(User.class));
        verify(clientDao).deleteById(testClient.getId());
    }
}
