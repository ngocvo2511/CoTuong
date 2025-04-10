package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.example.cotuong.chesslogic.Player;

import java.util.*;

public class GameStateForLoad {
    public String gameType;
    public Board board;
    public Stack<AbstractMap.SimpleEntry<Move, Piece>> moved;
    public Player currentPlayer;
    public Stack<Integer> noCapture;
    public int depth;
    public int timeRemainingRed;
    public int timeRemainingBlack;
    public Map<String, Integer> stateHistory = new HashMap<>();
    public Stack<String> stateString = new Stack<String>();
    public List<Piece> capturedRedPiece;
    public List<Piece> capturedBlackPiece;
}
