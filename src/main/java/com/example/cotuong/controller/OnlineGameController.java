        package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.*;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
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
import javafx.stage.Stage;
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

    private String username;
    private String opponentUsername;
    private int time;
    private Timeline timer;
    private boolean isPlayer1Turn = true;


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
    private boolean start = false;

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
            Sounds.playGameOverSound();
            hideHighlights();

            client.sendGameOver(roomName, gameState.getResult(), gameState.currentPlayer);
        }
    }

    public void handleGameOver(Result result, Player current) {
        // Logic xử lý khi game kết thúc
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
        // Đây là phần chức năng phức tạp hơn cần triển khai sau
        System.out.println("Replay functionality to be implemented");
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
    private void handleSetting() {
        // Logic lưu game
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
