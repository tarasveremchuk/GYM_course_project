package com.gym.model;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "medical_notes")
    private String medicalNotes;

    @Column(name = "photo")
    private byte[] photo;

    @Convert(converter = ClientStatusConverter.class)
    @Column(name = "status", nullable = false)
    private ClientStatus status;

    @Column(name = "photo_url")
    private String photoUrl;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    
    @Transient
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    @Transient
    private final ObjectProperty<LocalDate> birthDateProperty = new SimpleObjectProperty<>();
    @Transient
    private final StringProperty phoneProperty = new SimpleStringProperty();
    @Transient
    private final StringProperty emailProperty = new SimpleStringProperty();
    @Transient
    private final ObjectProperty<ClientStatus> statusProperty = new SimpleObjectProperty<>();

    public Client() {
        this.status = ClientStatus.ACTIVE;
        this.bookings = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.visits = new ArrayList<>();
        initializeProperties();
    }

    public Client(String fullName, LocalDate birthDate, String phone, String email, ClientStatus status) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.bookings = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.visits = new ArrayList<>();
        initializeProperties();
    }

    private void initializeProperties() {
        fullNameProperty.set(fullName);
        birthDateProperty.set(birthDate);
        phoneProperty.set(phone);
        emailProperty.set(email);
        statusProperty.set(status);

        
        fullNameProperty.addListener((observable, oldValue, newValue) -> fullName = newValue);
        birthDateProperty.addListener((observable, oldValue, newValue) -> birthDate = newValue);
        phoneProperty.addListener((observable, oldValue, newValue) -> phone = newValue);
        emailProperty.addListener((observable, oldValue, newValue) -> email = newValue);
        statusProperty.addListener((observable, oldValue, newValue) -> status = newValue);
    }

    
    public StringProperty fullNameProperty() {
        return fullNameProperty;
    }

    public ObjectProperty<LocalDate> birthDateProperty() {
        return birthDateProperty;
    }

    public StringProperty phoneProperty() {
        return phoneProperty;
    }

    public StringProperty emailProperty() {
        return emailProperty;
    }

    public ObjectProperty<ClientStatus> statusProperty() {
        return statusProperty;
    }

    
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.fullNameProperty.set(fullName);
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.birthDateProperty.set(birthDate);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneProperty.set(phone);
    }

    public void setEmail(String email) {
        this.email = email;
        this.emailProperty.set(email);
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
        this.statusProperty.set(status);
    }
} 