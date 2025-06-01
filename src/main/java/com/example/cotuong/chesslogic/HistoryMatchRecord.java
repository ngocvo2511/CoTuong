package com.example.cotuong.chesslogic;

import com.example.cotuong.chesslogic.gamestate.GameState;

import java.util.List;

public class HistoryMatchRecord {
    public String mode;
    public Result result;
    public List<MoveRecord> moved;

    public HistoryMatchRecord(){}

    public HistoryMatchRecord(String mode, Result result, List<MoveRecord> moved) {
        this.mode = mode;
        this.result = result;
        this.moved = moved;
    }
}
