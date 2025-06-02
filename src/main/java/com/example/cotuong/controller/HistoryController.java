package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.HistoryMatchRecord;
import com.example.cotuong.chesslogic.HistoryMatchView;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.saveservice.SaveHistoryMatchManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class HistoryController {

    @FXML
    private StackPane historyPane;
    @FXML
    private ListView<HistoryMatchView> historyList;
    private final File folder = new File("History_game");
    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() throws Exception {
        loadMatchHistory();

        historyList.setCellFactory(listView -> {
            ListCell<HistoryMatchView> cell = new ListCell<>() {
                @Override
                protected void updateItem(HistoryMatchView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.display);
                    }
                }
            };

            // Bật wrap và tự động cập nhật khi thay đổi kích thước
            cell.setWrapText(true);
            cell.prefWidthProperty().bind(historyList.widthProperty().subtract(20)); // tránh tràn khi có scrollbar

            return cell;
        });

        // Optional: double click để mở lại ván đấu
        historyList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                HistoryMatchView selected = historyList.getSelectionModel().getSelectedItem();
                if (selected != null && selected.file != null) {
                    openReplay(selected.file);
                }
            }
        });
    }

    private void openReplay(File file) throws Exception {
        HistoryMatchRecord historyMatchRecord = SaveHistoryMatchManager.load(file);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
        Scene gameScene = new Scene(loader.load());
        OfflineGameController controller = loader.getController();


    }


    private void loadMatchHistory() throws Exception {
        if (!folder.exists()) folder.mkdir();

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".hst"));

        if (files == null) return;

        // Sắp xếp theo thời gian sửa đổi mới nhất → cũ nhất
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        // Lấy tối đa 30 file đầu tiên
        int limit = Math.min(files.length, 30);
        for (int i = 0; i < limit; i++) {
            File file = files[i];

            LocalDateTime dateTime = Instant.ofEpochMilli(file.lastModified())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            String timeFile = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            HistoryMatchRecord record = SaveHistoryMatchManager.load(file);

            String winner;
            if (Objects.equals(record.mode, "Chơi 2 người")) {
                winner = (record.result.getWinner() == Player.BLACK) ? "Đen thắng" : "Đỏ thắng";
            } else {
                winner = record.isWin ? "Người thắng" : "Máy thắng";
            }

            String displayText = record.mode + ": " + winner +
                    " (" + record.result.getReason() + ") | " + timeFile;

            historyList.getItems().add(new HistoryMatchView(file, displayText));
        }
    }
    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            mainMenuController.hideHistory(); // Hide the history overlay
        }
    }
}