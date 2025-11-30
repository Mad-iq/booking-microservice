package com.booking.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String pnr;                
    private String email;
    private String name;
    private LocalDateTime timeOfBooking;
    private LocalDateTime timeOfJourney;
    private int numberOfSeats;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    private String flightId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_seat_numbers", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> seatNumbers;
}
