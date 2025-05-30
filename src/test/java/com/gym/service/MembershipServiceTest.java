package com.gym.service;

import com.gym.dao.impl.MembershipDao;
import com.gym.model.Membership;
import com.gym.model.Client;
import com.gym.model.Membership.MembershipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    private MembershipDao membershipDao;

    @InjectMocks
    private MembershipService membershipService;

    private Membership testMembership;
    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Test Client");
        
        testMembership = new Membership();
        testMembership.setId(1L);
        testMembership.setClient(testClient);
        testMembership.setStartDate(LocalDate.now());
        testMembership.setEndDate(LocalDate.now().plusMonths(1));
        testMembership.setType(MembershipType.unlimited);
        testMembership.setPrice(100.0);
        testMembership.setPaid(true);
    }

    @Test
    void getAllMemberships_ShouldReturnAllMemberships() {
        
        List<Membership> expectedMemberships = Arrays.asList(testMembership, new Membership());
        when(membershipDao.findAll()).thenReturn(expectedMemberships);

        
        List<Membership> actualMemberships = membershipService.getAllMemberships();

        
        assertEquals(expectedMemberships.size(), actualMemberships.size());
        assertEquals(expectedMemberships, actualMemberships);
        verify(membershipDao).findAll();
    }

    @Test
    void getActiveMemberships_ShouldReturnOnlyActiveMemberships() {
        
        List<Membership> allMemberships = Arrays.asList(
            testMembership, 
            createExpiredMembership() 
        );
        when(membershipDao.findAll()).thenReturn(allMemberships);

        
        List<Membership> actualMemberships = membershipService.getActiveMemberships();

        
        assertEquals(1, actualMemberships.size());
        assertEquals(testMembership, actualMemberships.get(0));
        verify(membershipDao).findAll();
    }
    
    private Membership createExpiredMembership() {
        Membership expiredMembership = new Membership();
        expiredMembership.setId(2L);
        expiredMembership.setClient(testClient);
        expiredMembership.setStartDate(LocalDate.now().minusMonths(2));
        expiredMembership.setEndDate(LocalDate.now().minusDays(1)); 
        expiredMembership.setType(MembershipType.fixed);
        return expiredMembership;
    }

    @Test
    void getMembershipById_WhenMembershipExists_ShouldReturnMembership() {
        
        when(membershipDao.findById(anyLong())).thenReturn(Optional.of(testMembership));

        
        Optional<Membership> result = membershipService.getMembershipById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testMembership, result.get());
        verify(membershipDao).findById(1L);
    }

    @Test
    void getMembershipById_WhenMembershipDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(membershipDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<Membership> result = membershipService.getMembershipById(999L);

        
        assertFalse(result.isPresent());
        verify(membershipDao).findById(999L);
    }

    @Test
    void getActiveMembershipsByClientId_ShouldReturnClientMemberships() {
        
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(membershipDao.findActiveByClientId(anyLong())).thenReturn(expectedMemberships);

        
        List<Membership> result = membershipService.getActiveMembershipsByClientId(1L);

        
        assertEquals(expectedMemberships.size(), result.size());
        assertEquals(expectedMemberships, result);
        verify(membershipDao).findActiveByClientId(1L);
    }

    @Test
    void saveMembership_NewMembership_ShouldSaveAndReturnMembership() throws Exception {
        
        testMembership.setId(null); 
        when(membershipDao.save(any(Membership.class))).thenReturn(testMembership);

        
        Membership result = membershipService.saveMembership(testMembership);

        
        assertNotNull(result);
        assertEquals(testMembership, result);
        verify(membershipDao).save(testMembership);
    }

    @Test
    void saveMembership_ExistingMembership_ShouldUpdateAndReturnMembership() throws Exception {
        
        when(membershipDao.save(any(Membership.class))).thenReturn(testMembership);

        
        Membership result = membershipService.saveMembership(testMembership);

        
        assertNotNull(result);
        assertEquals(testMembership, result);
        verify(membershipDao).save(testMembership);
    }

    @Test
    void saveMembership_WhenSaveFails_ShouldThrowException() {
        
        when(membershipDao.save(any(Membership.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            membershipService.saveMembership(testMembership);
        });
        
        assertEquals("Failed to save membership", exception.getMessage());
        verify(membershipDao).save(any(Membership.class));
    }

    @Test
    void getExpiringMemberships_ShouldReturnExpiringMemberships() {
        
        LocalDate expiryDate = LocalDate.now().plusWeeks(2);
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(membershipDao.findExpiringMemberships(any(LocalDate.class))).thenReturn(expectedMemberships);

        
        List<Membership> result = membershipService.getExpiringMemberships(expiryDate);

        
        assertEquals(expectedMemberships.size(), result.size());
        assertEquals(expectedMemberships, result);
        verify(membershipDao).findExpiringMemberships(expiryDate);
    }

    @Test
    void getMembershipsByType_ShouldReturnMembershipsOfSpecifiedType() {
        
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(membershipDao.findByType(any(MembershipType.class))).thenReturn(expectedMemberships);

        
        List<Membership> result = membershipService.getMembershipsByType(MembershipType.unlimited);

        
        assertEquals(expectedMemberships.size(), result.size());
        assertEquals(expectedMemberships, result);
        verify(membershipDao).findByType(MembershipType.unlimited);
    }

    @Test
    void markAsPaid_WhenMembershipExists_ShouldMarkAsPaid() throws Exception {
        
        testMembership.setPaid(false);
        when(membershipDao.findById(anyLong())).thenReturn(Optional.of(testMembership));
        when(membershipDao.save(any(Membership.class))).thenReturn(testMembership);

        
        Membership result = membershipService.markAsPaid(1L);

        
        assertTrue(result.isPaid());
        verify(membershipDao).findById(1L);
        verify(membershipDao).save(testMembership);
    }

    @Test
    void markAsPaid_WhenMembershipDoesNotExist_ShouldThrowException() {
        
        when(membershipDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            membershipService.markAsPaid(999L);
        });
        
        assertEquals("Membership not found with ID: 999", exception.getMessage());
        verify(membershipDao).findById(999L);
        verify(membershipDao, never()).save(any(Membership.class));
    }

    @Test
    void deleteMembership_ShouldCallDaoDeleteMethod() throws Exception {
        
        doNothing().when(membershipDao).deleteById(anyLong());

        
        membershipService.deleteMembership(1L);

        
        verify(membershipDao).deleteById(1L);
    }

    @Test
    void deleteMembership_WhenDaoThrowsException_ShouldPropagateException() {
        
        doThrow(new RuntimeException("Database error")).when(membershipDao).deleteById(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            membershipService.deleteMembership(1L);
        });
        
        verify(membershipDao).deleteById(1L);
    }
    
    @Test
    void getUnpaidMemberships_ShouldReturnOnlyUnpaidMemberships() {
        
        Membership unpaidMembership = new Membership();
        unpaidMembership.setId(2L);
        unpaidMembership.setPaid(false);
        
        List<Membership> allMemberships = Arrays.asList(
            testMembership, 
            unpaidMembership 
        );
        when(membershipDao.findAll()).thenReturn(allMemberships);

        
        List<Membership> result = membershipService.getUnpaidMemberships();

        
        assertEquals(1, result.size());
        assertEquals(unpaidMembership, result.get(0));
        verify(membershipDao).findAll();
    }
}
