package com.example.cotuong.ChessLogic;

import java.util.Objects;

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    public static boolean equals(Position left, Position right) {
        return Objects.equals(left, right);
    }

    public static boolean notEquals(Position left, Position right) {
        return !equals(left, right);
    }
    public Position add(Direction dir) {
        return new Position(this.getRow() + dir.getRowDelta(), this.getColumn() + dir.getColumnDelta());
    }
}