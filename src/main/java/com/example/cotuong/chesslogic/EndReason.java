package com.example.cotuong.chesslogic;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EndReason {
    CHECKMATE,
    STALEMATE,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE,
    DRAW_AGREEMENT,
    RESIGNATION,
    TIMEFORFEIT,
    ABANDONED,
    PLAYER_DISCONNECTED,
    UNKNOWN;

    @JsonValue
    public String getValue() {
        return name();
    }
}
