package com.example.cotuong.ChessLogic.pieces;

import com.example.cotuong.ChessLogic.*;

import java.util.ArrayList;
import java.util.List;

public class Cannon extends Piece {
    public Cannon(Player color) {
        this(color, Player.RED);
    }

    public Cannon(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.CANNON;
    }

    @Override
    public Piece copy() {
        Cannon copy = new Cannon(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private static final Direction[] dirs = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private List<Position> movePositionsInDir(Position from, Board board, Direction dir)
    {
        List<Position> positions = new ArrayList<>();
        boolean foundObstacle = false;

        for (Position pos = from.add(dir); Board.isInside(pos); pos = pos.add(dir))
        {
            if (board.isEmpty(pos))
            {
                if (!foundObstacle) // chua gap quan can
                {
                    positions.add(pos);
                }
            }
            else
            {
                if (!foundObstacle) // gap quan can dau tien
                {
                    foundObstacle = true;
                }
                else // gap quan can thu hai
                {
                    Piece piece = board.get(pos);
                    if (piece.getColor() != color)
                    {
                        positions.add(pos);
                    }
                    break;
                }
            }
        }
        return positions;
    }

    private List<Position> movePositionsInDirs(Position from, Board board, Direction[] dirs)
    {
        List<Position> allPositions = new ArrayList<>();
        for (Direction dir : dirs) {
            allPositions.addAll(movePositionsInDir(from, board, dir));
        }
        return allPositions;
    }

    @Override
    public List<Move> getMoves(Position from, Board board)
    {
        List<Move> moves = new ArrayList<>();
        for (Position to : movePositionsInDirs(from, board, dirs)) {
            moves.add(new Move(from, to));
        }
        return moves;
    }
}