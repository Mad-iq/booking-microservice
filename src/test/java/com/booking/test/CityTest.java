package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.booking.model.City;

class CityTest {

    @Test
    void testCityEnumValues() {
        City[] values = City.values();

        assertEquals(7, values.length);
        assertTrue(List.of(values).contains(City.DELHI));
        assertTrue(List.of(values).contains(City.MUMBAI));
        assertTrue(List.of(values).contains(City.KOLKATA));
        assertTrue(List.of(values).contains(City.CHENNAI));
        assertTrue(List.of(values).contains(City.BENGALURU));
        assertTrue(List.of(values).contains(City.HYDERABAD));
        assertTrue(List.of(values).contains(City.PUNE));
    }

    @Test
    void testCityEnumValueOf() {
        assertEquals(City.DELHI, City.valueOf("DELHI"));
        assertEquals(City.MUMBAI, City.valueOf("MUMBAI"));
    }

    @Test
    void testCityEnumToString() {
        assertEquals("DELHI", City.DELHI.toString());
        assertEquals("PUNE", City.PUNE.toString());
    }
}

