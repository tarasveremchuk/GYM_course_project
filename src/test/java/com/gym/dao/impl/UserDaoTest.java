package com.gym.dao.impl;

import com.gym.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<User> mockTypedQuery;
    
    @Spy
    @InjectMocks
    private UserDao userDao;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("johndoe");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.UserRole.client);
        testUser.setClientId(1L);
        
        
        doReturn(mockEm).when(userDao).getEntityManager();
    }
    
    @Test
    void testFindByUsername_Found() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(testUser);
        
        
        Optional<User> result = userDao.findByUsername("johndoe");
        
        
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "johndoe");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByUsername_NotFound() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException("No user found"));
        
        
        Optional<User> result = userDao.findByUsername("nonexistent");
        
        
        assertFalse(result.isPresent());
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "nonexistent");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testExistsByUsername_True() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(testUser);
        
        
        boolean result = userDao.existsByUsername("johndoe");
        
        
        assertTrue(result);
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "johndoe");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testExistsByUsername_False() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException("No user found"));
        
        
        boolean result = userDao.existsByUsername("nonexistent");
        
        
        assertFalse(result);
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "nonexistent");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByUsername_OtherException() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));
        
        
        assertThrows(RuntimeException.class, () -> userDao.findByUsername("johndoe"));
        
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "johndoe");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByUsername_NullUsername() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), isNull())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException("No user found"));
        
        
        Optional<User> result = userDao.findByUsername(null);
        
        
        assertFalse(result.isPresent());
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", null);
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testExistsByUsername_NullUsername() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), isNull())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException("No user found"));
        
        
        boolean result = userDao.existsByUsername(null);
        
        
        assertFalse(result);
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", null);
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testExistsByUsername_WithException() {
        
        when(mockEm.createQuery(anyString(), eq(User.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("username"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));
        
        
        assertThrows(RuntimeException.class, () -> userDao.existsByUsername("johndoe"));
        
        verify(mockEm).createQuery(anyString(), eq(User.class));
        verify(mockTypedQuery).setParameter("username", "johndoe");
        verify(mockTypedQuery).getSingleResult();
        verify(mockEm).close();
    }
}
