package com.example.cotuong.chesslogic;

public class Result {
    private final EndReason reason;
    private final Player winner;

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

    public EndReason getReason() {
        return reason;
    }

    public Player getWinner() {
        return winner;
    }
}