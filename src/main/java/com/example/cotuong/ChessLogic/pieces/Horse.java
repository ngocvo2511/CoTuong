package com.example.cotuong.ChessLogic.pieces;

import com.example.cotuong.ChessLogic.*;

import java.util.ArrayList;
import java.util.List;

public class Horse extends Piece {
    public Horse(Player color) {

        this(color, Player.RED);
    }

    public Horse(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.HORSE;
    }

    @Override
    public Piece copy() {
        Horse copy = new Horse(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private static List<Position> PotentialToPosition(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        for (Direction dir : new Direction[] { Direction.NORTH, Direction.SOUTH })
        {
            Position toPos = from.add(dir);
            if (Board.isInside(toPos) && board.isEmpty(toPos)) //khong co quan chan
            {
                Position newPos = from.add(dir.scale(2));
                positions.add(newPos.add(Direction.WEST));
                positions.add(newPos.add(Direction.EAST));
            }
        }

        for (Direction dir : new Direction[] { Direction.EAST, Direction.WEST })
        {
            Position toPos = from.add(dir);
            if (Board.isInside(toPos) && board.isEmpty(toPos)) //khong co quan chan
            {
                Position newPos = from.add(dir.scale(2));
                positions.add(newPos.add(Direction.NORTH));
                positions.add(newPos.add(Direction.SOUTH));
            }
        }
        return positions;
    }

    private List<Position> movePositions(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        for(Position pos : PotentialToPosition(from, board))
        {
            if (Board.isInside(pos) && (board.isEmpty(pos) || board.get(pos).getColor() != color)) {
                positions.add(pos);
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