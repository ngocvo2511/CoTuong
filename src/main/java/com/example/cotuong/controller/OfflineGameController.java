package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.Position;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.example.cotuong.utils.Images;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Ellipse;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

import java.io.*;
import java.util.*;

public class OfflineGameController {

    @FXML private GridPane overlayGrid;
    @FXML private ImageView boardImage;
    @FXML private Button pauseButton;
    @FXML private VBox controlButtons;

    private ImageView[][] pieceImages = new ImageView[10][9];
    private Ellipse[][] highlights = new Ellipse[10][9];
    private Canvas[][] posMoved = new Canvas[10][9];

    private GameState gameState;
    private Position selectedPos = null;
    private Map<Position, Move> moveCache = new HashMap<>();
    private List<Move> moveHistory = new ArrayList<>();
    private boolean isPaused = false;

    public void initialize() {
        initializeBoard();
        gameState = new GameState2P(Player.RED, Board.initial(), 0);
        drawBoard(gameState.getBoard());
    }

    private void initializeBoard() {
        overlayGrid.getChildren().clear();
        overlayGrid.getRowConstraints().clear();
        overlayGrid.getColumnConstraints().clear();
        overlayGrid.setStyle("-fx-background-color: transparent;");

        for (int r = 0; r < 10; r++) {
            overlayGrid.getRowConstraints().add(new RowConstraints(72));
        }
        for (int c = 0; c < 9; c++) {
            overlayGrid.getColumnConstraints().add(new ColumnConstraints(79));
        }

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-background-color: transparent;");

                ImageView imageView = new ImageView();
                pieceImages[r][c] = imageView;

                Ellipse highlight = new Ellipse(20, 20);
                highlight.setVisible(false);
                highlights[r][c] = highlight;

                Canvas canvas = new Canvas(72, 72);
                posMoved[r][c] = canvas;

                cell.getChildren().addAll(imageView, highlight, canvas);
                overlayGrid.add(cell, c, r);
            }
        }
    }

    private void drawBoard(Board board) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                Piece piece = board.get(r, c);
                pieceImages[r][c].setImage(Images.getImage(piece));
            }
        }
    }

    private void cacheMoves(List<Move> moves) {
        moveCache.clear();
        for (Move move : moves) {
            moveCache.put(move.getToPos(), move);
        }
    }

    @FXML
    private void handleBoardClick(MouseEvent e) {
        if (isPaused) return;

        double width = overlayGrid.getWidth();
        double height = overlayGrid.getHeight();

        double squareWidth = width / 9;
        double squareHeight = height / 10;

        int col = (int) (e.getX() / squareWidth);
        int row = (int) (e.getY() / squareHeight);

        Position pos = new Position(row, col);

        if (selectedPos == null) {
            onFromPositionSelected(pos);
        } else {
            onToPositionSelected(pos);
        }
    }

    private void onFromPositionSelected(Position pos) {
        List<Move> moves = gameState.legalMovesForPiece(pos);
        if (!moves.isEmpty()) {
            selectedPos = pos;
            cacheMoves(moves);
            showHighlights();
        }
    }

    private void onToPositionSelected(Position pos) {
        selectedPos = null;
        hideHighlights();

        if (moveCache.containsKey(pos)) {
            Move move = moveCache.get(pos);
            handleMove(move);
        }
    }

    private void showHighlights() {
        for (Position pos : moveCache.keySet()) {
            highlights[pos.getRow()][pos.getColumn()].setVisible(true);
        }
    }

    private void hideHighlights() {
        for (Ellipse[] row : highlights) {
            for (Ellipse e : row) {
                e.setVisible(false);
            }
        }
    }

    private void handleMove(Move move) {
        gameState.makeMove(move);
        moveHistory.add(move);
        drawBoard(gameState.getBoard());
        // Add logic for turn switching, check detection, game end, etc.
    }

    @FXML
    private void handlePause() {

    }

    @FXML
    private void handleUndo() {

    }

    @FXML
    private void handleSave() {

    }
}