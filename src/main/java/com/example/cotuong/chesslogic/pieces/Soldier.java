package com.example.cotuong.chesslogic.pieces;

import com.example.cotuong.chesslogic.*;

import java.util.ArrayList;
import java.util.List;

public class Soldier extends Piece {
    private final Direction forward;

    public Soldier(Player color) {

        this(color, Player.RED);
    }

    public Soldier(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.SOLDIER;

        if (bottomPlayer == Player.RED) {
            this.forward = (color == Player.RED) ? Direction.NORTH : Direction.SOUTH;
        } else {
            this.forward = (color == Player.RED) ? Direction.SOUTH : Direction.NORTH;
        }
    }

    @Override
    public Piece copy() {
        Advisor copy = new Advisor(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private boolean isCrossedRiver(Position pos) {
        if (bottomPlayer == Player.RED) {
            if (color == Player.RED) {
                return pos.getRow() < 5;
            } else {
                return pos.getRow() > 4;
            }
        } else {
            if (color == Player.RED) {
                return pos.getRow() > 4;
            } else {
                return pos.getRow() < 5;
            }
        }
    }

    private List<Position> movePositions(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        Position to = from.add(forward);
        if (Board.isInside(to) && (board.isEmpty(to) || board.get(to).getColor() != color)) {
            positions.add(to);
        }

        if (isCrossedRiver(from)) {
            for (Direction dir : new Direction[] { Direction.EAST, Direction.WEST }) {
                Position sideTo = from.add(dir);
                if (Board.isInside(sideTo) && (board.isEmpty(sideTo) || board.get(sideTo).getColor() != color)) {
                    positions.add(sideTo);
                }
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