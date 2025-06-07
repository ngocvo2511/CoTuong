package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.EndReason;
import com.example.cotuong.chesslogic.HistoryMatchRecord;
import com.example.cotuong.chesslogic.HistoryMatchView;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.saveservice.SaveHistoryMatchManager;
import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
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
                HistoryMatchView selected = historyList.getSelectionModel().getSelectedItem();
                if (selected != null && selected.file != null) {
                    try {
                        openReplay(selected.file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        });
    }

    private void openReplay(File file) throws Exception {
        HistoryMatchRecord historyMatchRecord = SaveHistoryMatchManager.load(file);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
        Parent root = loader.load();
        OfflineGameController controller = loader.getController();
        controller.initialize(historyMatchRecord);

        Stage stage = (Stage) ((Node) historyList).getScene().getWindow();
        Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

        // Set new Scene
        stage.setScene(gameScene);
        stage.setTitle("Cờ Tướng");
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
            if (record.mode.equals("Chơi hai người")) {
                winner = (record.result.getWinner() == Player.BLACK) ? "Đen thắng" : "Đỏ thắng";
            } else {
                winner = record.isWin ? "Người thắng" : "Máy thắng";
            }
            Player currentPlayer = record.winner.opponent();


            String displayText = record.mode + ": " + winner +
                    " (" + getReasonText(record.result.getReason(), currentPlayer, record.mode) + ") | " + timeFile;

            historyList.getItems().add(new HistoryMatchView(file, displayText));
        }
    }
    private String playerString(Player player, String mode) {
        if (Objects.equals(mode, "Chơi với máy")){
            return switch (player) {
                case RED -> "Người";
                case BLACK -> "Máy";
                default -> "";
            };
        }
        else{
            return switch (player) {
                case RED -> "Đỏ";
                case BLACK -> "Đen";
                default -> "";
            };
        }
    }

    private String getReasonText(EndReason reason, Player currentPlayer, String mode) {
        return switch (reason) {
            case STALEMATE -> playerString(currentPlayer, mode) + " hết nước đi";
            case CHECKMATE -> playerString(currentPlayer, mode) + " bị chiếu bí";
            case INSUFFICIENT_MATERIAL -> "Hòa vì thiếu quân";
            case FIFTY_MOVE_RULE -> "Hòa vì 50 nước không ăn quân";
            case THREEFOLD_REPETITION -> "Hòa vì lặp lại nước đi 3 lần";
            case TIMEFORFEIT -> playerString(currentPlayer, mode) + " hết thời gian";
            case PLAYER_DISCONNECTED -> playerString(currentPlayer, mode) + " đã thoát";
            default -> "";
        };
    }
    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            Sounds.playButtonClickSound();
            mainMenuController.hideHistory(); // Hide the history overlay
        }
    }
}