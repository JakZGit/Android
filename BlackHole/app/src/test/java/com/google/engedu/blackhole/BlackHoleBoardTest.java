package com.google.engedu.blackhole;

import android.util.Pair;

import org.junit.Test;
import static org.junit.Assert.*;

public class BlackHoleBoardTest {
    @Test
    public void testCoordsToIndex() {
        BlackHoleBoard b = new BlackHoleBoard();
        assertEquals(0, b.coordsToIndex(0, 0));
        assertEquals(1, b.coordsToIndex(0, 1));
        assertEquals(2, b.coordsToIndex(1, 1));
        assertEquals(3, b.coordsToIndex(0, 2));
        assertEquals(4, b.coordsToIndex(1, 2));
        assertEquals(5, b.coordsToIndex(2, 2));
    }

    @Test
    public void testIndexToCoords() {
        BlackHoleBoard b = new BlackHoleBoard();
        Coordinates coords = b.indexToCoords(0);
        assertEquals(0, coords.x);
        assertEquals(0, coords.y);
        coords = b.indexToCoords(1);
        assertEquals(0, coords.x);
        assertEquals(1, coords.y);
        for (int i = 0; i < b.BOARD_SIZE; i++) {
            coords = b.indexToCoords(i);
            assertEquals(i, b.coordsToIndex(coords.x, coords.y));
        }
    }

    @Test
    public void testGetScore() {
        // TODO: Implement this test to verify that your getScore method is working.
        BlackHoleBoard b = new BlackHoleBoard();
        b.setValue(1);
    }
}
