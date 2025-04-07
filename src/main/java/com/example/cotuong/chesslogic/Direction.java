package com.example.cotuong.chesslogic;

public class Direction {
    public static final Direction NORTH = new Direction(-1, 0);
    public static final Direction SOUTH = new Direction(1, 0);
    public static final Direction EAST = new Direction(0, 1);
    public static final Direction WEST = new Direction(0, -1);
    public static final Direction NORTH_EAST = new Direction(-1, 1);
    public static final Direction NORTH_WEST = new Direction(-1, -1);
    public static final Direction SOUTH_EAST = new Direction(1, 1);
    public static final Direction SOUTH_WEST = new Direction(1, -1);

    private final int rowDelta;
    private final int columnDelta;

    public Direction(int rowDelta, int columnDelta) {
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
    }

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColumnDelta() {
        return columnDelta;
    }

    public Direction add(Direction other) {
        return new Direction(this.rowDelta + other.rowDelta, this.columnDelta + other.columnDelta);
    }

    public Direction scale(int scale) {
        return new Direction(scale * this.rowDelta, scale * this.columnDelta);
    }
}
