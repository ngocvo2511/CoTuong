package com.example.cotuong.ChessLogic.gamestate;

import com.example.cotuong.ChessLogic.Board;
import com.example.cotuong.ChessLogic.Player;
import com.example.cotuong.ChessLogic.ValuePiece;

public class GameStateAI extends GameState{
    private int depth;
    private ValuePiece valuePiece;
    public GameStateAI(Player player, Board board, int depth, int timeLimit){
        super(player, board, timeLimit);
        this.depth = depth;
        valuePiece = new ValuePiece();
    }
}