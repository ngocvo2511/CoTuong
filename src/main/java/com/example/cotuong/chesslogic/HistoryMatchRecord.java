package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.gamestate.GameState;

import java.util.List;
import java.util.Stack;

public class HistoryMatchRecord {
    public String mode;
    public Result result;
    public Stack<MoveRecord> moved;
    public boolean isWin;
    public Difficulty level;
    public Player winner;
    public HistoryMatchRecord(){}

    public HistoryMatchRecord(String mode, Result result, Stack<MoveRecord> moved, boolean isWin, Difficulty level, Player winner) {
        this.mode = mode;
        this.result = result;
        this.moved = moved;
        this.isWin = isWin;
        this.level = level;
        this.winner = winner;
    }
}
