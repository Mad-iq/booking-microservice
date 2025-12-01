package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.booking.model.Passenger;

class PassengerTest {

    @Test
    void testPassengerGettersSetters() {
        Passenger p = new Passenger();

        p.setId(10L);
        p.setName("John");
        p.setGender("M");
        p.setAge(28);

        assertEquals(10L, p.getId());
        assertEquals("John", p.getName());
        assertEquals("M", p.getGender());
        assertEquals(28, p.getAge());
    }

    @Test
    void testPassengerToStringContainsFields() {
        Passenger p = new Passenger();
        p.setName("Alice");
        p.setGender("F");
        p.setAge(23);

        String toString = p.toString();
        assertTrue(toString.contains("Alice"));
        assertTrue(toString.contains("F"));
        assertTrue(toString.contains("23"));
    }
}

