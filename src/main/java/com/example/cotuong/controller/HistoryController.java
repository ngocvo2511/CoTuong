package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import java.util.List;

public class HistoryController {

    @FXML
    private StackPane historyPane;
    @FXML
    private ListView<String> historyList;

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() {
        // Load match history and set it to the ListView
        List<String> matchHistory = loadMatchHistory();
        historyList.setItems(FXCollections.observableArrayList(matchHistory));

        // Customize ListView cell rendering
        historyList.setCellFactory(listView -> new ListCell<>() {
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

    // Method to load match history (placeholder, replace with actual logic)
    private List<String> loadMatchHistory() {
        try {
            // Replace with actual logic to fetch match history, e.g., from a file or database
            return List.of(

            );
        } catch (Exception e) {
            System.err.println("Lỗi khi tải lịch sử trận đấu: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            mainMenuController.hideHistory(); // Hide the history overlay
        }
    }
}