package com.booking.test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Passenger;

class BookingTest {

    @Test
    void testBookingSettersAndGetters() {
        Booking booking = new Booking();

        booking.setId(1L);
        booking.setPnr("PNR123");
        booking.setEmail("test@example.com");
        booking.setName("John Doe");
        booking.setTimeOfBooking(LocalDateTime.now());
        booking.setTimeOfJourney(LocalDateTime.now().plusDays(1));
        booking.setNumberOfSeats(2);
        booking.setTotalPrice(500.25);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setFlightId("FL123");
        booking.setSeatNumbers(List.of("1A", "1B"));

        Passenger p1 = new Passenger();
        p1.setName("Passenger 1");
        p1.setGender("M");
        p1.setAge(30);

        booking.setPassengers(List.of(p1));

        assertEquals(1L, booking.getId());
        assertEquals("PNR123", booking.getPnr());
        assertEquals("test@example.com", booking.getEmail());
        assertEquals("John Doe", booking.getName());
        assertEquals(2, booking.getNumberOfSeats());
        assertEquals(500.25, booking.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals("FL123", booking.getFlightId());
        assertEquals(2, booking.getSeatNumbers().size());
        assertEquals(1, booking.getPassengers().size());
    }

    @Test
    void testBookingPassengersAssignment() {
        Booking booking = new Booking();

        Passenger p1 = new Passenger();
        p1.setName("Alice");
        p1.setGender("F");
        p1.setAge(22);

        Passenger p2 = new Passenger();
        p2.setName("Bob");
        p2.setGender("M");
        p2.setAge(25);

        booking.setPassengers(List.of(p1, p2));

        assertEquals(2, booking.getPassengers().size());
        assertEquals("Alice", booking.getPassengers().get(0).getName());
        assertEquals("Bob", booking.getPassengers().get(1).getName());
    }
}

