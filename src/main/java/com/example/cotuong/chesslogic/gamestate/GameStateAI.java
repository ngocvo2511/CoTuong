package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.ValuePiece;

public class GameStateAI extends GameState{
    private int depth;
    private ValuePiece valuePiece;
    public GameStateAI(Player player, Board board, int depth, int timeLimit){
        super(player, board, timeLimit);
        this.depth = depth;
        valuePiece = new ValuePiece();
    }
}