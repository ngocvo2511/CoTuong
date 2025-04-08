package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.ValuePiece;
import com.example.cotuong.chesslogic.pieces.Piece;

import java.util.AbstractMap;

public class GameStateAI extends GameState{
    private int depth;
    private ValuePiece valuePiece;
    private Piece capturedPieceAI;

    public Piece getCapturedPieceAI() {
        return capturedPieceAI;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setCapturedPieceAI(Piece capturedPieceAI) {
        this.capturedPieceAI = capturedPieceAI;
    }

    public GameStateAI(Player player, Board board, int depth, int timeLimit){
        super(player, board, timeLimit);
        this.depth = depth;
        valuePiece = new ValuePiece();
    }

    @Override
    public void undoMove() {
        if (moved.size() <= 1 || currentPlayer == Player.BLACK) return;
        for (int i = 0; i < 2; i++)
        {
            undoStateString();
            var undo = moved.pop();
            Move undoMove = new Move(undo.getKey().getToPos(), undo.getKey().getFromPos());
            undoMove.execute(board);
            board.set(undo.getKey().getToPos(),undo.getValue());
            if (undo.getValue() != null)
            {
                if (undo.getValue().getColor() == Player.BLACK) capturedBlackPiece.remove(capturedBlackPiece.size() - 1);
                else capturedRedPiece.remove(capturedRedPiece.size() - 1);
            }
            if (i == 0) capturedPieceAI = undo.getValue();
            else capturedPiece = undo.getValue();
            noCapture.pop();
        }
        currentPlayer = Player.RED;
    }
    public void makeTestMove(Move move){
        moved.push(new AbstractMap.SimpleEntry<>(move, board.get(move.getToPos())));
        move.execute(board);
        currentPlayer = currentPlayer.opponent();
    }
    public void undoTestMove(){
        if(moved.empty()) return;
        var undo = moved.pop();
        Move undoMove = new Move(undo.getKey().getToPos(), undo.getKey().getFromPos());
        undoMove.execute(board);
        board.set(undo.getKey().getToPos(), undo.getValue());
        currentPlayer = currentPlayer.opponent();
    }
//    public GameStateAI copy(){
//
//    }
}