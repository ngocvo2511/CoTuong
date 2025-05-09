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
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML private StackPane boardContainer;
    private AnchorPane gameOverPane;

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
            // Chỉ vô hiệu hóa bàn cờ và các nút điều khiển, không phải toàn bộ giao diện
            boardContainer.setDisable(true);
            controlButtons.setDisable(true);

            Move prevMove = gameState.moved.peek().getKey();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws ExecutionException, InterruptedException {
                    AI.makeAIMove();
                    Platform.runLater(() -> {
                        drawBoard(gameState.getBoard());
                        showPrevMove(gameState.moved.peek().getKey());
                        hidePrevMove(prevMove);

                        // Kiểm tra trạng thái game over sau khi AI đi
                        if (gameState.isGameOver()) {
                            hideHighlights();
                            showGameOverScreen();
                        } else {
                            // Kích hoạt lại bàn cờ và nút điều khiển
                            boardContainer.setDisable(false);
                            controlButtons.setDisable(false);
                        }
                    });
                    return null;
                }
            };
            new Thread(task).start();
            drawBoard(gameState.getBoard());
        }

        if (gameState.isGameOver()) {
            // Không gọi unableClick() nữa
            hideHighlights();
            showGameOverScreen();
        }
    }

    private void showGameOverScreen() {
        try {
            // Tải FXML của màn hình Game Over
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/game_over.fxml"));
            Parent gameOverRoot = loader.load();

            // Lấy controller và khởi tạo
            GameOverController controller = loader.getController();

            // Khởi tạo callback để xử lý các nút bấm trong màn hình Game Over
            GameOverController.GameOverCallback callback = new GameOverController.GameOverCallback() {
                @Override
                public void onNewGame() {
                    // Khởi tạo lại game mới
                    restartGame();
                }

                @Override
                public void onMainMenu() {
                    // Quay về màn hình chính
                    goToMainMenu();
                }

                @Override
                public void onReplay() {
                    // Xem lại ván đấu
                    replayGame();
                }
            };

            // Truyền dữ liệu kết quả game cho controller
            controller.initialize(gameState, callback);

            // Tạo AnchorPane mới để chứa màn hình game over
            gameOverPane = new AnchorPane(gameOverRoot);
            gameOverPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Overlay mờ
            gameOverPane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            // Thiết lập AnchorPane constraints để phủ toàn bộ rootPane
            AnchorPane.setTopAnchor(gameOverRoot, 0.0);
            AnchorPane.setBottomAnchor(gameOverRoot, 0.0);
            AnchorPane.setLeftAnchor(gameOverRoot, 0.0);
            AnchorPane.setRightAnchor(gameOverRoot, 0.0);

            // Đặt vị trí của gameOverPane để phủ toàn bộ rootPane
            AnchorPane.setTopAnchor(gameOverPane, 0.0);
            AnchorPane.setBottomAnchor(gameOverPane, 0.0);
            AnchorPane.setLeftAnchor(gameOverPane, 0.0);
            AnchorPane.setRightAnchor(gameOverPane, 0.0);

            // *** QUAN TRỌNG: Không vô hiệu hóa toàn bộ rootPane ***
            // Thay vì vô hiệu hóa toàn bộ rootPane, chỉ vô hiệu hóa các phần tử con cần thiết
            boardContainer.setDisable(true);
            controlButtons.setDisable(true);

            // KHÔNG gọi unableClick() vì nó sẽ vô hiệu hóa toàn bộ giao diện

            // Thêm gameOverPane vào rootPane
            rootPane.getChildren().remove(gameOverPane); // loại khỏi vị trí cũ nếu có
            rootPane.getChildren().add(gameOverPane);    // add lại cuối cùng
            gameOverPane.toFront();

            // Đặt controller để nó có thể truy cập đến gameOverPane sau này
            controller.setGameOverPane(gameOverPane);
            controller.setOfflineController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void restartGame() {
//        // Xóa bỏ màn hình game over
//        removeGameOverScreen();
//
//        // Xóa bỏ các highlights và trạng thái cũ
//        hideHighlights();
//        selectedPos = null;
//        moveCache.clear();
//        moveHistory.clear();
//
//        // Khởi tạo lại gameState với cùng cài đặt
//        boolean isAI = gameState instanceof GameStateAI;
//        int difficulty = isAI ? ((GameStateAI) gameState).getDifficulty() : 0;
//
//        if (isAI) {
//            gameState = new GameStateAI(Player.RED, Board.initial(), difficulty, 0);
//        } else {
//            gameState = new GameState2P(Player.RED, Board.initial(), 0);
//        }
//
//        // Vẽ lại bàn cờ
//        drawBoard(gameState.getBoard());
//
//        // Kích hoạt lại các phần tử giao diện
//        boardContainer.setDisable(false);
//        controlButtons.setDisable(false);
//    }
//
//    // Make sure this method properly removes the game over overlay
//    public void removeGameOverScreen() {
//        if (gameOverPane != null && rootPane.getChildren().contains(gameOverPane)) {
//            rootPane.getChildren().remove(gameOverPane);
//            gameOverPane = null;
//        }
    }

    private void goToMainMenu() {
        try {
            // Tải FXML của màn hình chính
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại
            Stage stage = (Stage) rootPane.getScene().getWindow();

            // Hiển thị màn hình chính
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replayGame() {
        // Chức năng xem lại ván đấu
        // Đây là phần chức năng phức tạp hơn cần triển khai sau
        System.out.println("Replay functionality to be implemented");
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
    private void handleUndo() throws ExecutionException, InterruptedException {
//        Sound.PlayButtonClickSound();
        if (!gameState.moved.isEmpty()) hidePrevMove(gameState.moved.peek().getKey());
        onToPositionSelected(selectedPos);
//        if (isReview == true)
//        {
//            if (gameState.Moved.Count == 0) return;
//            var move = gameState.Moved.Pop();
//            Move doMove = new NormalMove(move.Item1.ToPos, move.Item1.FromPos);
//            doMove.Execute(gameState.Board);
//            gameState.Board[doMove.FromPos] = move.Item2;
//            DrawBoard(gameState.Board);
//            if (gameState.Moved.Count != 0)
//            {
//                ShowPrevMove(gameState.Moved.First().Item1);
//            }
//            gameState.CapturedPiece = move.Item2;
//            moveList.Push(move);
//            gameState.CurrentPlayer = gameState.CurrentPlayer.Opponent();
//            UndoCapturedGrid(gameState.CapturedPiece);
//            TurnTextBlock.Text = gameState.CurrentPlayer == Player.Red ? "Đỏ" : "Đen";
//            WarningTextBlock.Text = gameState.Board.IsInCheck(gameState.CurrentPlayer) ? "Chiếu tướng!" : null;
//            gameState.noCapture.Pop();
//        }
//        else
//        {
        gameState.undoMove();
        drawBoard(gameState.getBoard());
        if (!gameState.moved.isEmpty())
        {
            showPrevMove(gameState.moved.peek().getKey());
        }
//            WarningTextBlock.Text = gameState.Board.IsInCheck(gameState.CurrentPlayer) ? "Chiếu tướng!" : null;
//            TurnTextBlock.Text = gameState.CurrentPlayer == Player.Red ? "Đỏ" : "Đen";

//            UndoCapturedGrid(gameState.CapturedPiece);
//            if (gameState instanceof GameStateAI AI)
//            UndoAiCapturedGrid(AI.AiCapturedPiece);
//            isRedTurn = gameState.CurrentPlayer == Player.Red;
//            if (redTimer != null) SwitchTurn();
//        }
    }

    @FXML
    private void handleSave() {
        // Logic lưu game
    }
}