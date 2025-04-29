package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private StackPane joinRoomPane;
    private CreateRoomController createRoomController;
    private JoinRoomController joinRoomController;

    public void initialize() {

        // Load the create room overlay
        loadCreateRoomOverlay();
        loadJoinRoomOverlay();
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

    public void handleRoomJoined(String roomName, String playerName, String timeSelection, String team) {
        System.out.println("Room joined successfully!");
        System.out.println("Room Name: " + roomName);
        System.out.println("Player Name: " + playerName);
        System.out.println("Time: " + timeSelection);
        System.out.println("Team: " + team);

        // TODO: Implement room joining with server communication
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