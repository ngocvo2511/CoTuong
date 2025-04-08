package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Player;

public class GameState2P extends GameState{
    public GameState2P(Player player, Board board, int timeLimit) {
        super(player, board, timeLimit);
    }

    @Override
    public void undoMove() {

    }
}