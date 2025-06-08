package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.*;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.google.gson.annotations.Expose;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameStateAI extends GameState{
    private volatile boolean cancelled = false;

    private Difficulty depth;

    private transient ExecutorService executor;

    public Difficulty getDepth() {
        return depth;
    }

    private transient ValuePiece valuePiece = new ValuePiece();
    private Piece capturedPieceAI;

    public Piece getCapturedPieceAI() {
        return capturedPieceAI;
    }

    public GameStateAI(Player player, Board board, Difficulty depth, int timeLimit){
        super(player, board, timeLimit);
        this.depth = depth;
    }
    public void initValuePiece(){
        valuePiece = new ValuePiece();
    }

    @Override
    public void undoMove() {
        if (moved.size() <= 1 || currentPlayer == Player.BLACK) return;
        for (int i = 0; i < 2; i++)
        {
            undoStateString();
            var undo = moved.pop();
            Move undoMove = new Move(undo.move.getToPos(), undo.move.getFromPos());
            undoMove.execute(board);
            board.set(undo.move.getToPos(),undo.piece);
            if (undo.piece != null)
            {
                if (undo.piece.getColor() == Player.BLACK) capturedBlackPiece.removeLast();
                else capturedRedPiece.removeLast();
            }
            if (i == 0) capturedPieceAI = undo.piece;
            else capturedPiece = undo.piece;
            noCapture.pop();
        }
        currentPlayer = Player.RED;
    }
    public void makeTestMove(Move move){
        moved.push(new MoveRecord(move, board.get(move.getToPos())));
        move.execute(board);
        currentPlayer = currentPlayer.opponent();
    }
    public void undoTestMove(){
        if(moved.empty()) return;
        var undo = moved.pop();
        Move undoMove = new Move(undo.move.getToPos(), undo.move.getFromPos());
        undoMove.execute(board);
        board.set(undo.move.getToPos(), undo.piece);
        currentPlayer = currentPlayer.opponent();
    }
    public GameStateAI copy(){
        return new GameStateAI(currentPlayer, board, depth, timeRemainingBlack);
    }

    public void makeAIMove() throws ExecutionException, InterruptedException {
        cancelled = false;
        List<Move> moveList = allLegalMovesFor(currentPlayer);
        if (moveList.isEmpty()) return;

        int availableThreads = Math.min(moveList.size(), Math.max(1,Runtime.getRuntime().availableProcessors() / 2));
        executor = Executors.newFixedThreadPool(availableThreads);
        List<Callable<AbstractMap.SimpleEntry<Move, Integer>>> tasks = new ArrayList<>();

        for (Move move : moveList) {
            tasks.add(() -> {
                GameStateAI copy = this.copy();
                copy.makeTestMove(move);
                int value = minimaxAlgorithm(copy, depth.getLevel() - 1, -9999, 9999);
                return new AbstractMap.SimpleEntry<>(move, value);
            });
        }

        List<Future<AbstractMap.SimpleEntry<Move, Integer>>> results = null;
        try{
            results = executor.invokeAll(tasks);
            Move bestMove = null;
            int bestValue = Integer.MIN_VALUE;
            for (Future<AbstractMap.SimpleEntry<Move, Integer>> result : results) {
                if(cancelled) return;
                AbstractMap.SimpleEntry<Move, Integer> entry = result.get();
                if(entry == null) continue;
                if (entry.getValue() > bestValue) {
                    bestValue = entry.getValue();
                    bestMove = entry.getKey();
                }
            }

            if (!cancelled || bestMove != null) {
                makeMove(bestMove);
            }
        }
        catch (InterruptedException e){
            cancelled = true;
            Thread.currentThread().interrupt();
        }
        finally {
            executor.shutdownNow();
        }
    }

    private int minimaxAlgorithm(GameStateAI copy, int depth, int alpha, int beta){
        if(cancelled || Thread.currentThread().isInterrupted()) return -9999;
        List<Move> moves = copy.allLegalMovesFor(copy.currentPlayer);
        if (moves.isEmpty()) return (copy.currentPlayer == Player.BLACK) ? -9999 : 9999;
        if (depth == 0) return valuePiece.getValueBoard(copy.board);
        if (copy.currentPlayer == Player.BLACK)
        {
            int bestValue = -9999;
            for (var move : moves)
            {
                copy.makeTestMove(move);
                int value = minimaxAlgorithm(copy,depth - 1, alpha, beta);
                copy.undoTestMove();
                bestValue = Math.max(bestValue, value);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) return bestValue;
            }
            return bestValue;
        }
        else if (copy.currentPlayer == Player.RED)
        {
            int bestValue = 9999;
            for (var move : moves)
            {
                copy.makeTestMove(move);
                int value = minimaxAlgorithm(copy,depth - 1, alpha, beta);
                copy.undoTestMove();
                bestValue = Math.min(bestValue, value);
                beta = Math.min(beta, value);
                if (alpha >= beta) return bestValue;
            }
            return bestValue;
        }
        else return valuePiece.getValueBoard(copy.board);
    }
    public void cancelAIMove() {
        cancelled = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}