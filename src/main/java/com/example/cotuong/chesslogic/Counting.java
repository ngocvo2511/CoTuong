package com.example.cotuong.chesslogic;

import java.util.EnumMap;

public class Counting {
    private final EnumMap<PieceType, Integer> redCount = new EnumMap<>(PieceType.class);
    private final EnumMap<PieceType, Integer> blackCount = new EnumMap<>(PieceType.class);

    private int totalCount;



    public Counting() {
        for (PieceType pieceType : PieceType.values()) {
            redCount.put(pieceType, 0);
            blackCount.put(pieceType, 0);
        }
    }

    public void increment(Player color, PieceType type) {
        if (color == Player.RED) {
            redCount.put(type, redCount.get(type) + 1);
        } else if (color == Player.BLACK) {
            blackCount.put(type, blackCount.get(type) + 1);
        }

        totalCount++;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int red(PieceType type) {
        return redCount.get(type);
    }

    public int black(PieceType type) {
        return blackCount.get(type);
    }

    public int countingAttackPieces(Player color) {
        if (color == Player.RED) {
            return redCount.get(PieceType.CHARIOT)
                    + redCount.get(PieceType.CANNON)
                    + redCount.get(PieceType.HORSE)
                    + redCount.get(PieceType.SOLDIER);
        } else if (color == Player.BLACK) {
            return blackCount.get(PieceType.CHARIOT)
                    + blackCount.get(PieceType.CANNON)
                    + blackCount.get(PieceType.HORSE)
                    + blackCount.get(PieceType.SOLDIER);
        } else {
            return 0;
        }
    }
}

