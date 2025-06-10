        package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.*;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.chesslogic.pieces.Piece;
import com.example.cotuong.network.ChessWebSocketClient;
import com.example.cotuong.network.LobbyManager;
import com.example.cotuong.network.LobbyWebSocketClient;
import com.example.cotuong.saveservice.SaveHistoryMatchManager;
import com.example.cotuong.utils.Images;
import com.example.cotuong.utils.Sounds;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class OnlineGameController {

    @FXML
    public Label player2Name;
    @FXML
    public Label player1Name;
    @FXML
    public Label player1TimerLabel;
    @FXML
    public Label player2TimerLabel;
    @FXML
    public Button leaveButtons;
    @FXML
    public Label currentTurnLabel;
    @FXML
    public Button undoButton;
    @FXML
    public Button nextButton;

    @FXML private GridPane overlayGrid;
    @FXML private ImageView boardImage;
    @FXML private ImageView backgroundImage;
    @FXML private AnchorPane rootPane;
    @FXML private StackPane boardContainer;
    @FXML private VBox controlButtons;
    @FXML private Button leaveButton;
    @FXML private Button settingButton;
    @FXML private FlowPane capturedBlackPieces;
    @FXML private FlowPane capturedRedPieces;
    @FXML
    private StackPane countdownPopup;
    @FXML
    private Label checkLabel;
    @FXML
    private Label countdownText;

    private Pane dimmer;
    private Parent overlay;

    @FXML
    private StackPane responsiveBoardContainer;
    private String username;
    private String opponentUsername;
    private int time;
    private Timeline timer;
    private boolean isPlayer1Turn = true;
    private boolean isReview = false;


    private ChessWebSocketClient client;
    private Player color;

    private ImageView[][] pieceImages = new ImageView[10][9];
    private Ellipse[][] highlights = new Ellipse[10][9];
    private Pane[][] posMoved = new Pane[10][9];
    private AnchorPane gameOverPane; // New field to hold game over overlay


    private GameState gameState;
    private String roomName;
    private Position selectedPos = null;
    private Map<Position, Move> moveCache = new HashMap<>();
    private Stack<MoveRecord> moveHistory = new Stack<>();
    private boolean start = false;
    private final int BOARD_ROWS = 10;
    private final int BOARD_COLS = 9;
    private final double ORIGINAL_BOARD_SIZE = 720.0;
    private final double ORIGINAL_OVERLAY_SIZE = 710.0;
    private final double MIN_BOARD_SIZE = 200.0; // Kích thước tối thiểu
    private final double MAX_BOARD_SIZE = 800.0; // Kích thước tối đa
    public Player getColor() {
        return color;
    }

    public GameState getGameState() {
        return gameState;
    }


    public void onPlayerMoved(int x1, int y1, int x2, int y2) {
        if (client != null && client.isOpen()) {
            client.makeMove(x1, y1, x2, y2);
            System.out.println("on PlayerMoved");
        }
    }

    public void initializeGame(String roomName, String username, Player playerColor, int timeLimit, String opponentUsername) {
        this.roomName = roomName;
        this.username = username;
        this.color = playerColor;
        this.time = timeLimit;
        this.opponentUsername = opponentUsername;
        this.gameState = new GameState2P(Player.RED, Board.initialForOnline(playerColor), timeLimit);
        initializeBoard();
        setupResponsiveBoard();
        drawBoard(gameState.getBoard());
        client.registerGameSession(roomName);
        showGameInformation();

        LobbyWebSocketClient client = LobbyManager.getInstance().getClient();

        client.setOnPlayerJoined((creator, joiner) -> {
            Platform.runLater(() -> {
                System.out.println("Đối thủ đã vào phòng: " + joiner);
                setOpponentUsername(joiner); // ← Bạn đã có sẵn hàm này
                sendStartGame(roomName);
            });
        });
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(this::handleWindowClose);
        });
    }

    private void handleWindowClose(WindowEvent event) {
        // Prevent the window from closing immediately
        Sounds.playButtonClickSound();
        event.consume();

        // Show exit confirmation dialog
        showExitConfirmation();
    }

    private void showExitConfirmation() {
        try {
            // Load FXML cho exit confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/exit_confirm.fxml"));
            StackPane exitConfirmPane = loader.load();

            // Lấy controller của exit confirmation
            ExitConfirmController exitController = loader.getController();

            // Callback để xử lý khi user xác nhận thoát
            exitController.setOnConfirmExit(() -> {
                // Thực hiện logic thoát game
                if (client != null) {
                    client.leaveRoom(roomName);
                }

                // Đóng cửa sổ
                Stage currentStage = (Stage) rootPane.getScene().getWindow();
                // Remove the close request handler to avoid infinite loop
                currentStage.setOnCloseRequest(null);
                currentStage.close();
                Platform.exit();
                System.exit(0);
            });

            // Callback để xử lý khi user hủy
            exitController.setOnCancel(() -> {
                // Remove exit confirmation từ rootPane
                rootPane.getChildren().remove(exitConfirmPane);
            });

            // Thêm exit confirmation vào rootPane
            // Set anchor để fill toàn màn hình
            AnchorPane.setTopAnchor(exitConfirmPane, 0.0);
            AnchorPane.setBottomAnchor(exitConfirmPane, 0.0);
            AnchorPane.setLeftAnchor(exitConfirmPane, 0.0);
            AnchorPane.setRightAnchor(exitConfirmPane, 0.0);

            rootPane.getChildren().add(exitConfirmPane);

        } catch (IOException e) {
            e.printStackTrace();
            // Fallback: thoát trực tiếp nếu không load được FXML
            if (client != null) {
                client.leaveRoom(roomName);
            }
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.setOnCloseRequest(null);
            currentStage.close();
        }
    }

    public void sendStartGame(String roomName) {
        JSONObject message = new JSONObject();
        message.put("action", "StartGame");
        message.put("roomName", roomName);
        client.send(message.toString());
    }

    private void setOpponentUsername(String joiner) {
        this.opponentUsername = joiner;
        showGameInformation();
    }

    private void showGameInformation(){
        player1Name.setText(username);
        player2Name.setText(opponentUsername);
    }

    public void setWebSocketClient(ChessWebSocketClient client) {
        this.client = client;
    }

    public void setPlayerColor(Player color) {
        this.color = color;
        gameState = new GameState2P(Player.RED, Board.initialForOnline(color), 0);
        drawBoard(gameState.getBoard());
    }

    private void setupResponsiveBoard() {
        // Bind kích thước responsiveBoardContainer với logic responsive cho hình vuông
        responsiveBoardContainer.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() -> {
                    double rootWidth = rootPane.getWidth();
                    double rootHeight = rootPane.getHeight();

                    // Tính toán không gian khả dụng
                    double availableWidth = rootWidth * 0.5 - 100; // 50% width trừ đi margin cho sidebar/controls
                    double availableHeight = rootHeight * 0.9 - 100; // 90% height trừ đi margin cho header/footer

                    // Chọn kích thước nhỏ nhất để đảm bảo bàn cờ vuông fit trong cả width và height
                    double maxSquareSize = availableHeight;

                    // Áp dụng giới hạn min/max
                    maxSquareSize = Math.max(MIN_BOARD_SIZE, Math.min(MAX_BOARD_SIZE, maxSquareSize));

                    return maxSquareSize;
                }, rootPane.widthProperty(), rootPane.heightProperty())
        );

        // Height = Width để tạo hình vuông
        responsiveBoardContainer.prefHeightProperty().bind(
                responsiveBoardContainer.prefWidthProperty()
        );

        // Set constraints cho min/max
        responsiveBoardContainer.minWidthProperty().set(MIN_BOARD_SIZE);
        responsiveBoardContainer.maxWidthProperty().set(MAX_BOARD_SIZE);
        responsiveBoardContainer.minHeightProperty().bind(responsiveBoardContainer.minWidthProperty());
        responsiveBoardContainer.maxHeightProperty().bind(responsiveBoardContainer.maxWidthProperty());

        // Bind boardImage với responsiveBoardContainer
        boardImage.fitWidthProperty().bind(responsiveBoardContainer.widthProperty());
        boardImage.fitHeightProperty().bind(responsiveBoardContainer.heightProperty());
        boardImage.setPreserveRatio(false); // Giữ nguyên để tạo hình vuông

        // Chờ boardImage load xong rồi mới bind overlay
        Platform.runLater(() -> {
            // Bind overlayGrid với kích thước thực tế của boardImage
            overlayGrid.prefWidthProperty().bind(
                    boardImage.boundsInLocalProperty().map(bounds ->
                            bounds.getWidth() * (ORIGINAL_OVERLAY_SIZE / ORIGINAL_BOARD_SIZE)
                    )
            );
            overlayGrid.prefHeightProperty().bind(
                    boardImage.boundsInLocalProperty().map(bounds ->
                            bounds.getHeight() * (ORIGINAL_OVERLAY_SIZE / ORIGINAL_BOARD_SIZE)
                    )
            );
            overlayGrid.maxWidthProperty().bind(overlayGrid.prefWidthProperty());
            overlayGrid.maxHeightProperty().bind(overlayGrid.prefHeightProperty());
            overlayGrid.minWidthProperty().bind(overlayGrid.prefWidthProperty());
            overlayGrid.minHeightProperty().bind(overlayGrid.prefHeightProperty());
        });

        // Setup dynamic cell sizing cho overlayGrid
        setupDynamicGridSizing();
    }

    private void setupDynamicGridSizing() {
        // Listener để update kích thước cells khi overlayGrid thay đổi
        overlayGrid.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                updateGridCellSizes();
            }
        });

        overlayGrid.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                updateGridCellSizes();
            }
        });

        // Listener cho boardImage bounds để update ngay khi boardImage thay đổi
        boardImage.boundsInLocalProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> updateGridCellSizes());
        });
    }

    private void updateGridCellSizes() {
        double overlayWidth = overlayGrid.getWidth();
        double overlayHeight = overlayGrid.getHeight();

        if (overlayWidth <= 0 || overlayHeight <= 0) return;

        // Tính toán kích thước cell dựa trên overlayGrid thực tế
        double cellWidth = overlayWidth / BOARD_COLS;
        double cellHeight = overlayHeight / BOARD_ROWS;

        // Update RowConstraints
        for (RowConstraints row : overlayGrid.getRowConstraints()) {
            row.setPrefHeight(cellHeight);
            row.setMinHeight(cellHeight);
            row.setMaxHeight(cellHeight);
        }

        // Update ColumnConstraints
        for (ColumnConstraints col : overlayGrid.getColumnConstraints()) {
            col.setPrefWidth(cellWidth);
            col.setMinWidth(cellWidth);
            col.setMaxWidth(cellWidth);
        }

        // Update highlight sizes
        updateHighlightSizes(cellWidth, cellHeight);
    }

    private void updateHighlightSizes(double cellWidth, double cellHeight) {
        double highlightRadius = Math.min(cellWidth, cellHeight) * 0.25; // 25% của cell size

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                if (highlights[r][c] != null) {
                    highlights[r][c].setRadiusX(highlightRadius);
                    highlights[r][c].setRadiusY(highlightRadius);
                }
            }
        }
    }

    private void initializeBoard() {
        overlayGrid.getChildren().clear();
        overlayGrid.getRowConstraints().clear();
        overlayGrid.getColumnConstraints().clear();
        overlayGrid.setStyle("-fx-background-color: transparent;");
        // Tạo constraints với kích thước tạm thời (sẽ được update sau)
        for (int r = 0; r < BOARD_ROWS; r++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPrefHeight(71); // Giá trị mặc định
            overlayGrid.getRowConstraints().add(rowConstraint);
        }

        for (int c = 0; c < BOARD_COLS; c++) {
            ColumnConstraints colConstraint = new ColumnConstraints();
            colConstraint.setPrefWidth(78.5); // Giá trị mặc định
            overlayGrid.getColumnConstraints().add(colConstraint);
        }

        // Tạo cells
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-background-color: transparent;");

                ImageView imageView = new ImageView();
                // Bind kích thước piece image với cell
                imageView.fitWidthProperty().bind(
                        Bindings.min(cell.widthProperty(), cell.heightProperty()).multiply(0.95)
                );
                imageView.fitHeightProperty().bind(
                        Bindings.min(cell.widthProperty(), cell.heightProperty()).multiply(0.95)
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

        // Force layout update để đảm bảo kích thước được tính toán
        Platform.runLater(() -> {
            overlayGrid.applyCss();
            overlayGrid.layout();
        });
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
        if(!start){
            return;
        }
        if (gameState.currentPlayer != color) {
            return;
        }

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
            onPlayerMoved(move.getFromPos().getRow(), move.getFromPos().getColumn(),
                    move.getToPos().getRow(), move.getToPos().getColumn());
            System.out.println("on to position selected");
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
    public void handleMove(Move move) {
        Sounds.playMoveSound();
        if(!gameState.moved.empty()) hidePrevMove(gameState.moved.peek().move);
        gameState.makeMove(move);
        switchTurn();
        if(gameState.moved.peek().piece!=null) addCapturedPiece(gameState.moved.peek().piece);
        drawBoard(gameState.getBoard());
        showPrevMove(move);
        updateCheckLabel();
        updateTurnIndicator();
        if (gameState.isGameOver()) {
            // Không gọi unableClick() nữa
            if(isReview) return;
            timer.stop();
            Sounds.playGameOverSound();
            hideHighlights();
            client.sendGameOver(roomName, gameState.getResult(), gameState.currentPlayer);
        }
    }

    public void handleGameOver(Result result, Player current) {
        // Logic xử lý khi game kết thúc
        if(isReview) return;
        showGameOverScreen();

        // Hiển thị thông báo hoặc thực hiện hành động khác
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
                    Sounds.playButtonClickSound();
                    restartGame();
                }

                @Override
                public void onMainMenu() {
                    // Quay về màn hình chính
                    Sounds.playButtonClickSound();
                    goToMainMenu();
                }

                @Override
                public void onReplay() {
                    // Xem lại ván đấu
                    Sounds.playButtonClickSound();
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
            controller.setOnlineController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        try{
            Sounds.playButtonClickSound();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/mode_selection.fxml"));
            overlay = loader.load();

            ModeSelectionController controller = loader.getController();
            controller.setOnlineGameController(this);

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        isReview = true;
        if (gameOverPane != null && rootPane.getChildren().contains(gameOverPane)) {
            rootPane.getChildren().remove(gameOverPane);
        }
        if (!gameState.moved.empty())
            hidePrevMove(gameState.moved.peek().move);
        moveHistory.addAll(gameState.moved.stream().toList().reversed());
        gameState = new GameState2P(Player.RED, Board.initialForOnline(color),0);
        updateTurnIndicator();
        capturedBlackPieces.getChildren().clear();
        capturedRedPieces.getChildren().clear();
        player1TimerLabel.setText("");
        player2TimerLabel.setText("");
        checkLabel.setText("");
        drawBoard(gameState.getBoard());
        boardContainer.setDisable(true);
        undoButton.setVisible(true);
        nextButton.setVisible(true);
        controlButtons.setDisable(false);
        undoButton.setDisable(true);
    }


    @FXML
    private void handleLeave() {
        Sounds.playButtonClickSound();
//        client.leaveRoom(roomName);
        try {
            // Load FXML cho exit confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/exit_confirm.fxml"));
            StackPane exitConfirmPane = loader.load();

            // Lấy controller của exit confirmation
            ExitConfirmController exitController = loader.getController();

            // Callback để xử lý khi user xác nhận thoát
            exitController.setOnConfirmExit(() -> {
                // Thực hiện logic thoát game
                client.leaveRoom(roomName);

                try {
                    // Tải FXML của màn hình chính
                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml"));
                    Parent root = loader1.load();

                    // Lấy stage hiện tại
                    Stage stage = (Stage) rootPane.getScene().getWindow();

                    MainMenuController controller = loader1.getController();
                    controller.setStage(stage); //

                    // Hiển thị màn hình chính
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Callback để xử lý khi user hủy
            exitController.setOnCancel(() -> {
                // Remove exit confirmation từ rootPane
                rootPane.getChildren().remove(exitConfirmPane);
            });

            // Thêm exit confirmation vào rootPane
            // Set anchor để fill toàn màn hình
            AnchorPane.setTopAnchor(exitConfirmPane, 0.0);
            AnchorPane.setBottomAnchor(exitConfirmPane, 0.0);
            AnchorPane.setLeftAnchor(exitConfirmPane, 0.0);
            AnchorPane.setRightAnchor(exitConfirmPane, 0.0);

            rootPane.getChildren().add(exitConfirmPane);

        } catch (IOException e) {
            e.printStackTrace();
            // Fallback: thoát trực tiếp nếu không load được FXML
            client.leaveRoom(roomName);
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();
        }
    }

    @FXML
    private void handleUndo(){
        Sounds.playButtonClickSound();
        if (gameState.moved.isEmpty()) return;
        if(nextButton.isDisable()) nextButton.setDisable(false);
        moveHistory.add(gameState.moved.peek());
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
        else undoButton.setDisable(true);
        updateCheckLabel();
        updateTurnIndicator();
    }
    @FXML
    private void handleNext(){
        Sounds.playButtonClickSound();
        if(moveHistory.isEmpty()) return;
        if(!gameState.moved.isEmpty()) hidePrevMove(gameState.moved.peek().move);
        MoveRecord moveRecord = moveHistory.pop();
        Piece capturedPiece = moveRecord.piece;
        if (capturedPiece != null && capturedPiece.getColor() != gameState.getBoard().get(moveRecord.move.getFromPos()).getColor()) {
            ImageView pieceImage = addCapturedPiece(capturedPiece);
        }
        gameState.makeMove(moveRecord.move);
        if(moveHistory.isEmpty()) nextButton.setDisable(true);
        if(!gameState.moved.isEmpty()) undoButton.setDisable(false);
        showPrevMove(gameState.moved.peek().move);

        drawBoard(gameState.getBoard());
        updateCheckLabel();
        updateTurnIndicator();
    }
    @FXML
    private void handleSetting() {
        Sounds.playButtonClickSound();
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/settings.fxml"));
            overlay = loader.load();

            // Get controller
            SettingsController settingsController = loader.getController();
            settingsController.refreshUIFromSettings();
            settingsController.setOnlineGameController(this);

            // Set callback cho khi cancel
            settingsController.setOnCancel(() -> {
                removeOverlay();
            });


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

        } catch (IOException e) {
            e.printStackTrace();
            // Log error hoặc hiển thị error dialog
            System.err.println("Không thể load settings dialog: " + e.getMessage());
        }
    }

    public void removeOverlay() {
        if (dimmer != null && overlay != null) {
            rootPane.getChildren().removeAll(dimmer, overlay);
            dimmer = null;
            overlay = null;
        }
    }

    private ImageView addCapturedPiece(Piece piece) {
        if (piece == null) return null;
        FlowPane targetContainer;
        // Xác định container dựa trên màu của quân cờ
        if(color == Player.BLACK){
            targetContainer = piece.getColor() == Player.BLACK ? capturedRedPieces : capturedBlackPieces;
        }
        else targetContainer = piece.getColor() == Player.BLACK ? capturedBlackPieces : capturedRedPieces;

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

    public void showCountdown(int secondsLeft) {

        countdownText.setText("Trận đấu sẽ bắt đầu sau " + secondsLeft + " giây");
        countdownPopup.setVisible(true);

    }

    public void hideCountdown() {
        countdownPopup.setVisible(false);
    }

    public void startGame() {
        start = true;
        initializeTimer();
    }

    private void initializeTimer() {
        updateTimerLabels();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (isPlayer1Turn) {
                gameState.timeRemainingRed--;
                if (gameState.timeRemainingRed <= 0) {
                    gameState.timeRemainingRed = 0;
                    timer.stop();
                    try {
                        handleTimeOut();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                gameState.timeRemainingBlack--;
                if (gameState.timeRemainingBlack <= 0) {
                    gameState.timeRemainingBlack = 0;
                    timer.stop();
                    try {
                        handleTimeOut();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            updateTimerLabels();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabels() {
        if (color == Player.RED) {
            player1TimerLabel.setText(formatTime(gameState.timeRemainingRed));
            player2TimerLabel.setText(formatTime(gameState.timeRemainingBlack));
        } else {
            player1TimerLabel.setText(formatTime(gameState.timeRemainingBlack));
            player2TimerLabel.setText(formatTime(gameState.timeRemainingRed));
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
    }

    private void handleTimeOut() throws Exception {
        Sounds.playGameOverSound();
        gameState.timeForfeit();
        hideHighlights();
//        HistoryMatchRecord historyMatchRecord = initRecord();
//        SaveHistoryMatchManager.save(historyMatchRecord);
//        System.out.println("Save successfully");
        client.sendGameOver(roomName, gameState.getResult(), gameState.currentPlayer);
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

    public void handleLeaveRoom(){
        if(!start){
            setOpponentUsername("");
        }
        else{
            Sounds.playGameOverSound();
            gameState.setResult(Result.win(color, EndReason.PLAYER_DISCONNECTED));
            client.sendGameOver(roomName, gameState.getResult(), color == Player.RED ? Player.BLACK : Player.RED);
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
}
