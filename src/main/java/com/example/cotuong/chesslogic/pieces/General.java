package com.example.cotuong.chesslogic.pieces;

import com.example.cotuong.chesslogic.*;

import java.util.ArrayList;
import java.util.List;

public class General extends Piece {
    public General(Player color) {

        this(color, Player.RED);
    }

    public General(Player color, Player bottomPlayer) {
        this.color = color;
        this.bottomPlayer = bottomPlayer;
        this.type = PieceType.GENERAL;
    }

    @Override
    public Piece copy() {
        General copy = new General(color, bottomPlayer);
        copy.hasMoved = false;
        return copy;
    }

    private static final Direction[] dirs = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
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

    private boolean isExposedToOpponentKing(Position to, Board board) {
        Position opponentKingPos = findOpponentKing(board);
        if (opponentKingPos == null || opponentKingPos.getColumn() != to.getColumn())
        {
            return false;
        }
        int rowStep = opponentKingPos.getRow() > to.getRow() ? 1 : -1;
        for (int row = to.getRow() + rowStep; row != opponentKingPos.getRow(); row += rowStep)
        {
            if (!board.isEmpty(new Position(row, to.getColumn())))
            {
                return false;
            }
        }
        return true;
    }

    private Position findOpponentKing(Board board)
    {
        if (bottomPlayer == Player.RED)
        {
            if (color == Player.RED)
            {
                for (int r = 0; r <= 2; r++)
                {
                    for (int c = 3; c <= 5; c++)
                    {
                        Position pos = new Position(r, c);
                        Piece piece = board.get(pos);
                        if (piece != null && piece.getType() == PieceType.GENERAL)
                        {
                            return pos;
                        }
                    }
                }
                return null;
            }
            else if (color == Player.BLACK)
            {
                for (int r = 7; r <= 9; r++)
                {
                    for (int c = 3; c <= 5; c++)
                    {
                        Position pos = new Position(r, c);
                        Piece piece = board.get(pos);
                        if (piece != null && piece.getType() == PieceType.GENERAL)
                        {
                            return pos;
                        }
                    }
                }
                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (color == Player.BLACK)
            {
                for (int r = 0; r <= 2; r++)
                {
                    for (int c = 3; c <= 5; c++)
                    {
                        Position pos = new Position(r, c);
                        Piece piece = board.get(pos);
                        if (piece != null && piece.getType() == PieceType.GENERAL)
                        {
                            return pos;
                        }
                    }
                }
                return null;
            }
            else if (color == Player.RED)
            {
                for (int r = 7; r <= 9; r++)
                {
                    for (int c = 3; c <= 5; c++)
                    {
                        Position pos = new Position(r, c);
                        Piece piece = board.get(pos);
                        if (piece != null && piece.getType() == PieceType.GENERAL)
                        {
                            return pos;
                        }
                    }
                }
                return null;
            }
            else
            {
                return null;
            }
        }
    }

    @Override
    public boolean canCaptureOpponentGeneral(Position from, Board board) {
        return isExposedToOpponentKing(from, board);
    }
}