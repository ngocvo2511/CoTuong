package com.example.cotuong.chesslogic;

public enum Difficulty {
    NONE(0,""),
    EASY(2, "Dễ"),
    MEDIUM(3, "Thường"),
    HARD(4, "Khó");

    private final int level;
    private final String description;

    Difficulty(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }
}

