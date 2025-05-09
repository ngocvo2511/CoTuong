package com.example.cotuong.chesslogic;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Player {
    NONE,
    RED,
    BLACK;

    @JsonValue
    public String getValue() {
        return name();
    }

    public Player opponent() {
        switch (this) {
            case RED:
                return BLACK;
            case BLACK:
                return RED;
            default:
                return NONE;
        }
    }
}
