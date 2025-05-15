package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.pieces.Piece;

public class MoveRecord {
    public Move move;
    public Piece piece;

    public MoveRecord() {}  // Gson cần constructor rỗng

    public MoveRecord(Move move, Piece piece) {
        this.move = move;
        this.piece = piece;
    }
}
