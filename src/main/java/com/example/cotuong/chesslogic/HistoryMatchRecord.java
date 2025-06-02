package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.gamestate.GameState;

import java.util.List;

public class HistoryMatchRecord {
    public String mode;
    public Result result;
    public List<MoveRecord> moved;
    public boolean isWin;
    public String level;
    public HistoryMatchRecord(){}

    public HistoryMatchRecord(String mode, Result result, List<MoveRecord> moved, boolean isWin, String level) {
        this.mode = mode;
        this.result = result;
        this.moved = moved;
        this.isWin = isWin;
        this.level = level;
    }
}
