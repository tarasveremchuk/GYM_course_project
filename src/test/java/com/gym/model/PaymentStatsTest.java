package com.gym.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatsTest {

    private PaymentStats paymentStats;
    private LocalDate today;
    private LocalDate yesterday;

    @BeforeEach
    void setUp() {
        paymentStats = new PaymentStats();
        today = LocalDate.now();
        yesterday = today.minusDays(1);
    }

    @Test
    void testDefaultConstructor() {
        
        PaymentStats stats = new PaymentStats();
        
        
        assertNotNull(stats.getDailyAmounts());
        assertTrue(stats.getDailyAmounts().isEmpty());
        assertNotNull(stats.getMembershipCounts());
        assertTrue(stats.getMembershipCounts().isEmpty());
        assertEquals(BigDecimal.ZERO, stats.getTotalAmount());
    }

    @Test
    void testAddDailyAmount() {
        
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("500.00"));
        paymentStats.addDailyAmount(today, "Credit Card", new BigDecimal("300.00"));
        paymentStats.addDailyAmount(yesterday, "Cash", new BigDecimal("200.00"));
        
        
        assertEquals(2, paymentStats.getDailyAmounts().size());
        assertEquals(new BigDecimal("1000.00"), paymentStats.getTotalAmount());
        
        
        Map<String, BigDecimal> todayAmounts = paymentStats.getDailyAmountsByMethod(today);
        assertEquals(2, todayAmounts.size());
        assertEquals(new BigDecimal("500.00"), todayAmounts.get("Cash"));
        assertEquals(new BigDecimal("300.00"), todayAmounts.get("Credit Card"));
        
        
        Map<String, BigDecimal> yesterdayAmounts = paymentStats.getDailyAmountsByMethod(yesterday);
        assertEquals(1, yesterdayAmounts.size());
        assertEquals(new BigDecimal("200.00"), yesterdayAmounts.get("Cash"));
    }

    @Test
    void testAddDailyAmountWithSameMethod() {
        
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("300.00"));
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("200.00"));
        
        
        Map<String, BigDecimal> todayAmounts = paymentStats.getDailyAmountsByMethod(today);
        assertEquals(1, todayAmounts.size());
        assertEquals(new BigDecimal("500.00"), todayAmounts.get("Cash"));
        assertEquals(new BigDecimal("500.00"), paymentStats.getTotalAmount());
    }

    @Test
    void testGetTotalsByPaymentMethod() {
        
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("500.00"));
        paymentStats.addDailyAmount(today, "Credit Card", new BigDecimal("300.00"));
        paymentStats.addDailyAmount(yesterday, "Cash", new BigDecimal("200.00"));
        paymentStats.addDailyAmount(yesterday, "Bank Transfer", new BigDecimal("1000.00"));
        
        
        Map<String, BigDecimal> totals = paymentStats.getTotalsByPaymentMethod();
        
        
        assertEquals(3, totals.size());
        assertEquals(new BigDecimal("700.00"), totals.get("Cash")); 
        assertEquals(new BigDecimal("300.00"), totals.get("Credit Card"));
        assertEquals(new BigDecimal("1000.00"), totals.get("Bank Transfer"));
    }

    @Test
    void testGetDates() {
        
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("100.00"));
        paymentStats.addDailyAmount(yesterday, "Cash", new BigDecimal("200.00"));
        
        
        List<LocalDate> dates = paymentStats.getDates();
        
        
        assertEquals(2, dates.size());
        assertEquals(yesterday, dates.get(0)); 
        assertEquals(today, dates.get(1));
    }

    @Test
    void testGetDailyTotal() {
        
        paymentStats.addDailyAmount(today, "Cash", new BigDecimal("500.00"));
        paymentStats.addDailyAmount(today, "Credit Card", new BigDecimal("300.00"));
        paymentStats.addDailyAmount(today, "Bank Transfer", new BigDecimal("1000.00"));
        
        
        BigDecimal dailyTotal = paymentStats.getDailyTotal(today);
        
        
        assertEquals(new BigDecimal("1800.00"), dailyTotal);
    }

    @Test
    void testGetDailyTotalForNonExistentDate() {
        
        BigDecimal dailyTotal = paymentStats.getDailyTotal(today);
        
        
        assertEquals(BigDecimal.ZERO, dailyTotal);
    }

    @Test
    void testAddMembershipCount() {
        
        paymentStats.addMembershipCount(today, "unlimited", 2);
        paymentStats.addMembershipCount(today, "limited", 3);
        paymentStats.addMembershipCount(yesterday, "unlimited", 1);
        
        
        Map<String, Integer> todayCounts = paymentStats.getMembershipCountsByType(today);
        assertEquals(2, todayCounts.size());
        assertEquals(2, todayCounts.get("unlimited"));
        assertEquals(3, todayCounts.get("limited"));
        
        Map<String, Integer> yesterdayCounts = paymentStats.getMembershipCountsByType(yesterday);
        assertEquals(1, yesterdayCounts.size());
        assertEquals(1, yesterdayCounts.get("unlimited"));
    }

    @Test
    void testAddMembershipCountWithSameType() {
        
        paymentStats.addMembershipCount(today, "unlimited", 2);
        paymentStats.addMembershipCount(today, "unlimited", 3);
        
        
        Map<String, Integer> todayCounts = paymentStats.getMembershipCountsByType(today);
        assertEquals(1, todayCounts.size());
        assertEquals(5, todayCounts.get("unlimited"));
    }

    @Test
    void testGetTotalMembershipCountsByType() {
        
        paymentStats.addMembershipCount(today, "unlimited", 2);
        paymentStats.addMembershipCount(today, "limited", 3);
        paymentStats.addMembershipCount(yesterday, "unlimited", 1);
        paymentStats.addMembershipCount(yesterday, "fixed", 4);
        
        
        Map<String, Integer> totalCounts = paymentStats.getTotalMembershipCountsByType();
        
        
        assertEquals(3, totalCounts.size());
        assertEquals(3, totalCounts.get("unlimited")); 
        assertEquals(3, totalCounts.get("limited"));
        assertEquals(4, totalCounts.get("fixed"));
    }

    @Test
    void testGetMembershipCountsByTypeForNonExistentDate() {
        
        Map<String, Integer> counts = paymentStats.getMembershipCountsByType(today);
        
        
        assertTrue(counts.isEmpty());
    }
}
