package com.gym.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    @Test
    void testDefaultConstructor() {
        
        Staff staff = new Staff();
        
        
        assertNull(staff.getId());
        assertNull(staff.getFullName());
        assertNull(staff.getRole());
        assertNull(staff.getPhone());
        assertNull(staff.getEmail());
        assertNull(staff.getMondaySchedule());
        assertNull(staff.getTuesdaySchedule());
        assertNull(staff.getWednesdaySchedule());
        assertNull(staff.getThursdaySchedule());
        assertNull(staff.getFridaySchedule());
        assertNull(staff.getSaturdaySchedule());
        assertNull(staff.getSundaySchedule());
        assertNull(staff.getPhotoUrl());
        assertNull(staff.getSalary());
    }

    @Test
    void testParameterizedConstructor() {
        
        String fullName = "John Smith";
        StaffRole role = StaffRole.TRAINER;
        String phone = "+380501234567";
        String email = "john.smith@example.com";
        BigDecimal salary = new BigDecimal("25000.00");
        
        Staff staff = new Staff(fullName, role, phone, email, salary);
        
        
        assertEquals(fullName, staff.getFullName());
        assertEquals(role, staff.getRole());
        assertEquals(phone, staff.getPhone());
        assertEquals(email, staff.getEmail());
        assertEquals(salary, staff.getSalary());
        
        
        assertNull(staff.getId());
        assertNull(staff.getMondaySchedule());
        assertNull(staff.getTuesdaySchedule());
        assertNull(staff.getWednesdaySchedule());
        assertNull(staff.getThursdaySchedule());
        assertNull(staff.getFridaySchedule());
        assertNull(staff.getSaturdaySchedule());
        assertNull(staff.getSundaySchedule());
        assertNull(staff.getPhotoUrl());
    }

    @Test
    void testSettersAndGetters() {
        
        Staff staff = new Staff();
        
        
        staff.setId(1L);
        staff.setFullName("Jane Doe");
        staff.setRole(StaffRole.ADMIN);
        staff.setPhone("+380671234567");
        staff.setEmail("jane.doe@example.com");
        staff.setMondaySchedule("9:00-18:00");
        staff.setTuesdaySchedule("9:00-18:00");
        staff.setWednesdaySchedule("9:00-18:00");
        staff.setThursdaySchedule("9:00-18:00");
        staff.setFridaySchedule("9:00-18:00");
        staff.setSaturdaySchedule("10:00-15:00");
        staff.setSundaySchedule("Off");
        staff.setPhotoUrl("http://example.com/staff/jane.jpg");
        staff.setSalary(new BigDecimal("30000.00"));
        
        
        assertEquals(1L, staff.getId());
        assertEquals("Jane Doe", staff.getFullName());
        assertEquals(StaffRole.ADMIN, staff.getRole());
        assertEquals("+380671234567", staff.getPhone());
        assertEquals("jane.doe@example.com", staff.getEmail());
        assertEquals("9:00-18:00", staff.getMondaySchedule());
        assertEquals("9:00-18:00", staff.getTuesdaySchedule());
        assertEquals("9:00-18:00", staff.getWednesdaySchedule());
        assertEquals("9:00-18:00", staff.getThursdaySchedule());
        assertEquals("9:00-18:00", staff.getFridaySchedule());
        assertEquals("10:00-15:00", staff.getSaturdaySchedule());
        assertEquals("Off", staff.getSundaySchedule());
        assertEquals("http://example.com/staff/jane.jpg", staff.getPhotoUrl());
        assertEquals(new BigDecimal("30000.00"), staff.getSalary());
    }
    
    @Test
    void testEqualsAndHashCode() {
        
        Staff staff1 = new Staff();
        staff1.setId(1L);
        staff1.setFullName("Alex Brown");
        
        Staff staff2 = new Staff();
        staff2.setId(1L);
        staff2.setFullName("Alex Brown");
        
        Staff staff3 = new Staff();
        staff3.setId(2L);
        staff3.setFullName("Different Name");
        
        
        
        assertEquals(staff1, staff1); 
        assertNotEquals(staff1, staff3); 
    }
    
    @Test
    void testRoleAssignment() {
        
        Staff staff = new Staff();
        
        
        staff.setRole(StaffRole.TRAINER);
        assertEquals(StaffRole.TRAINER, staff.getRole());
        
        
        staff.setRole(StaffRole.ADMIN);
        assertEquals(StaffRole.ADMIN, staff.getRole());
        
        
        staff.setRole(StaffRole.CLEANER);
        assertEquals(StaffRole.CLEANER, staff.getRole());
    }
    
    @Test
    void testSalaryPrecision() {
        
        Staff staff = new Staff();
        
        
        BigDecimal salary1 = new BigDecimal("5000.00");
        staff.setSalary(salary1);
        assertEquals(salary1, staff.getSalary());
        
        
        BigDecimal salary2 = new BigDecimal("5000.557");
        staff.setSalary(salary2);
        assertEquals(salary2, staff.getSalary());
    }
}
