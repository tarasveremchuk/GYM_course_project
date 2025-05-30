package com.gym.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StaffDTOTest {

    @Test
    void testDefaultConstructor() {
        StaffDTO staffDTO = new StaffDTO();
        
        assertNull(staffDTO.getId());
        assertNull(staffDTO.getFullName());
        assertNull(staffDTO.getRole());
        assertNull(staffDTO.getPhone());
        assertNull(staffDTO.getEmail());
        assertNull(staffDTO.getSalary());
        assertNull(staffDTO.getPhotoUrl());
        
        assertNull(staffDTO.getMondaySchedule());
        assertNull(staffDTO.getTuesdaySchedule());
        assertNull(staffDTO.getWednesdaySchedule());
        assertNull(staffDTO.getThursdaySchedule());
        assertNull(staffDTO.getFridaySchedule());
        assertNull(staffDTO.getSaturdaySchedule());
        assertNull(staffDTO.getSundaySchedule());
        
        assertNotNull(staffDTO.fullNameProperty());
        assertNotNull(staffDTO.roleProperty());
        assertNotNull(staffDTO.phoneProperty());
        assertNotNull(staffDTO.emailProperty());
        assertNotNull(staffDTO.salaryProperty());
        assertNotNull(staffDTO.photoUrlProperty());
        
        assertNotNull(staffDTO.mondayScheduleProperty());
        assertNotNull(staffDTO.tuesdayScheduleProperty());
        assertNotNull(staffDTO.wednesdayScheduleProperty());
        assertNotNull(staffDTO.thursdayScheduleProperty());
        assertNotNull(staffDTO.fridayScheduleProperty());
        assertNotNull(staffDTO.saturdayScheduleProperty());
        assertNotNull(staffDTO.sundayScheduleProperty());
    }

    @Test
    void testParameterizedConstructor() {
        String fullName = "John Smith";
        StaffRole role = StaffRole.TRAINER;
        String phone = "+380501234567";
        String email = "john.smith@example.com";
        BigDecimal salary = new BigDecimal("25000.00");
        
        StaffDTO staffDTO = new StaffDTO(fullName, role, phone, email, salary);
        
        assertEquals(fullName, staffDTO.getFullName());
        assertEquals(role, staffDTO.getRole());
        assertEquals(phone, staffDTO.getPhone());
        assertEquals(email, staffDTO.getEmail());
        assertEquals(salary, staffDTO.getSalary());
        
        assertEquals(fullName, staffDTO.fullNameProperty().get());
        assertEquals(role, staffDTO.roleProperty().get());
        assertEquals(phone, staffDTO.phoneProperty().get());
        assertEquals(email, staffDTO.emailProperty().get());
        assertEquals(salary, staffDTO.salaryProperty().get());
        
        assertNull(staffDTO.getId());
        assertNull(staffDTO.getPhotoUrl());
        assertNull(staffDTO.getMondaySchedule());
        assertNull(staffDTO.getTuesdaySchedule());
        assertNull(staffDTO.getWednesdaySchedule());
        assertNull(staffDTO.getThursdaySchedule());
        assertNull(staffDTO.getFridaySchedule());
        assertNull(staffDTO.getSaturdaySchedule());
        assertNull(staffDTO.getSundaySchedule());
    }

    @Test
    void testSettersAndGetters() {
        StaffDTO staffDTO = new StaffDTO();
        
        staffDTO.setId(1L);
        staffDTO.setFullName("Jane Doe");
        staffDTO.setRole(StaffRole.ADMIN);
        staffDTO.setPhone("+380671234567");
        staffDTO.setEmail("jane.doe@example.com");
        staffDTO.setSalary(new BigDecimal("30000.00"));
        staffDTO.setPhotoUrl("http://example.com/staff/jane.jpg");
        
        staffDTO.setMondaySchedule("9:00-18:00");
        staffDTO.setTuesdaySchedule("9:00-18:00");
        staffDTO.setWednesdaySchedule("9:00-18:00");
        staffDTO.setThursdaySchedule("9:00-18:00");
        staffDTO.setFridaySchedule("9:00-18:00");
        staffDTO.setSaturdaySchedule("10:00-15:00");
        staffDTO.setSundaySchedule("Off");
        
        assertEquals(1L, staffDTO.getId());
        assertEquals("Jane Doe", staffDTO.getFullName());
        assertEquals(StaffRole.ADMIN, staffDTO.getRole());
        assertEquals("+380671234567", staffDTO.getPhone());
        assertEquals("jane.doe@example.com", staffDTO.getEmail());
        assertEquals(new BigDecimal("30000.00"), staffDTO.getSalary());
        assertEquals("http://example.com/staff/jane.jpg", staffDTO.getPhotoUrl());
        
        assertEquals("9:00-18:00", staffDTO.getMondaySchedule());
        assertEquals("9:00-18:00", staffDTO.getTuesdaySchedule());
        assertEquals("9:00-18:00", staffDTO.getWednesdaySchedule());
        assertEquals("9:00-18:00", staffDTO.getThursdaySchedule());
        assertEquals("9:00-18:00", staffDTO.getFridaySchedule());
        assertEquals("10:00-15:00", staffDTO.getSaturdaySchedule());
        assertEquals("Off", staffDTO.getSundaySchedule());
        
        assertEquals("Jane Doe", staffDTO.fullNameProperty().get());
        assertEquals(StaffRole.ADMIN, staffDTO.roleProperty().get());
        assertEquals("+380671234567", staffDTO.phoneProperty().get());
        assertEquals("jane.doe@example.com", staffDTO.emailProperty().get());
        assertEquals(new BigDecimal("30000.00"), staffDTO.salaryProperty().get());
        assertEquals("http://example.com/staff/jane.jpg", staffDTO.photoUrlProperty().get());
        
        assertEquals("9:00-18:00", staffDTO.mondayScheduleProperty().get());
        assertEquals("9:00-18:00", staffDTO.tuesdayScheduleProperty().get());
        assertEquals("9:00-18:00", staffDTO.wednesdayScheduleProperty().get());
        assertEquals("9:00-18:00", staffDTO.thursdayScheduleProperty().get());
        assertEquals("9:00-18:00", staffDTO.fridayScheduleProperty().get());
        assertEquals("10:00-15:00", staffDTO.saturdayScheduleProperty().get());
        assertEquals("Off", staffDTO.sundayScheduleProperty().get());
    }

    @Test
    void testJavaFxPropertyListeners() {
        StaffDTO staffDTO = new StaffDTO();
        
        staffDTO.fullNameProperty().set("Maria Garcia");
        staffDTO.roleProperty().set(StaffRole.TRAINER);
        staffDTO.phoneProperty().set("+380631234567");
        staffDTO.emailProperty().set("maria.garcia@example.com");
        staffDTO.salaryProperty().set(new BigDecimal("27500.00"));
        staffDTO.photoUrlProperty().set("http://example.com/staff/maria.jpg");
        
        staffDTO.mondayScheduleProperty().set("8:00-17:00");
        staffDTO.tuesdayScheduleProperty().set("8:00-17:00");
        staffDTO.wednesdayScheduleProperty().set("8:00-17:00");
        staffDTO.thursdayScheduleProperty().set("8:00-17:00");
        staffDTO.fridayScheduleProperty().set("8:00-17:00");
        staffDTO.saturdayScheduleProperty().set("Off");
        staffDTO.sundayScheduleProperty().set("Off");
        
        assertEquals("Maria Garcia", staffDTO.getFullName());
        assertEquals(StaffRole.TRAINER, staffDTO.getRole());
        assertEquals("+380631234567", staffDTO.getPhone());
        assertEquals("maria.garcia@example.com", staffDTO.getEmail());
        assertEquals(new BigDecimal("27500.00"), staffDTO.getSalary());
        assertEquals("http://example.com/staff/maria.jpg", staffDTO.getPhotoUrl());
        
        assertEquals("8:00-17:00", staffDTO.getMondaySchedule());
        assertEquals("8:00-17:00", staffDTO.getTuesdaySchedule());
        assertEquals("8:00-17:00", staffDTO.getWednesdaySchedule());
        assertEquals("8:00-17:00", staffDTO.getThursdaySchedule());
        assertEquals("8:00-17:00", staffDTO.getFridaySchedule());
        assertEquals("Off", staffDTO.getSaturdaySchedule());
        assertEquals("Off", staffDTO.getSundaySchedule());
    }
}
