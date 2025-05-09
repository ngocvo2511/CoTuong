package com.example.cotuong.chesslogic.gamestate;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.ValuePiece;
import com.example.cotuong.chesslogic.pieces.Piece;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameStateAI extends GameState{
    private final int depth;
    private final ValuePiece valuePiece;
    private Piece capturedPieceAI;

    public Piece getCapturedPieceAI() {
        return capturedPieceAI;
    }

    public GameStateAI(Player player, Board board, int depth, int timeLimit){
        super(player, board, timeLimit);
        this.depth = depth;
        valuePiece = new ValuePiece();
    }

    @Override
    public void undoMove() {
        if (moved.size() <= 1 || currentPlayer == Player.BLACK) return;
        for (int i = 0; i < 2; i++)
        {
            undoStateString();
            var undo = moved.pop();
            Move undoMove = new Move(undo.getKey().getToPos(), undo.getKey().getFromPos());
            undoMove.execute(board);
            board.set(undo.getKey().getToPos(),undo.getValue());
            if (undo.getValue() != null)
            {
                if (undo.getValue().getColor() == Player.BLACK) capturedBlackPiece.removeLast();
                else capturedRedPiece.removeLast();
            }
            if (i == 0) capturedPieceAI = undo.getValue();
            else capturedPiece = undo.getValue();
            noCapture.pop();
        }
        currentPlayer = Player.RED;
    }
    public void makeTestMove(Move move){
        moved.push(new AbstractMap.SimpleEntry<>(move, board.get(move.getToPos())));
        move.execute(board);
        currentPlayer = currentPlayer.opponent();
    }
    public void undoTestMove(){
        if(moved.empty()) return;
        var undo = moved.pop();
        Move undoMove = new Move(undo.getKey().getToPos(), undo.getKey().getFromPos());
        undoMove.execute(board);
        board.set(undo.getKey().getToPos(), undo.getValue());
        currentPlayer = currentPlayer.opponent();
    }
    public GameStateAI copy(){
        return new GameStateAI(currentPlayer, board, depth, timeRemainingBlack);
    }

    //Minimax
//    public void makeAIMove() throws ExecutionException, InterruptedException {
////        ExecutorService executorService = Executors.newFixedThreadPool(2);
////        List<Move> moveList = allLegalMovesFor(currentPlayer);
////        List<Future<AbstractMap.SimpleEntry<Move,Integer>>> futures = new ArrayList<>();
////        if(moveList.isEmpty()) return;
////
////        for(Move move:moveList){
////            futures.add(executorService.submit(()->{
////                GameStateAI copy=this.copy();
////                copy.makeTestMove(move);
////                int alpha=-9999, beta=9999;
////                int score=minimaxAlgorithm(copy, depth, alpha, beta);
////                return new AbstractMap.SimpleEntry<>(move, score);
////            }));
////        }
////        executorService.shutdown();
////
////        int bestValue=-10000;
////        Move bestMove = null;
////        for(var futureMove:futures){
////            if(futureMove.get().getValue()>bestValue){
////                bestValue = futureMove.get().getValue();
////                bestMove = futureMove.get().getKey();
////            }
////        }
////        if(bestMove!=null) makeMove(bestMove);
//        List<Move> moves = allLegalMovesFor(currentPlayer);
//        if (moves.isEmpty()) return;
//        Move bestMove = null;
//        int value;
//        int bestValue = -10000;
//        for (var move : moves)
//        {
//            makeTestMove(move);
//            value = minimaxAlgorithm(this,depth - 1,-9999,9999);
//            undoTestMove();
//            if (value > bestValue)
//            {
//                bestValue = value;
//                bestMove = move;
//            }
////            if (token.IsCancellationRequested) return;
//        }
//        if (bestMove != null) makeMove(bestMove);
//    }
public void makeAIMove() throws ExecutionException, InterruptedException {
    List<Move> moveList = allLegalMovesFor(currentPlayer);
    if (moveList.isEmpty()) return;

    int availableThreads = Math.min(moveList.size(), Runtime.getRuntime().availableProcessors() / 2);
    ExecutorService executor = Executors.newFixedThreadPool(availableThreads);
    List<Callable<AbstractMap.SimpleEntry<Move, Integer>>> tasks = new ArrayList<>();

    for (Move move : moveList) {
        tasks.add(() -> {
            GameStateAI copy = this.copy();
            copy.makeTestMove(move);
            int value = minimaxAlgorithm(copy, depth - 1, -9999, 9999); // depth-1 vì đã move 1 lần
            return new AbstractMap.SimpleEntry<>(move, value);
        });
    }

    List<Future<AbstractMap.SimpleEntry<Move, Integer>>> results = executor.invokeAll(tasks);
    executor.shutdown();

    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    for (Future<AbstractMap.SimpleEntry<Move, Integer>> result : results) {
        AbstractMap.SimpleEntry<Move, Integer> entry = result.get();
        if (entry.getValue() > bestValue) {
            bestValue = entry.getValue();
            bestMove = entry.getKey();
        }
    }

    if (bestMove != null) {
        makeMove(bestMove);
    }
}

    private int minimaxAlgorithm(GameStateAI copy, int depth, int alpha, int beta){
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
}