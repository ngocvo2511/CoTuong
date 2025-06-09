package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.network.*;
import com.example.cotuong.utils.Sounds;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private Button randomMatchButton;

    @FXML
    private Button backButton;

    @FXML
    private CheckBox autoFindServerCheckBox;

    private ModeSelectionController modeSelectionController;
    private StackPane createRoomPane;
    private StackPane joinRoomPane;
    private StackPane findRandomMatchPane;
    private CreateRoomController createRoomController;
    private JoinRoomController joinRoomController;
    private FindRandomMatchController findRandomMatchController;
    private String serverIp;

    public void initialize() {
        // Load the overlays
        loadCreateRoomOverlay();
        loadJoinRoomOverlay();
        loadFindRandomMatchOverlay();
    }

    private void registerClientCallbacks(LobbyWebSocketClient client) {
        // Đăng ký callback xử lý khi phòng được tạo thành công
        client.setOnRoomCreated((roomName, username, time) -> {
            javafx.application.Platform.runLater(() -> {
                handleRoomCreated(roomName, username, time);
            });
        });

        client.setOnRoomJoined((roomName, username, time, creatorUsername) -> {
            javafx.application.Platform.runLater(() -> {
                handleRoomJoined(roomName, username, time, creatorUsername);
            });
        });

        // Thêm callback cho tìm trận ngẫu nhiên
        client.setOnRandomMatchFound((roomName, username,  color,time, opponentUsername) -> {
            javafx.application.Platform.runLater(() -> {
                handleRandomMatchFound(roomName, username, color, time, opponentUsername);
            });
        });

        // Xử lý thông báo trạng thái đang chờ
        client.setOnWaitingStatus((status) -> {
            javafx.application.Platform.runLater(() -> {
                System.out.println("Trạng thái chờ: " + status);
                // Có thể cập nhật UI để hiển thị trạng thái chờ
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
            System.err.println("Error loading join room overlay: " + e.getMessage());
        }
    }

    private void loadFindRandomMatchOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/find_random_match.fxml"));
            findRandomMatchPane = (StackPane) loader.load();
            findRandomMatchController = loader.getController();
            findRandomMatchController.setOnlineOptionsController(this);

            // Initially invisible
            findRandomMatchPane.setVisible(false);

            // Add to the parent StackPane
            onlineOptionPane.getChildren().add(findRandomMatchPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading find random match overlay: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateRoom() {
        Sounds.playButtonClickSound();
        String serverIp = "";
        if(autoFindServerCheckBox.isSelected()){
            serverIp = ServerDiscoveryClient.findServerIp();
        }
        System.out.println("Create room option selected");
        if(!serverIp.isEmpty()){
            setServerIp(serverIp);
        }
        showCreateRoomOverlay(serverIp);
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
        LobbyManager.getInstance().ensureClientInitialized(serverIp);
        LobbyWebSocketClient client = LobbyManager.getInstance().getClient();
        registerClientCallbacks(client);
        showCreateRoomOverlay(serverIp);
    }

    @FXML
    private void handleFindRoom() {
        Sounds.playButtonClickSound();
        String serverIp = "";
        if(autoFindServerCheckBox.isSelected()){
            serverIp = ServerDiscoveryClient.findServerIp();
        }
        System.out.println("Find room option selected");
        if(!serverIp.isEmpty()){
            setServerIp(serverIp);
        }
        showJoinRoomOverlay(serverIp);
    }

    @FXML
    private void handleQuickMatch() {
        Sounds.playButtonClickSound();
        String serverIp = "";
        if(autoFindServerCheckBox.isSelected()){
            serverIp = ServerDiscoveryClient.findServerIp();
        }
        System.out.println("Quick match option selected");
        if(!serverIp.isEmpty()){
            setServerIp(serverIp);
        }
        showFindRandomMatchOverlay(serverIp);
    }

    @FXML
    private void handleBackButton() {
        Sounds.playButtonClickSound();
        // Close this overlay to return to mode selection
        if (modeSelectionController != null) {
            modeSelectionController.hideOnlineOption();
        }
    }

    public void showCreateRoomOverlay(String serverIp) {
        if (createRoomPane != null) {
            // Hide the main UI controls
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
                    node.setVisible(false);
                }
            }

            // Make sure other overlays are hidden
            if (joinRoomPane != null) {
                joinRoomPane.setVisible(false);
            }
            if (findRandomMatchPane != null) {
                findRandomMatchPane.setVisible(false);
            }

            // Show the create room overlay
            createRoomPane.setVisible(true);

            if (createRoomController != null) {
                createRoomController.setServerIp(serverIp);
            } else {
                System.err.println("createRoomController is not initialized.");
            }
        }
    }

    public void showJoinRoomOverlay(String serverIp) {
        if (joinRoomPane != null) {
            // Hide the main UI controls
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
                    node.setVisible(false);
                }
            }

            // Make sure other overlays are hidden
            if (createRoomPane != null) {
                createRoomPane.setVisible(false);
            }
            if (findRandomMatchPane != null) {
                findRandomMatchPane.setVisible(false);
            }

            // Show the join room overlay
            joinRoomPane.setVisible(true);
            // Set the server IP in the join room controller
            if (joinRoomController != null) {
                joinRoomController.setServerIp(serverIp);
            } else {
                System.err.println("JoinRoomController is not initialized.");
            }
        }
    }

    public void showFindRandomMatchOverlay(String serverIp) {
        if (findRandomMatchPane != null) {
            // Hide the main UI controls
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
                    node.setVisible(false);
                }
            }

            // Make sure other overlays are hidden
            if (createRoomPane != null) {
                createRoomPane.setVisible(false);
            }
            if (joinRoomPane != null) {
                joinRoomPane.setVisible(false);
            }

            // Show the find random match overlay
            findRandomMatchPane.setVisible(true);

            if (findRandomMatchController != null) {
                findRandomMatchController.setServerIp(serverIp);
            } else {
                System.err.println("findRandomMatchController is not initialized.");
            }
        }
    }

    public void hideCreateRoomOverlay() {
        if (createRoomPane != null) {
            createRoomPane.setVisible(false);

            // Show the main UI controls again
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
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
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void hideFindRandomMatchOverlay() {
        if (findRandomMatchPane != null) {
            findRandomMatchPane.setVisible(false);

            // Show the main UI controls again
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                Node node = onlineOptionPane.getChildren().get(i);
                if (node != createRoomPane && node != joinRoomPane && node != findRandomMatchPane) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void handleRoomCreated(String roomName, String playerName, int timeSelection) {
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

            ChessWebSocketClient chessClient = GameManager.getInstance().createClient(controller, serverIp );
            chessClient.connectBlocking();

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, playerName, Player.RED, timeSelection * 60, "");
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

    public void handleRoomJoined(String roomName, String playerName, int timeSelection, String creatorUsername) {
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chơi game
            OnlineGameController controller = loader.getController();
            ChessWebSocketClient chessClient = GameManager.getInstance().createClient(controller, serverIp);

            chessClient.connectBlocking();

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, playerName, Player.BLACK, timeSelection * 60, creatorUsername);
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

    public void handleRandomMatchFound(String roomName, String playerName, String color, int timeSelection, String opponentUsername) {
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chơi game
            OnlineGameController controller = loader.getController();
            ChessWebSocketClient chessClient = GameManager.getInstance().createClient(controller, serverIp);

            chessClient.connectBlocking();

            // Phía máy chủ sẽ quyết định bên nào đi trước (RED) và bên nào đi sau (BLACK)
            // Giả sử ở đây server trả về phe đi trước là RED
            Player playerSide = color.equalsIgnoreCase("RED") ? Player.RED : Player.BLACK; // Hoặc BLACK tùy theo server gửi về

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, playerName, playerSide, timeSelection * 60,opponentUsername);
            Stage stage = (Stage) ((Node) createRoomButton).getScene().getWindow();

            // Chuyển scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cờ Tướng");
            stage.show();

            if(playerSide == Player.RED){
                controller.sendStartGame(roomName);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            showNotification("Không thể vào phòng chơi!");
        }
    }

    // Thêm phương thức mới để xử lý trạng thái đang chờ tìm trận
    public void handleWaitingForMatch() {
        // Thông báo đang chờ tìm trận
        System.out.println("Đang chờ tìm trận ngẫu nhiên...");
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