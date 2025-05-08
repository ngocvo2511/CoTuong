        package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Board;
import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.Position;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.example.cotuong.utils.Images;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class OfflineGameController {

    @FXML private GridPane overlayGrid;
    @FXML private ImageView boardImage;
    @FXML private ImageView backgroundImage;
    @FXML private Button pauseButton;
    @FXML private Button undoButton;
    @FXML private Button saveButton;
    @FXML private VBox controlButtons;
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane boardContainer; // Container bọc bàn cờ

    private ImageView[][] pieceImages = new ImageView[10][9];
    private Ellipse[][] highlights = new Ellipse[10][9];
    private Pane[][] posMoved = new Pane[10][9];

    private GameState gameState;
    private Position selectedPos = null;
    private Map<Position, Move> moveCache = new HashMap<>();
    private List<Move> moveHistory = new ArrayList<>();
    private boolean isPaused = false;

    public void initialize(int difficult, boolean isAI) {
        initializeBoard();
        if (!isAI) gameState = new GameState2P(Player.RED, Board.initial(), 0);
        else gameState = new GameStateAI(Player.RED, Board.initial(), difficult, 0);
        drawBoard(gameState.getBoard());
        // Trì hoãn setupBoardCentering để đảm bảo Scene đã sẵn sàng
        Platform.runLater(this::setupBoardCentering);
    }

    private void initializeBoard() {
        overlayGrid.getChildren().clear();
        overlayGrid.getRowConstraints().clear();
        overlayGrid.getColumnConstraints().clear();
        overlayGrid.setStyle("-fx-background-color: transparent;");

        for (int r = 0; r < 10; r++) {
            overlayGrid.getRowConstraints().add(new RowConstraints(71));
        }
        for (int c = 0; c < 9; c++) {
            overlayGrid.getColumnConstraints().add(new ColumnConstraints(78.5));
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

                Pane pane = new Pane();
                posMoved[r][c] = pane;

                cell.getChildren().addAll(imageView, highlight, pane);
                overlayGrid.add(cell, c, r);
            }
        }
    }

    private void setupBoardCentering() {
        // Lấy Stage từ rootPane
        Stage stage = (Stage) rootPane.getScene().getWindow();

        // Binding kích thước của backgroundImage với Scene
        backgroundImage.fitWidthProperty().bind(rootPane.getScene().widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.getScene().heightProperty());

        // Căn giữa boardContainer
        updateBoardCentering();

        // Lắng nghe thay đổi kích thước của Scene
        rootPane.getScene().widthProperty().addListener((obs, oldVal, newVal) -> updateBoardCentering());
        rootPane.getScene().heightProperty().addListener((obs, oldVal, newVal) -> updateBoardCentering());
    }

    private void updateBoardCentering() {
        // Lấy kích thước của Scene
        Scene scene = rootPane.getScene();
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();

        // Kích thước cố định của BorderPane
        double boardWidth = 1200;
        double boardHeight = 720;

        // Tính toán translateX và translateY để căn giữa
        double translateX = (sceneWidth - boardWidth) / 2;
        double translateY = (sceneHeight - boardHeight) / 2;

        // Đảm bảo không di chuyển ra ngoài nếu cửa sổ nhỏ hơn bàn cờ
        if (translateX < 0) translateX = 0;
        if (translateY < 0) translateY = 0;

        boardContainer.setTranslateX(translateX);
        boardContainer.setTranslateY(translateY);
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
    private void handleBoardClick(MouseEvent e) throws ExecutionException, InterruptedException {
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

    private void onToPositionSelected(Position pos) throws ExecutionException, InterruptedException {
        selectedPos = null;
        hideHighlights();

        if (moveCache.containsKey(pos)) {
            Move move = moveCache.get(pos);
            handleMove(move);
        }
    }

    private void showHighlights() {
        for (Position pos : moveCache.keySet()) {
            Color color = gameState.getBoard().get(pos) == null ? Color.rgb(25, 255, 125, (double) 150 /255) : Color.rgb(255, 0, 0, (double) 150 /255);
            highlights[pos.getRow()][pos.getColumn()].setFill(color);
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
    private Group createCornerHighlight(Color color, boolean isOldPos){
        int offset = isOldPos ? 15 : 0;
        int length=  isOldPos ? 15 : 30;
        Group group = new Group();
        Line tlH,tlV,trH,trV,blH,blV,brH,brV;
        if(!isOldPos){
            tlH = new Line(5,0,25,0); // trái trên
            tlV = new Line(5,0,5,20);

            trH = new Line(55,0,75,0); //phải trên
            trV = new Line(75,0,75,20);

            blH = new Line(5,70,25,70); // trái dưới
            blV = new Line(5,70,5,50);

            brH = new Line(55,70,75,70 ); // phải dưới
            brV = new Line(75,70,75,50);
        }
        else{
            tlH = new Line(20, 16, 30, 16); // trái trên
            tlV = new Line(20, 16, 20, 26);

            trH = new Line(48, 16, 58, 16); //phải trên
            trV = new Line( 58, 16, 58, 26);

            blH = new Line(20, 54, 30, 54); // trái dưới
            blV = new Line( 20, 54, 20, 44);

            brH = new Line(58, 54, 48, 54); // phải dưới
            brV = new Line( 58, 54, 58, 44);
        }


        for (Line line : new Line[]{tlH, tlV, trH, trV, blH, blV, brH, brV}) {
            line.setStroke(color);
            line.setStrokeWidth(2);
        }

        group.getChildren().addAll(tlH, tlV, trH, trV, blH, blV, brH, brV);
        return group;
    }
    private void showPrevMove(Move move){
        Color color;
        if(gameState.getBoard().get(move.getToPos()).getColor() == Player.BLACK) color = Color.BLUE;
        else color = Color.RED;
        Group oldPos = createCornerHighlight(color, true);
        Group newPos = createCornerHighlight(color, false);
        posMoved[move.getFromPos().getRow()][move.getFromPos().getColumn()].getChildren().add(oldPos);
        posMoved[move.getToPos().getRow()][move.getToPos().getColumn()].getChildren().add(newPos);
    }
    private void hidePrevMove(Move move){
        posMoved[move.getFromPos().getRow()][move.getFromPos().getColumn()].getChildren().clear();
        posMoved[move.getToPos().getRow()][move.getToPos().getColumn()].getChildren().clear();
    }
    private void handleMove(Move move) throws ExecutionException, InterruptedException {
        if(!gameState.moved.empty()) hidePrevMove(gameState.moved.peek().getKey());
        gameState.makeMove(move);
        moveHistory.add(move);
        drawBoard(gameState.getBoard());
        showPrevMove(move);
        if (gameState instanceof GameStateAI AI) {
            unableClick();
            Move prevMove = gameState.moved.peek().getKey();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws ExecutionException, InterruptedException {
                    AI.makeAIMove();
                    Platform.runLater(() -> {
                        drawBoard(gameState.getBoard());
                        showPrevMove(gameState.moved.peek().getKey());
                        hidePrevMove(prevMove);
                        ableClick();
                    });
                    return null;
                }
            };
            new Thread(task).start();
            drawBoard(gameState.getBoard());
        }
    }
    private void ableClick(){
        rootPane.setDisable(false);
        saveButton.setDisable(false);
        pauseButton.setDisable(false);
        undoButton.setDisable(false);
    }
    private void unableClick(){
        rootPane.setDisable(true);
        saveButton.setDisable(true);
        pauseButton.setDisable(true);
        undoButton.setDisable(true);
    }

    @FXML
    private void handlePause() {
        // Logic tạm dừng
    }

    @FXML
    private void handleUndo() {
        // Logic hoàn tác
    }

    @FXML
    private void handleSave() {
        // Logic lưu game
    }
}
