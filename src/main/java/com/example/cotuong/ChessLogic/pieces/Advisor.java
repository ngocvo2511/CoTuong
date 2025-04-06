package com.example.cotuong.ChessLogic.pieces;

import com.example.cotuong.ChessLogic.*;

import java.util.ArrayList;
import java.util.List;

public class Advisor extends Piece {

    public Advisor(Player color) {

        this(color, Player.RED);
    }

    public Advisor(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.ADVISOR;
    }

    @Override
    public Piece copy() {
        Advisor copy = new Advisor(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private static final Direction[] dirs = {
            Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_EAST, Direction.NORTH_WEST
    };

    private List<Position> movePositions(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        for (Direction dir : dirs) {
            Position to = from.add(dir);
            if (!Board.isInside(to)) {
                continue;
            }

            if (Board.isInPalace(to, color) && (board.isEmpty(to) || board.get(to).getColor() != color)) {
                positions.add(to);
            }
        }
        return positions;
    }

    @Override
    public List<Move> getMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        for (Position to : movePositions(from, board)) {
            moves.add(new Move(from, to));
        }
        return moves;
    }
}