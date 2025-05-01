package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.List;

public class LoadController {

    @FXML
    private VBox loadPane;
    @FXML
    private ListView<String> loadList;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Để trống nếu không có trận đấu
        List<String> savedMatches = loadSavedMatches(); // Thay bằng phương thức thực tế
        loadList.setItems(FXCollections.observableArrayList(savedMatches));
    }

    // Phương thức giả lập, thay bằng logic thực tế
    private List<String> loadSavedMatches() {
        return List.of(); // Trả về danh sách trống, thêm dữ liệu thực tế sau
        // Ví dụ: return List.of("Trận đã lưu 1: vs AI", "Trận đã lưu 2: vs Người chơi");
    }

    @FXML
    private void handleCloseButton() {
        if (stage != null) {
            stage.close();
        }
    }
}