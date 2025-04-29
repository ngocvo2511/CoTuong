package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.network.ChessWebSocketClient;
import com.example.cotuong.network.LobbyManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;

public class OnlineOptionsController {
    @FXML
    private StackPane onlineOptionPane;

    @FXML
    private Button createRoomButton;

    @FXML
    private Button findRoomButton;

    @FXML
    private Button quickMatchButton;

    @FXML
    private Button backButton;

    private ModeSelectionController modeSelectionController;
    private StackPane createRoomPane;
    private StackPane joinRoomPane;
    private CreateRoomController createRoomController;
    private JoinRoomController joinRoomController;

    public void initialize() {

        // Load the create room overlay
        loadCreateRoomOverlay();
        loadJoinRoomOverlay();

        // Đăng ký callback xử lý khi phòng được tạo thành công
        LobbyManager.getInstance().getClient().setOnRoomCreated((roomName, username, time) -> {
            javafx.application.Platform.runLater(() -> {
                handleRoomCreated(roomName, username, time);
            });
        });

        LobbyManager.getInstance().getClient().setOnRoomJoined((roomName, username, time) -> {
            javafx.application.Platform.runLater(() -> {
                handleRoomJoined(roomName, username, time);
            });
        });
    }

    public void setModeSelectionController(ModeSelectionController controller) {
        this.modeSelectionController = controller;
    }

    private void loadCreateRoomOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/create_room.fxml"));
            createRoomPane = (StackPane) loader.load();
            createRoomController = loader.getController();
            createRoomController.setOnlineOptionsController(this);

            // Initially invisible
            createRoomPane.setVisible(false);

            // Add to the parent StackPane
            onlineOptionPane.getChildren().add(createRoomPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading create room overlay: " + e.getMessage());
        }
    }

    private void loadJoinRoomOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/join_room.fxml"));
            joinRoomPane = (StackPane) loader.load();
            joinRoomController = loader.getController();
            joinRoomController.setOnlineOptionsController(this);

            // Initially invisible
            joinRoomPane.setVisible(false);

            // Add to the parent StackPane
            onlineOptionPane.getChildren().add(joinRoomPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading create room overlay: " + e.getMessage());
        }
    }


    @FXML
    private void handleCreateRoom() {
        System.out.println("Create room option selected");
        showCreateRoomOverlay();
    }

    @FXML
    private void handleFindRoom() {
        System.out.println("Find room option selected");
        showJoinRoomOverlay();
    }

    @FXML
    private void handleQuickMatch() {
        System.out.println("Quick match option selected");
        // TODO: Implement quick match functionality
        startOnlineGame("quick");
    }

    @FXML
    private void handleBackButton() {
        // Close this overlay to return to mode selection
        if (modeSelectionController != null) {
            modeSelectionController.hideOnlineOption();
        }
    }

    public void showCreateRoomOverlay() {
        if (createRoomPane != null) {
            // Hide the main UI controls
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane) {
                    node.setVisible(false);
                }
            }

            // Make sure the join room overlay is hidden
            if (joinRoomPane != null) {
                joinRoomPane.setVisible(false);
            }

            // Show the create room overlay
            createRoomPane.setVisible(true);
        }
    }

    public void showJoinRoomOverlay() {
        if (joinRoomPane != null) {
            // Hide the main UI controls
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane) {
                    node.setVisible(false);
                }
            }

            // Make sure the create room overlay is hidden
            if (createRoomPane != null) {
                createRoomPane.setVisible(false);
            }

            // Show the join room overlay
            joinRoomPane.setVisible(true);
        }
    }

    public void hideCreateRoomOverlay() {
        if (createRoomPane != null) {
            createRoomPane.setVisible(false);

            // Show the main UI controls again
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void hideJoinRoomOverlay() {
        if (joinRoomPane != null) {
            joinRoomPane.setVisible(false);

            // Show the main UI controls again
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void handleRoomCreated(String roomName, String playerName, int timeSelection) {

        // TODO: Implement room creation with server communication
        // For now, we'll just hide all overlays and start the game
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }

        // Load giao diện GameScreen (ví dụ)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chơi game
            OnlineGameController controller = loader.getController();
            URI uri = new URI("ws://localhost:8080/ws/chess");
            ChessWebSocketClient chessClient = new ChessWebSocketClient(uri, controller);
            chessClient.connectBlocking();

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, playerName, Player.RED, timeSelection);
            Stage stage = (Stage) ((Node) createRoomButton).getScene().getWindow();  // `playButton` là ID của nút

            // Chuyển scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cờ Tướng");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
//            showNotification("Không thể vào phòng chơi!");
        }
    }

    public void handleRoomJoined(String roomName, String playerName, int timeSelection) {
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chơi game
            OnlineGameController controller = loader.getController();
            URI uri = new URI("ws://localhost:8080/ws/chess");
            ChessWebSocketClient chessClient = new ChessWebSocketClient(uri, controller);
            chessClient.connectBlocking();

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, playerName, Player.BLACK, timeSelection);
            Stage stage = (Stage) ((Node) createRoomButton).getScene().getWindow();  // `playButton` là ID của nút

            // Chuyển scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cờ Tướng");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
//            showNotification("Không thể vào phòng chơi!");
        }
    }

    private void startOnlineGame(String mode) {
        // TODO: Implement online game start with selected mode
        System.out.println("Starting online game with mode: " + mode);

        // Here you would start the online game with the selected mode
        // For now we'll just return to the main menu as a placeholder
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }
    }
}