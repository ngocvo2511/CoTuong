package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.pieces.Piece;

public class Move {
    private Position fromPos;
    private Position toPos;

    public Move(Position fromPos, Position toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }

    public Position getFromPos() {
        return fromPos;
    }

    public Position getToPos() {
        return toPos;
    }

    public void setFromPos(Position fromPos) {
        this.fromPos = fromPos;
    }

    public void setToPos(Position toPos) {
        this.toPos = toPos;
    }

    public boolean isLegal(Board board) {
        Player player = board.get(fromPos).getColor();
        Board boardCopy = board.copy();
        execute(boardCopy);
        return !boardCopy.isInCheck(player);
    }

    public boolean execute(Board board) {
        Piece piece = board.get(fromPos);
        boolean capture = !board.isEmpty(toPos);
        board.set(toPos, piece);
        board.set(fromPos, null);
        piece.hasMoved = true;
        return capture;
    }
}