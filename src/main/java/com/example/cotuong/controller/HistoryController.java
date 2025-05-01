package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.List;

public class HistoryController {

    @FXML
    private VBox historyPane;
    @FXML
    private ListView<String> historyList;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Để trống nếu không có trận đấu
        List<String> matchHistory = loadMatchHistory(); // Thay bằng phương thức thực tế
        historyList.setItems(FXCollections.observableArrayList(matchHistory));
    }

    // Phương thức giả lập, thay bằng logic thực tế
    private List<String> loadMatchHistory() {
        return List.of(); // Trả về danh sách trống, thêm dữ liệu thực tế sau
        // Ví dụ: return List.of("Trận 1: Thắng vs AI", "Trận 2: Thua vs Người chơi");
    }

    @FXML
    private void handleCloseButton() {
        if (stage != null) {
            stage.close();
        }
    }
}