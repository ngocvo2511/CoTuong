package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import java.util.List;

public class LoadController {

    @FXML
    private StackPane loadPane;
    @FXML
    private ListView<String> loadList;

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() {
        // Load saved matches and set to the ListView
        List<String> savedMatches = loadSavedMatches();
        loadList.setItems(FXCollections.observableArrayList(savedMatches));

        // Customize ListView cell rendering
        loadList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    text.setStyle("-fx-font-size: 16px; -fx-fill: #333333;");
                    setGraphic(text);
                }
            }
        });
    }

    // Method to load saved matches (placeholder, replace with actual logic)
    private List<String> loadSavedMatches() {
        try {
            // Replace with actual logic to fetch saved matches, e.g., from a file or database
            return List.of(
                    "Trận đã lưu 1: vs AI (Dễ) - 2025-05-01",
                    "Trận đã lưu 2: vs Người chơi - 2025-05-02",
                    "Trận đã lưu 2: vs Người chơi - 2025-05-02",
                    "Trận đã lưu 2: vs Người chơi - 2025-05-02",
                    "Trận đã lưu 2: vs Người chơi - 2025-05-02",
                    "Trận đã lưu 2: vs Người chơi - 2025-05-02"
            );
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách trận đã lưu: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            mainMenuController.hideLoad(); // Hide the load overlay
        }
    }
}