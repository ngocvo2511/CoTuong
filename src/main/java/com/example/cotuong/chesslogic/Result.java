package com.example.cotuong.chesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
    @JsonProperty
    private EndReason reason;
    @JsonProperty
    private Player winner;

    public Result() {
    }

    public Result(Player winner, EndReason reason) {
        this.reason = reason;
        this.winner = winner;
    }

    public static Result win(Player winner, EndReason reason) {
        return new Result(winner, reason);
    }

    public static Result draw(EndReason reason) {
        return new Result(Player.NONE, reason);
    }

    @JsonProperty
    public EndReason getReason() {
        return reason;
    }
    @JsonProperty
    public Player getWinner() {
        return winner;
    }
    @JsonProperty
    public void setReason(EndReason reason) {
        this.reason = reason;
    }
    @JsonProperty
    public void setWinner(Player winner) {
        this.winner = winner;
    }
}