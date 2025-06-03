
        package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.*;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.example.cotuong.saveservice.SaveHistoryMatchManager;
import com.example.cotuong.utils.Images;
import com.example.cotuong.utils.Sounds;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    @FXML
    private GridPane overlayGrid;
    @FXML
    private ImageView boardImage;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Button pauseButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button playButton;
    @FXML
    private Button nextButton;
    @FXML
    private VBox controlButtons;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane boardContainer;
    @FXML
    private StackPane boardStackPane;
    @FXML
    private HBox player1Container;
    @FXML
    private HBox player2Container;
    @FXML
    private Label player1Name;
    @FXML
    private Label player2Name;
    @FXML
    private ImageView player1Avatar;
    @FXML
    private ImageView player2Avatar;
    @FXML
    private VBox outerContainer;
    @FXML
    private FlowPane capturedBlackPieces;
    @FXML
    private FlowPane capturedRedPieces;
    @FXML
    private Label checkLabel;
    @FXML
    private Label currentTurnLabel;

    private AnchorPane gameOverPane;

    private Pane dimmer;
    private Parent overlay;

    private ImageView[][] pieceImages = new ImageView[10][9];
    private Ellipse[][] highlights = new Ellipse[10][9];
    private Pane[][] posMoved = new Pane[10][9];

    private GameState gameState;
    private Position selectedPos = null;
    private Map<Position, Move> moveCache = new HashMap<>();
    private List<Move> moveHistory = new ArrayList<>();
    private boolean isPaused = false;
    private boolean isReview = false;
    private boolean isPlayerFirst = true;
    private int selectedTime = 10;
    private List<Map.Entry<Move, ImageView>> capturedPiecesHistory = new ArrayList<>();
    // Constants for board dimensions
    private final int BOARD_ROWS = 10;
    private final int BOARD_COLS = 9;

    private final double BOARD_LEFT_PADDING_PERCENT = 0.0555;  // 28.9 / 521 ≈ 5.55%
    private final double BOARD_RIGHT_PADDING_PERCENT = 0.0555;  // Giả định giống viền trái
    private final double BOARD_TOP_PADDING_PERCENT = 0.1;       // Giả định 10% chiều cao
    private final double BOARD_BOTTOM_PADDING_PERCENT = 0.1;    // Giả định 10% chiều cao

    public void setPlayerFirst(boolean isPlayerFirst) {
        this.isPlayerFirst = isPlayerFirst;
    }

    public void initialize(Difficulty difficulty, boolean isAI, int selectedTime) {
        this.selectedTime = selectedTime;
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        setupResponsiveBoard();
        initializeBoard();

        if (!isAI) {
            // Chế độ 2 người chơi: sử dụng selectedTime
            gameState = new GameState2P(Player.RED, Board.initial(), selectedTime * 60);
        } else {
            // Chế độ AI: không giới hạn thời gian
            Player startingPlayer = isPlayerFirst ? Player.RED : Player.BLACK;
            gameState = new GameStateAI(startingPlayer, Board.initial(), difficulty, 0);
            if (!isPlayerFirst) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws ExecutionException, InterruptedException {
                        ((GameStateAI) gameState).makeAIMove();
                        Platform.runLater(() -> {
                            drawBoard(gameState.getBoard());
                            if (!gameState.moved.isEmpty()) {
                                showPrevMove(gameState.moved.peek().move);
                            }
                            updateCheckLabel();
                            updateTurnIndicator();
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
        drawBoard(gameState.getBoard());
        updateTurnIndicator();
        updateCheckLabel();
    }

    public void initialize(HistoryMatchRecord historyMatchRecord) {
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        setupResponsiveBoard();
        initializeBoard();
        updateCheckLabel();
    }

    public void initialize(GameState gameState) {
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        setupResponsiveBoard();
        for (Piece piece : gameState.getCapturedBlackPiece()) addCapturedPiece(piece);
        for (Piece piece : gameState.getCapturedRedPiece()) addCapturedPiece(piece);
        initializeBoard();

        this.gameState = gameState;

        drawBoard(this.gameState.getBoard());
        if (!gameState.moved.isEmpty()) Platform.runLater(() -> showPrevMove(gameState.moved.peek().move));
        //setupPlayerContainersScaling();
        updateCheckLabel();
    }

    private void setupResponsiveBoard() {
        DoubleBinding minDimension = Bindings.createDoubleBinding(() ->
                        Math.min(boardContainer.getWidth(), boardContainer.getHeight()),
                boardContainer.widthProperty(), boardContainer.heightProperty()
        );

        // Liên kết kích thước boardImage với minDimension
        boardImage.fitWidthProperty().bind(minDimension);
        boardImage.fitHeightProperty().bind(minDimension);

        // Hệ số kéo dài chiều cao
        double verticalScale = 1.25; // Tăng chiều cao lên 125%

        // Căn chỉnh overlayGrid với khu vực chơi
        overlayGrid.maxWidthProperty().bind(boardImage.fitWidthProperty().multiply(
                1.0 - BOARD_LEFT_PADDING_PERCENT - BOARD_RIGHT_PADDING_PERCENT)); // ~463.2px
        overlayGrid.maxHeightProperty().bind(boardImage.fitHeightProperty().multiply(
                (1.0 - BOARD_TOP_PADDING_PERCENT - BOARD_BOTTOM_PADDING_PERCENT) * verticalScale)); // ~461.6 * 1.25
        overlayGrid.minWidthProperty().bind(overlayGrid.maxWidthProperty());
        overlayGrid.minHeightProperty().bind(overlayGrid.maxHeightProperty());

        // Căn giữa boardStackPane và boardContainer
        boardStackPane.setAlignment(Pos.CENTER);
        boardContainer.setAlignment(Pos.CENTER);
        DoubleBinding leftOffset;
        // Tính toán offset để căn chỉnh overlayGrid với khu vực chơi
        if (isPlayerFirst == true) {
            leftOffset = boardImage.fitWidthProperty().multiply(BOARD_LEFT_PADDING_PERCENT).add(-45);
        } else {
            leftOffset = boardImage.fitWidthProperty().multiply(BOARD_LEFT_PADDING_PERCENT);
        }
        DoubleBinding topOffset = boardImage.fitHeightProperty().multiply(BOARD_TOP_PADDING_PERCENT); // ~10%

        // Bù verticalScale để căn giữa dọc
        DoubleBinding verticalAdjustment = boardImage.fitHeightProperty().multiply(
                (verticalScale - 1.0) * (1.0 - BOARD_TOP_PADDING_PERCENT - BOARD_BOTTOM_PADDING_PERCENT) / 2);

        // Tạo ObjectBinding cho Insets
        DoubleBinding finalLeftOffset = leftOffset;
        ObjectBinding<Insets> marginBinding = Bindings.createObjectBinding(() ->
                        new Insets(topOffset.get() - verticalAdjustment.get(), 0, 0, finalLeftOffset.get()),
                topOffset, verticalAdjustment, leftOffset);

        // Cập nhật margin động bằng listener
        marginBinding.addListener((obs, oldVal, newVal) -> {
            StackPane.setMargin(overlayGrid, newVal);
        });

        // Đặt margin ban đầu
        StackPane.setMargin(overlayGrid, marginBinding.get());

        // Không cần padding cho boardStackPane
        boardStackPane.setPadding(new Insets(0, 0, 0, 0));

        overlayGrid.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateGridConstraints();
        });
    }

    private void updateGridConstraints() {
        double cellWidth = overlayGrid.getWidth() / BOARD_COLS;  // 463.2 / 9 ≈ 51.47px mỗi cột
        double cellHeight = cellWidth * (10.0 / 9.0);          // Ép tỷ lệ 10:9, ≈ 57.19px mỗi hàng

        // Cập nhật row constraints
        overlayGrid.getRowConstraints().clear();
        for (int r = 0; r < BOARD_ROWS; r++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(cellHeight);
            overlayGrid.getRowConstraints().add(row);
        }

        // Cập nhật column constraints
        overlayGrid.getColumnConstraints().clear();
        for (int c = 0; c < BOARD_COLS; c++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(cellWidth);
            overlayGrid.getColumnConstraints().add(col);
        }

        // Cập nhật kích thước highlights
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                if (highlights[r][c] != null) {
                    highlights[r][c].setRadiusX(cellWidth * 0.25);
                    highlights[r][c].setRadiusY(cellHeight * 0.25);
                }
            }
        }
    }

    private void initializeBoard() {
        overlayGrid.getChildren().clear();
        overlayGrid.getRowConstraints().clear();
        overlayGrid.getColumnConstraints().clear();
        overlayGrid.setStyle("-fx-background-color: transparent;");
        overlayGrid.setTranslateX(0); // Đảm bảo không lệch
        overlayGrid.setTranslateY(0);
        // Initial setup of constraints - will be updated dynamically later
        updateGridConstraints();

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-background-color: transparent;");

                ImageView imageView = new ImageView();
                // Make piece images resize with the board
                imageView.fitWidthProperty().bind(
                        overlayGrid.widthProperty().divide(BOARD_COLS)
                );
                imageView.fitHeightProperty().bind(
                        overlayGrid.heightProperty().divide(BOARD_ROWS)
                );
                imageView.setPreserveRatio(true);

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
    private void handleBoardClick(MouseEvent e) throws Exception {
        if (isPaused) return;

        double width = overlayGrid.getWidth();
        double height = overlayGrid.getHeight();

        double squareWidth = width / BOARD_COLS;
        double squareHeight = height / BOARD_ROWS;

        int col = (int) (e.getX() / squareWidth);
        int row = (int) (e.getY() / squareHeight);

        // Ensure coordinates are within board boundaries
        if (row < 0 || row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS) {
            return;
        }

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

    private void onToPositionSelected(Position pos) throws Exception {
        selectedPos = null;
        hideHighlights();

        if (moveCache.containsKey(pos)) {
            Move move = moveCache.get(pos);
            Sounds.playMoveSound();
            handleMove(move);
        }
    }

    private void showHighlights() {
        for (Position pos : moveCache.keySet()) {
            Color color = gameState.getBoard().get(pos) == null ? Color.rgb(25, 255, 125, (double) 150 / 255) : Color.rgb(255, 0, 0, (double) 150 / 255);
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

    private Group createCornerHighlight(Color color, boolean isOldPos) {
        // Make corner highlights resize with the cell size
        double cellWidth = overlayGrid.getWidth() / BOARD_COLS;
        double cellHeight = overlayGrid.getHeight() / BOARD_ROWS;

        double offset = isOldPos ? cellHeight * 0.2 : 0;
        double length = isOldPos ? cellWidth * 0.15 : cellWidth * 0.3;

        Group group = new Group();
        Line tlH, tlV, trH, trV, blH, blV, brH, brV;

        if (!isOldPos) {
            double margin = cellWidth * 0.05;
            double cornerLength = cellWidth * 0.25;

            tlH = new Line(margin, margin, margin + cornerLength, margin); // top-left horizontal
            tlV = new Line(margin, margin, margin, margin + cornerLength); // top-left vertical

            trH = new Line(cellWidth - margin - cornerLength, margin, cellWidth - margin, margin); // top-right horizontal
            trV = new Line(cellWidth - margin, margin, cellWidth - margin, margin + cornerLength); // top-right vertical

            blH = new Line(margin, cellHeight - margin, margin + cornerLength, cellHeight - margin); // bottom-left horizontal
            blV = new Line(margin, cellHeight - margin - cornerLength, margin, cellHeight - margin); // bottom-left vertical

            brH = new Line(cellWidth - margin - cornerLength, cellHeight - margin, cellWidth - margin, cellHeight - margin); // bottom-right horizontal
            brV = new Line(cellWidth - margin, cellHeight - margin - cornerLength, cellWidth - margin, cellHeight - margin); // bottom-right vertical
        } else {
            double margin = cellWidth * 0.2;
            double cornerLength = cellWidth * 0.1;

            tlH = new Line(margin, margin, margin + cornerLength, margin); // top-left horizontal
            tlV = new Line(margin, margin, margin, margin + cornerLength); // top-left vertical

            trH = new Line(cellWidth - margin - cornerLength, margin, cellWidth - margin, margin); // top-right horizontal
            trV = new Line(cellWidth - margin, margin, cellWidth - margin, margin + cornerLength); // top-right vertical

            blH = new Line(margin, cellHeight - margin, margin + cornerLength, cellHeight - margin); // bottom-left horizontal
            blV = new Line(margin, cellHeight - margin - cornerLength, margin, cellHeight - margin); // bottom-left vertical

            brH = new Line(cellWidth - margin - cornerLength, cellHeight - margin, cellWidth - margin, cellHeight - margin); // bottom-right horizontal
            brV = new Line(cellWidth - margin, cellHeight - margin - cornerLength, cellWidth - margin, cellHeight - margin); // bottom-right vertical
        }

        for (Line line : new Line[]{tlH, tlV, trH, trV, blH, blV, brH, brV}) {
            line.setStroke(color);
            line.setStrokeWidth(Math.max(2, cellWidth * 0.02));
        }

        group.getChildren().addAll(tlH, tlV, trH, trV, blH, blV, brH, brV);
        return group;
    }

    private void showPrevMove(Move move) {
        Color color;
        if (gameState.getBoard().get(move.getToPos()).getColor() == Player.BLACK) color = Color.BLUE;
        else color = Color.RED;

        // Clear previous highlights first
        posMoved[move.getFromPos().getRow()][move.getFromPos().getColumn()].getChildren().clear();
        posMoved[move.getToPos().getRow()][move.getToPos().getColumn()].getChildren().clear();

        Group oldPos = createCornerHighlight(color, true);
        Group newPos = createCornerHighlight(color, false);
        posMoved[move.getFromPos().getRow()][move.getFromPos().getColumn()].getChildren().add(oldPos);
        posMoved[move.getToPos().getRow()][move.getToPos().getColumn()].getChildren().add(newPos);
    }

    private void hidePrevMove(Move move) {
        posMoved[move.getFromPos().getRow()][move.getFromPos().getColumn()].getChildren().clear();
        posMoved[move.getToPos().getRow()][move.getToPos().getColumn()].getChildren().clear();
    }


    // Thêm phương thức để hiển thị quân cờ bị ăn
    private ImageView addCapturedPiece(Piece piece) {
        if (piece == null) return null;

        // Xác định container dựa trên màu của quân cờ
        FlowPane targetContainer = piece.getColor() == Player.BLACK ? capturedBlackPieces : capturedRedPieces;

        // Tạo ImageView cho quân cờ
        ImageView pieceImage = new ImageView(Images.getImage(piece));
        DoubleBinding scaleFactor = rootPane.widthProperty().divide(1920.0);
        pieceImage.fitWidthProperty().bind(scaleFactor.multiply(90));
        pieceImage.fitHeightProperty().bind(scaleFactor.multiply(90));
        pieceImage.setPreserveRatio(true);

        // Thêm vào container
        targetContainer.getChildren().add(pieceImage);
        return pieceImage;
    }

    // Sửa phương thức handleMove để chỉ thêm quân bị ăn
    private void handleMove(Move move) throws Exception {
        Piece capturedPiece = gameState.getBoard().get(move.getToPos());
        if (capturedPiece != null && capturedPiece.getColor() != gameState.getBoard().get(move.getFromPos()).getColor()) {
            ImageView pieceImage = addCapturedPiece(capturedPiece);
            if (pieceImage != null) {
                capturedPiecesHistory.add(Map.entry(move, pieceImage));
            }
        }
        if (!gameState.moved.isEmpty()) hidePrevMove(gameState.moved.peek().move);
        gameState.makeMove(move);
        moveHistory.add(move);
        drawBoard(gameState.getBoard());
        showPrevMove(move);
        updateCheckLabel();
        updateTurnIndicator();

        if (gameState instanceof GameStateAI AI) {
            boardContainer.setDisable(true);
            controlButtons.setDisable(true);

            Move prevMove = gameState.moved.peek().move;
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws ExecutionException, InterruptedException {
                    Board boardBeforeMove = gameState.getBoard().copy();
                    AI.makeAIMove();
                    Move aiMove = gameState.moved.isEmpty() ? null : gameState.moved.peek().move;
                    Platform.runLater(() -> {
                        if (aiMove != null) {
                            Piece capturedPiece = boardBeforeMove.get(aiMove.getToPos());
                            if (capturedPiece != null && capturedPiece.getColor() != boardBeforeMove.get(aiMove.getFromPos()).getColor()) {
                                ImageView pieceImage = addCapturedPiece(capturedPiece);
                                if (pieceImage != null) {
                                    capturedPiecesHistory.add(Map.entry(aiMove, pieceImage));
                                }
                            }
                        }
                        drawBoard(gameState.getBoard());
                        showPrevMove(gameState.moved.peek().move);
                        hidePrevMove(prevMove);
                        updateCheckLabel();
                        updateTurnIndicator();
                        if (gameState.isGameOver()) {
                            hideHighlights();
                            showGameOverScreen();
                        } else {
                            boardContainer.setDisable(false);
                            controlButtons.setDisable(false);
                        }
                    });
                    return null;
                }
            };
            new Thread(task).start();
        }

        if (gameState.isGameOver()) {
            Sounds.playGameOverSound();
            hideHighlights();
            List<MoveRecord> moveRecords = gameState.moved.stream().toList().reversed();
            Difficulty level = Difficulty.NONE;
            String mode;
            boolean isWin = true;
            Player winner = gameState.currentPlayer.opponent();
            if (gameState instanceof GameStateAI AI) {
                mode = "Chơi với máy";
                level = AI.getDepth();
                isWin = gameState.currentPlayer == Player.BLACK;
            } else mode = "Chơi 2 người";
            HistoryMatchRecord historyMatchRecord = new HistoryMatchRecord(mode, gameState.getResult(), moveRecords, isWin, level, winner);
            SaveHistoryMatchManager.save(historyMatchRecord);
            showGameOverScreen();
        }
    }


    private void updateTurnIndicator() {
        if (gameState.getCurrentPlayer() == Player.RED) {
            currentTurnLabel.setText("ĐỎ");
            currentTurnLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            currentTurnLabel.setText("ĐEN");
            currentTurnLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
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

            // Chỉ vô hiệu hóa các phần tử con cần thiết
            boardContainer.setDisable(true);
            controlButtons.setDisable(true);

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
        // Implementation for restartGame
    }

    private void goToMainMenu() {
        try {
            // Tải FXML của màn hình chính
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại
            Stage stage = (Stage) rootPane.getScene().getWindow();

            MainMenuController controller = loader.getController();
            controller.setStage(stage); //

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

    }

    private void ableClick() {
        rootPane.setDisable(false);
        saveButton.setDisable(false);
        pauseButton.setDisable(false);
        undoButton.setDisable(false);
    }

    private void unableClick() {
        rootPane.setDisable(true);
        saveButton.setDisable(true);
        pauseButton.setDisable(true);
        undoButton.setDisable(true);
    }

    public void closeOverlay() {
        // Tìm và xóa dimmer và overlay nếu có
        rootPane.getChildren().remove(dimmer);
        rootPane.getChildren().remove(overlay);
    }

    @FXML
    private void handlePause() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/pause_menu.fxml"));
        overlay = loader.load();

        PauseMenuController controller = loader.getController();
       // controller.setGameState(gameState);
        controller.setOfflineGameController(this);

        // Lớp làm mờ
        dimmer = new Pane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        dimmer.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // Anchor lớp mờ
        AnchorPane.setTopAnchor(dimmer, 0.0);
        AnchorPane.setBottomAnchor(dimmer, 0.0);
        AnchorPane.setLeftAnchor(dimmer, 0.0);
        AnchorPane.setRightAnchor(dimmer, 0.0);

        // Anchor overlay đúng 4 phía
        AnchorPane.setTopAnchor(overlay, 0.0);
        AnchorPane.setBottomAnchor(overlay, 0.0);
        AnchorPane.setLeftAnchor(overlay, 0.0);
        AnchorPane.setRightAnchor(overlay, 0.0);

        rootPane.getChildren().addAll(dimmer, overlay);
    }
    public void removePauseOverlay() {
        if (dimmer != null && overlay != null) {
            rootPane.getChildren().removeAll(dimmer, overlay);
            dimmer = null;
            overlay = null;
            System.out.println("Overlay tạm dừng đã được xóa");
        }
    }


    @FXML

    private void handleUndo() throws Exception {
        Sounds.playButtonClickSound();
        if (gameState.moved.isEmpty()) return;

        // Ẩn highlight của nước đi hiện tại
        Move lastMove = gameState.moved.peek().move;
        hidePrevMove(lastMove);

        // Gọi onToPositionSelected như logic gốc
        onToPositionSelected(selectedPos);

        gameState.undoMove();
        if(gameState.getCapturedPiece()!=null){
            if(gameState.getCapturedPiece().getColor() == Player.RED) capturedRedPieces.getChildren().removeLast();
            else capturedBlackPieces.getChildren().removeLast();
        }
        if(gameState instanceof GameStateAI){
            if(((GameStateAI) gameState).getCapturedPieceAI()!=null) capturedRedPieces.getChildren().removeLast();
        }

        // Cập nhật bàn cờ
        drawBoard(gameState.getBoard());

        // Hiển thị highlight của nước đi trước đó (nếu có)
        if (!gameState.moved.isEmpty()) {
            showPrevMove(gameState.moved.peek().move);
        }
        updateCheckLabel();
        updateTurnIndicator();
    }

    @FXML
    private void handleSave() throws IOException {
        Sounds.playButtonClickSound();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/save.fxml"));
        overlay = loader.load();

        SaveController controller = loader.getController();
        controller.setGameState(gameState);
        controller.setOfflineGameController(this);

        // Lớp làm mờ
        dimmer = new Pane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        dimmer.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // Anchor lớp mờ
        AnchorPane.setTopAnchor(dimmer, 0.0);
        AnchorPane.setBottomAnchor(dimmer, 0.0);
        AnchorPane.setLeftAnchor(dimmer, 0.0);
        AnchorPane.setRightAnchor(dimmer, 0.0);

        // Anchor overlay đúng 4 phía
        AnchorPane.setTopAnchor(overlay, 0.0);
        AnchorPane.setBottomAnchor(overlay, 0.0);
        AnchorPane.setLeftAnchor(overlay, 0.0);
        AnchorPane.setRightAnchor(overlay, 0.0);

        rootPane.getChildren().addAll(dimmer, overlay);
    }
    private void updateCheckLabel() {
        if (gameState.getBoard().isInCheck(Player.RED)) {
            checkLabel.setText(" CHIẾU TƯỚNG!");
        } else if (gameState.getBoard().isInCheck(Player.BLACK)) {
            checkLabel.setText(" CHIẾU TƯỚNG!");
        } else {
            checkLabel.setText("");
        }
    }
}
