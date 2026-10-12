package ru.bobgroup.multythreading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArraySummerTest {

    @Test
    void sumsArrayInSeveralThreads() {
        int[] values = {10, 20, 30, 40, 50, 60, 70};

        assertEquals(280L, ArraySummer.sumInParallel(values, 3));
    }

    @Test
    void parallelAndSequentialResultsMatch() {
        int[] values = new int[10_003];
        for (int index = 0; index < values.length; index++) {
            values[index] = index % 100;
        }

        assertEquals(
                ArraySummer.sumSequentially(values),
                ArraySummer.sumInParallel(values, 8)
        );
    }

    @Test
    void handlesEmptyArray() {
        assertEquals(0L, ArraySummer.sumInParallel(new int[0], 4));
    }

    @Test
    void rejectsInvalidThreadCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ArraySummer.sumInParallel(new int[]{1, 2, 3}, 0)
        );
    }
}
