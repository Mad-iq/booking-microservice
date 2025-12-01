package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.booking.model.MealStatus;

class MealStatusTest {

    @Test
    void testMealStatusValues() {
        MealStatus[] values = MealStatus.values();
        assertEquals(2, values.length);
        assertTrue(List.of(values).contains(MealStatus.VEG));
        assertTrue(List.of(values).contains(MealStatus.NONVEG));
    }

    @Test
    void testMealStatusValueOf() {
        assertEquals(MealStatus.VEG, MealStatus.valueOf("VEG"));
        assertEquals(MealStatus.NONVEG, MealStatus.valueOf("NONVEG"));
    }

    @Test
    void testMealStatusToString() {
        assertEquals("VEG", MealStatus.VEG.toString());
        assertEquals("NONVEG", MealStatus.NONVEG.toString());
    }
}

