package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.pieces.Piece;

public class StateString {
    private final StringBuilder sb = new StringBuilder();

    public StateString(Player currentPlayer, Board board) {
        addPiecePlacement(board);
        sb.append(' ');
        addCurrentPlayer(currentPlayer);
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    private static char pieceChar(Piece piece) {
        char c;
        switch (piece.getType()) {
            case GENERAL -> c = 'g';
            case ADVISOR -> c = 'a';
            case ELEPHANT -> c = 'e';
            case HORSE -> c = 'h';
            case CHARIOT -> c = 'c';
            case CANNON -> c = 'n';
            case SOLDIER -> c = 's';
            default -> c = ' ';
        }

        if (piece.getColor() == Player.RED) {
            return Character.toUpperCase(c);
        }
        return c;
    }

    private void addRowData(Board board, int row) {
        int empty = 0;

        for (int c = 0; c < 9; c++) {
            Piece piece = board.get(row, c);
            if (piece == null) {
                empty++;
                continue;
            }

            if (empty > 0) {
                sb.append(empty);
                empty = 0;
            }

            sb.append(pieceChar(piece));
        }

        if (empty > 0) {
            sb.append(empty);
        }
    }

    private void addPiecePlacement(Board board) {
        for (int r = 0; r < 10; r++) {
            if (r != 0) {
                sb.append('/');
            }
            addRowData(board, r);
        }
    }

    private void addCurrentPlayer(Player currentPlayer) {
        if (currentPlayer == Player.RED) {
            sb.append("r");
        } else {
            sb.append("b");
        }
    }
}
