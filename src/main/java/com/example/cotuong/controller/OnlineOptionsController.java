package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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
    private CreateRoomController createRoomController;

    public void initialize() {
        // Load the create room overlay
        loadCreateRoomOverlay();
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

    @FXML
    private void handleCreateRoom() {
        System.out.println("Create room option selected");
        showCreateRoomOverlay();
    }

    @FXML
    private void handleFindRoom() {
        System.out.println("Find room option selected");
        // TODO: Implement room finding functionality
        startOnlineGame("find");
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
            // Hide the online options controls but keep the overlay active
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                if (onlineOptionPane.getChildren().get(i) != createRoomPane) {
                    onlineOptionPane.getChildren().get(i).setVisible(false);
                }
            }

            // Show the create room overlay
            createRoomPane.setVisible(true);
        }
    }

    public void hideCreateRoomOverlay() {
        if (createRoomPane != null) {
            createRoomPane.setVisible(false);

            // Show the online options controls again
            for (int i = 0; i < onlineOptionPane.getChildren().size(); i++) {
                if (onlineOptionPane.getChildren().get(i) != createRoomPane) {
                    onlineOptionPane.getChildren().get(i).setVisible(true);
                }
            }
        }
    }

    public void handleRoomCreated(String roomName, String playerName, String timeSelection, String team) {
        System.out.println("Room created successfully!");
        System.out.println("Room Name: " + roomName);
        System.out.println("Player Name: " + playerName);
        System.out.println("Time: " + timeSelection);
        System.out.println("Team: " + team);

        // TODO: Implement room creation with server communication
        // For now, we'll just hide all overlays and start the game
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }

        // TODO: Navigate to waiting room or directly to game
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