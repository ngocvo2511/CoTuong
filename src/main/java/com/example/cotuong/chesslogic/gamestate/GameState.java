package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.Position;
import com.example.cotuong.chesslogic.pieces.Piece;

import java.util.*;

public abstract class GameState {
    // Properties
    protected final Board board;
    protected Stack<AbstractMap.SimpleEntry<Move, Piece>> moved;
    protected Player currentPlayer;
    //protected Result result = null;
    protected Piece capturedPiece;
    protected int timeRemainingRed;
    protected int timeRemainingBlack;
    protected List<Piece> capturedRedPiece;
    protected List<Piece> capturedBlackPiece;
    protected Stack<Integer> noCapture;
    //    protected Stack<String> stateString;
    private final Map<String, Integer> stateHistory;

    // Constructor
    public GameState(Player player, Board board, int timeLimit) {
        this.currentPlayer = player;
        this.board = board;
        this.moved = new Stack<>();
        this.capturedRedPiece = new ArrayList<>();
        this.capturedBlackPiece = new ArrayList<>();
        this.noCapture = new Stack<>();
        this.stateHistory = new HashMap<>();
//        this.stateString = new Stack<>();
//        this.stateString.push(new StateString(player, board).toString());
//        this.stateHistory.put(stateString.peek(), 1);
        this.timeRemainingRed = timeLimit;
        this.timeRemainingBlack = timeLimit;
    }

    public Board getBoard() {
        return board;
    }

    public GameState(Player player, Board board, int redTime, int blackTime,
                     Stack<AbstractMap.SimpleEntry<Move, Piece>> moved,
                     Map<String, Integer> stateHistory,
                     List<Piece> capturedRedPiece,
                     List<Piece> capturedBlackPiece,
                     Stack<Integer> noCapture,
                     Stack<String> stateString) {
        this.currentPlayer = player;
        this.board = board;
        this.moved = moved;
        this.stateHistory = stateHistory;
//        this.stateString = stateString;
        this.capturedRedPiece = capturedRedPiece;
        this.capturedBlackPiece = capturedBlackPiece;
        this.noCapture = noCapture;
        this.timeRemainingRed = redTime;
        this.timeRemainingBlack = blackTime;
    }

    public List<String> getStateHistory() {
        List<String> history = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : stateHistory.entrySet()) {
            history.add(entry.getKey());
            history.add(String.valueOf(entry.getValue()));
        }
        return history;
    }

    public List<Move> legalMovesForPiece(Position pos) {
        if (board.isEmpty(pos) || board.get(pos).getColor() != currentPlayer) {
            return Collections.emptyList();
        }
        Piece piece = board.get(pos);
        List<Move> moveCandidates = piece.getMoves(pos, board);
        List<Move> legalMoves = new ArrayList<>();
        for (Move move : moveCandidates) {
            if (move.isLegal(board)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    public void makeMove(Move move) {
        moved.push(new AbstractMap.SimpleEntry<>(move, board.get(move.getToPos())));
        capturedPiece = board.get(move.getToPos());

        if (capturedPiece != null) {
            if (capturedPiece.getColor() == Player.BLACK) {
                capturedBlackPiece.add(capturedPiece);
            } else {
                capturedRedPiece.add(capturedPiece);
            }
        }

        boolean capture = move.execute(board);

        if (capture) {
            noCapture.push(0);
//            stateString.push("Clear");
            stateHistory.clear();
        } else {
            noCapture.push(noCapture.isEmpty() ? 1 : noCapture.peek() + 1);
        }

        currentPlayer = currentPlayer.opponent();
//        updateStateString();
//        checkForGameOver();
    }

//    public abstract void undoMove();

    public List<Move> allLegalMovesFor(Player player) {
        List<Move> legalMoves = new ArrayList<>();
        for (Position pos : board.piecePositionFor(player)) {
            Piece piece = board.get(pos);
            for (Move move : piece.getMoves(pos, board)) {
                if (move.isLegal(board)) {
                    legalMoves.add(move);
                }
            }
        }
        return legalMoves;
    }
}