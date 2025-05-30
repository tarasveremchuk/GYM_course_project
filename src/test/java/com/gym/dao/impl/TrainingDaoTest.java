package com.gym.dao.impl;

import com.gym.model.Staff;
import com.gym.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrainingDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Training> mockTypedQuery;
    
    @Spy
    @InjectMocks
    private TrainingDao trainingDao;
    
    private Training testTraining;
    private Staff testTrainer;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testTrainer = new Staff();
        testTrainer.setId(1L);
        testTrainer.setFullName("John Smith");
        
        
        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setName("Yoga Class");
        testTraining.setDescription("Beginner-friendly yoga session");
        testTraining.setCapacity(15);
        testTraining.setTrainer(testTrainer);
        testTraining.setScheduledAt(LocalDateTime.now().plusDays(1));
        testTraining.setDurationMinutes(60);
        
        
        doReturn(mockEm).when(trainingDao).getEntityManager();
    }
    
    @Test
    void testFindAll() {
        
        List<Training> expectedTrainings = Arrays.asList(testTraining);
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainings);
        
        
        List<Training> result = trainingDao.findAll();
        
        
        assertEquals(expectedTrainings, result);
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindUpcomingTrainings() {
        
        List<Training> expectedTrainings = Arrays.asList(testTraining);
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("now"), any(LocalDateTime.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainings);
        
        
        List<Training> result = trainingDao.findUpcomingTrainings();
        
        
        assertEquals(expectedTrainings, result);
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter(eq("now"), any(LocalDateTime.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByTrainerId() {
        
        List<Training> expectedTrainings = Arrays.asList(testTraining);
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("trainerId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainings);
        
        
        List<Training> result = trainingDao.findByTrainerId(1L);
        
        
        assertEquals(expectedTrainings, result);
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter("trainerId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindAvailableTrainings() {
        
        List<Training> expectedTrainings = Arrays.asList(testTraining);
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("now"), any(LocalDateTime.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainings);
        
        
        List<Training> result = trainingDao.findAvailableTrainings();
        
        
        assertEquals(expectedTrainings, result);
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter(eq("now"), any(LocalDateTime.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindAll_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Training> result = trainingDao.findAll();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindUpcomingTrainings_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("now"), any(LocalDateTime.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Training> result = trainingDao.findUpcomingTrainings();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter(eq("now"), any(LocalDateTime.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByTrainerId_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("trainerId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Training> result = trainingDao.findByTrainerId(999L);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter("trainerId", 999L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindAvailableTrainings_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Training.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("now"), any(LocalDateTime.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Training> result = trainingDao.findAvailableTrainings();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Training.class));
        verify(mockTypedQuery).setParameter(eq("now"), any(LocalDateTime.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
}
