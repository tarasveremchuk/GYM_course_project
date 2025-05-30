package com.gym.model;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StaffDTO {
    private Long id;
    private String fullName;
    private StaffRole role;
    private String phone;
    private String email;
    private BigDecimal salary;
    private String photoUrl;
    
    
    private String mondaySchedule;
    private String tuesdaySchedule;
    private String wednesdaySchedule;
    private String thursdaySchedule;
    private String fridaySchedule;
    private String saturdaySchedule;
    private String sundaySchedule;

    
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final ObjectProperty<StaffRole> roleProperty = new SimpleObjectProperty<>();
    private final StringProperty phoneProperty = new SimpleStringProperty();
    private final StringProperty emailProperty = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> salaryProperty = new SimpleObjectProperty<>();
    private final StringProperty photoUrlProperty = new SimpleStringProperty();
    
    
    private final StringProperty mondayScheduleProperty = new SimpleStringProperty();
    private final StringProperty tuesdayScheduleProperty = new SimpleStringProperty();
    private final StringProperty wednesdayScheduleProperty = new SimpleStringProperty();
    private final StringProperty thursdayScheduleProperty = new SimpleStringProperty();
    private final StringProperty fridayScheduleProperty = new SimpleStringProperty();
    private final StringProperty saturdayScheduleProperty = new SimpleStringProperty();
    private final StringProperty sundayScheduleProperty = new SimpleStringProperty();

    public StaffDTO() {
        initializeProperties();
    }

    public StaffDTO(String fullName, StaffRole role, String phone, String email, BigDecimal salary) {
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        initializeProperties();
    }

    private void initializeProperties() {
        fullNameProperty.set(fullName);
        roleProperty.set(role);
        phoneProperty.set(phone);
        emailProperty.set(email);
        salaryProperty.set(salary);
        photoUrlProperty.set(photoUrl);
        
        
        mondayScheduleProperty.set(mondaySchedule);
        tuesdayScheduleProperty.set(tuesdaySchedule);
        wednesdayScheduleProperty.set(wednesdaySchedule);
        thursdayScheduleProperty.set(thursdaySchedule);
        fridayScheduleProperty.set(fridaySchedule);
        saturdayScheduleProperty.set(saturdaySchedule);
        sundayScheduleProperty.set(sundaySchedule);

        
        fullNameProperty.addListener((obs, oldVal, newVal) -> fullName = newVal);
        roleProperty.addListener((obs, oldVal, newVal) -> role = newVal);
        phoneProperty.addListener((obs, oldVal, newVal) -> phone = newVal);
        emailProperty.addListener((obs, oldVal, newVal) -> email = newVal);
        salaryProperty.addListener((obs, oldVal, newVal) -> salary = newVal);
        photoUrlProperty.addListener((obs, oldVal, newVal) -> photoUrl = newVal);
        
        
        mondayScheduleProperty.addListener((obs, oldVal, newVal) -> mondaySchedule = newVal);
        tuesdayScheduleProperty.addListener((obs, oldVal, newVal) -> tuesdaySchedule = newVal);
        wednesdayScheduleProperty.addListener((obs, oldVal, newVal) -> wednesdaySchedule = newVal);
        thursdayScheduleProperty.addListener((obs, oldVal, newVal) -> thursdaySchedule = newVal);
        fridayScheduleProperty.addListener((obs, oldVal, newVal) -> fridaySchedule = newVal);
        saturdayScheduleProperty.addListener((obs, oldVal, newVal) -> saturdaySchedule = newVal);
        sundayScheduleProperty.addListener((obs, oldVal, newVal) -> sundaySchedule = newVal);
    }

    
    public StringProperty fullNameProperty() {
        return fullNameProperty;
    }

    public ObjectProperty<StaffRole> roleProperty() {
        return roleProperty;
    }

    public StringProperty phoneProperty() {
        return phoneProperty;
    }

    public StringProperty emailProperty() {
        return emailProperty;
    }

    public ObjectProperty<BigDecimal> salaryProperty() {
        return salaryProperty;
    }

    public StringProperty photoUrlProperty() {
        return photoUrlProperty;
    }
    
    
    public StringProperty mondayScheduleProperty() {
        return mondayScheduleProperty;
    }
    
    public StringProperty tuesdayScheduleProperty() {
        return tuesdayScheduleProperty;
    }
    
    public StringProperty wednesdayScheduleProperty() {
        return wednesdayScheduleProperty;
    }
    
    public StringProperty thursdayScheduleProperty() {
        return thursdayScheduleProperty;
    }
    
    public StringProperty fridayScheduleProperty() {
        return fridayScheduleProperty;
    }
    
    public StringProperty saturdayScheduleProperty() {
        return saturdayScheduleProperty;
    }
    
    public StringProperty sundayScheduleProperty() {
        return sundayScheduleProperty;
    }

    
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.fullNameProperty.set(fullName);
    }

    public void setRole(StaffRole role) {
        this.role = role;
        this.roleProperty.set(role);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneProperty.set(phone);
    }

    public void setEmail(String email) {
        this.email = email;
        this.emailProperty.set(email);
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
        this.salaryProperty.set(salary);
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        this.photoUrlProperty.set(photoUrl);
    }
    
    
    public void setMondaySchedule(String schedule) {
        this.mondaySchedule = schedule;
        this.mondayScheduleProperty.set(schedule);
    }
    
    public void setTuesdaySchedule(String schedule) {
        this.tuesdaySchedule = schedule;
        this.tuesdayScheduleProperty.set(schedule);
    }
    
    public void setWednesdaySchedule(String schedule) {
        this.wednesdaySchedule = schedule;
        this.wednesdayScheduleProperty.set(schedule);
    }
    
    public void setThursdaySchedule(String schedule) {
        this.thursdaySchedule = schedule;
        this.thursdayScheduleProperty.set(schedule);
    }
    
    public void setFridaySchedule(String schedule) {
        this.fridaySchedule = schedule;
        this.fridayScheduleProperty.set(schedule);
    }
    
    public void setSaturdaySchedule(String schedule) {
        this.saturdaySchedule = schedule;
        this.saturdayScheduleProperty.set(schedule);
    }
    
    public void setSundaySchedule(String schedule) {
        this.sundaySchedule = schedule;
        this.sundayScheduleProperty.set(schedule);
    }
} 