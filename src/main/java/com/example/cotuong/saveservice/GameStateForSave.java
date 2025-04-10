package com.example.cotuong.saveservice;

import java.util.List;

public class GameStateForSave {
    public String gameType;
    public List<String> board;
    public String currentPlayer;
    public List<Integer> noCapture;
    public int depth;
    public List<String> moved;
    public List<String> stateHistory;
    public List<String> stateString;
    public int timeRemainingRed;
    public int timeRemainingBlack;
    public List<String> capturedRedPiece;
    public List<String> capturedBlackPiece;
}
