package com.example.cotuong.chesslogic.pieces;

import com.example.cotuong.chesslogic.*;
import java.util.List;

public abstract class Piece {
    public Player bottomPlayer;
    protected PieceType type;
    protected Player color;

    public PieceType getType() {
        return type;
    }

    public Player getColor() {
        return color;
    }

    public boolean hasMoved = false;
    public abstract Piece copy();

    public abstract List<Move> getMoves(Position from, Board board);

    public boolean canCaptureOpponentGeneral(Position from, Board board) {
        return getMoves(from, board).stream().anyMatch(move -> {
            Piece piece = board.get(move.getToPos());
            return piece != null && piece.getType() == PieceType.GENERAL;
        });
    }

    @Override
    public String toString() {
        switch (getType()) {
            case GENERAL: return (getColor() == Player.BLACK) ? "bG" : "rG";
            case ADVISOR: return (getColor() == Player.BLACK) ? "bA" : "rA";
            case CHARIOT: return (getColor() == Player.BLACK) ? "bCh" : "rCh";
            case CANNON: return (getColor() == Player.BLACK) ? "bC" : "rC";
            case ELEPHANT: return (getColor() == Player.BLACK) ? "bE" : "rE";
            case HORSE: return (getColor() == Player.BLACK) ? "bH" : "rH";
            default: return (getColor() == Player.BLACK) ? "bS" : "rS";
        }
    }
}
