package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.network.ChessWebSocketClient;
import com.example.cotuong.network.LobbyManager;
import com.example.cotuong.network.LobbyWebSocketClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CreateRoomController {

    @FXML
    private TextField roomNameField;
    @FXML
    private TextField playerNameField;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private Button createRoomButton;
    @FXML
    private Button backButton;

    private Stage stage;
    private Scene previousScene;
    LobbyWebSocketClient client = LobbyManager.getInstance().getClient();



    public void initialize() {
        timeChoiceBox.getItems().addAll("5 phút", "10 phút", "15 phút", "30 phút");
        timeChoiceBox.setValue("10 phút");

        client = LobbyManager.getInstance().getClient();

        client.setOnRoomCreated((roomName, username, time) -> {
            Platform.runLater(() -> {
                navigateToGameOnlineE(roomName, username, Player.RED, time);
            });
        });

        client.setOnError(message -> {
            Platform.runLater(() -> {
//                showNotification(message);
            });
        });
    }

    private void navigateToGameOnlineE(String roomName, String username, Player color, int timeLimit) {
        System.out.println("Navigating to Online Game Screen"); // <-- thêm dòng này

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chơi game
            OnlineGameController controller = loader.getController();
            URI uri = new URI("ws://localhost:8080/ws/chess");
            ChessWebSocketClient chessClient = new ChessWebSocketClient(uri, controller);
            chessClient.connectBlocking();

            controller.setWebSocketClient(chessClient);
            controller.initializeGame(roomName, username, color, timeLimit);

            // Chuyển scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cờ Tướng - Trực tuyến");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
//            showNotification("Không thể vào phòng chơi!");
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    private void handleCreateRoomButton() {
        String roomName = roomNameField.getText();
        String username = playerNameField.getText();
        if (roomName.isEmpty()) {
//            showNotification("Vui lòng nhập tên phòng");
            return;
        }
        if (username.isEmpty()) {
            username = "Người chơi 1";
        }

        int time = switch (timeChoiceBox.getValue()) {
            case "5 phút" -> 5 * 60;
            case "10 phút" -> 10 * 60;
            case "15 phút" -> 15 * 60;
            case "30 phút" -> 30 * 60;
            default -> 10 * 60;
        };

        client.createRoom(roomName, username, time);
    }

    @FXML
    private void handleBackButton() {
        if (previousScene != null) {
            stage.setScene(previousScene);
            stage.setTitle("Cờ Tướng - Tùy Chọn Online");
        }
    }
}