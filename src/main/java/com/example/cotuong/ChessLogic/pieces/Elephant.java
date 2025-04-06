package com.example.cotuong.ChessLogic.pieces;

import com.example.cotuong.ChessLogic.*;

import java.util.ArrayList;
import java.util.List;

public class Elephant extends Piece {
    public Elephant(Player color) {

        this(color, Player.RED);
    }

    public Elephant(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.ELEPHANT;
    }

    @Override
    public Piece copy() {
        Elephant copy = new Elephant(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private static final Direction[] dirs = {
            Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_EAST, Direction.NORTH_WEST
    };

    private boolean isCrossedRiver(Position pos){
        if (bottomPlayer == Player.RED)
        {
            if (color == Player.RED)
            {
                return pos.getRow() < 5;
            }
            else if (color == Player.BLACK)
            {
                return pos.getRow() > 4;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (color == Player.RED)
            {
                return pos.getRow() > 4;
            }
            else if (color == Player.BLACK)
            {
                return pos.getRow() < 5;
            }
            else
            {
                return false;
            }
        }
    }

    private List<Position> movePositions(Position from, Board board) {
        List<Position> positions = new ArrayList<>();
        for (Direction dir : dirs) {
            Position middlePos = from.add(dir);
            if(!Board.isInside(middlePos) || !board.isEmpty(middlePos) || isCrossedRiver(middlePos)){
                continue;
            }

            Position to = middlePos.add(dir);
            if(Board.isInside(to) && (board.isEmpty(to) || board.get(to).getColor() != color)){
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