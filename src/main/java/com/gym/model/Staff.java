package com.gym.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "staff")
@Getter
@Setter
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Convert(converter = StaffRoleConverter.class)
    @Column(nullable = false)
    private StaffRole role;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(name = "monday_schedule")
    private String mondaySchedule;

    @Column(name = "tuesday_schedule")
    private String tuesdaySchedule;

    @Column(name = "wednesday_schedule")
    private String wednesdaySchedule;

    @Column(name = "thursday_schedule")
    private String thursdaySchedule;

    @Column(name = "friday_schedule")
    private String fridaySchedule;

    @Column(name = "saturday_schedule")
    private String saturdaySchedule;

    @Column(name = "sunday_schedule")
    private String sundaySchedule;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    
    public Staff() {
    }

    
    public Staff(String fullName, StaffRole role, String phone, String email, BigDecimal salary) {
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }
} 