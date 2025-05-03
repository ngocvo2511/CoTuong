package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;

public class GameState2P extends GameState{
    public GameState2P(Player player, Board board, int timeLimit) {
        super(player, board, timeLimit);
    }

    @Override
    public void undoMove() {
        if (moved.isEmpty()) return;
        undoStateString();
        var undo = moved.pop();
        Move undoMove = new Move(undo.getKey().getToPos(), undo.getKey().getFromPos());
        undoMove.execute(board);
        board.set(undo.getKey().getToPos(),undo.getValue());
        if (undo.getValue() != null)
        {
            if (undo.getValue().getColor() == Player.BLACK) capturedBlackPiece.removeLast();
            else capturedRedPiece.removeLast();
        }
        currentPlayer = currentPlayer.opponent();
        capturedPiece = undo.getValue();
        noCapture.pop();
    }
}