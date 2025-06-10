package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.saveservice.SaveMatchManager;
import com.example.cotuong.utils.Sounds;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaveController {
    @FXML
    private ListView<String> saveSlotContainer;
    private GameState currentGameState;
    private  OfflineGameController offlineGameController;
    private final File folder = new File("Save_game");
    private final SaveMatchManager saveMatchManager = new SaveMatchManager();

    public void setGameState(GameState currentGamestate){
        this.currentGameState = currentGamestate;
    }
    public void setOfflineGameController(OfflineGameController offlineGameController){
        this.offlineGameController = offlineGameController;
    }

    public void initialize() throws Exception {
        loadListView();
        // Cho phép nhấn chọn
        saveSlotContainer.setFocusTraversable(true);

        // Bắt sự kiện click vào slot
        saveSlotContainer.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-font-size: 16px; -fx-padding: 10;");
                    }
                }
            };

            // Bắt sự kiện click cho từng item
            cell.setOnMouseClicked(event -> {
                Sounds.playButtonClickSound();
                if (!cell.isEmpty()) {
                    String fileName = "Save_" + (cell.getIndex()+1) + ".xqi";
                    File file = new File(folder,fileName);
                    try {
                        saveMatchManager.save(currentGameState,file);
                        LocalDateTime dateTime = Instant.ofEpochMilli(file.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        String timeFile = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String mode;

                        if (currentGameState instanceof GameStateAI) mode = "Chơi với máy";
                        else if (currentGameState instanceof GameState2P) mode = "Chơi 2 người";
                        else mode = "Không rõ";

                        String display = mode + " | " + timeFile;
                        saveSlotContainer.getItems().set(cell.getIndex(), display);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            return cell;
        });

    }
    private void loadListView() throws Exception {
        if(!folder.exists()) folder.mkdir();
        for (int i = 1; i <= 10; i++) {
            String filename = "Save_" + i + ".xqi";
            File file = new File(folder,filename);
            if(file.exists()){
                String mode;
                LocalDateTime dateTime = Instant.ofEpochMilli(file.lastModified())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                String timeFile =  dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                GameState gameState = saveMatchManager.load(file);
                if(gameState instanceof GameStateAI) mode = "Chơi với máy";
                else if(gameState instanceof GameState2P) mode = "Chơi 2 người";
                else mode = "Không rõ";
                saveSlotContainer.getItems().add(mode + "| " + timeFile);
            }
            else{
                saveSlotContainer.getItems().add("Trống");
            }
        }
    }

    @FXML
    private void handleCloseButton() {
        Sounds.playButtonClickSound();
        offlineGameController.removeOverlay();
    }
}
