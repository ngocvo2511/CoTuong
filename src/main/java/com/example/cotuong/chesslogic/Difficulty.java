package com.example.cotuong.chesslogic;

public enum Difficulty {
    EASY(1, "Dễ"),
    MEDIUM(2, "Trung bình"),
    HARD(3, "Khó");

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

