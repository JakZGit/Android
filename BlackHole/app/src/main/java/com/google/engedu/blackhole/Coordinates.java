package com.google.engedu.blackhole;

public class Coordinates {
    public int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        Coordinates other = (Coordinates) o;
        return x == other.x && y == other.y;
    }
}

