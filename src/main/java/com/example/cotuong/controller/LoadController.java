package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.saveservice.SaveMatchManager;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoadController {

    @FXML
    private StackPane loadPane;
    @FXML
    private ListView<String> loadSlotContainer;
    private GameState currentGameState;
    private  OfflineGameController offlineGameController;
    private final File folder = new File("Save_game");
    private final Gson gson = new Gson();
    private final SaveMatchManager saveMatchManager = new SaveMatchManager();

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() throws Exception {
        loadListView();
        // Cho phép nhấn chọn
        loadSlotContainer.setFocusTraversable(true);

        // Bắt sự kiện click vào slot
        loadSlotContainer.setCellFactory(listView -> {
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
                if (!cell.isEmpty()) {
                    String fileName = "Save_" + (cell.getIndex()+1) + ".xqi";
                    File file = new File(folder,fileName);
                    try {
                        //read file
                        GameState gameState = saveMatchManager.load(file);
                        if(gameState instanceof GameStateAI) ((GameStateAI) gameState).initValuePiece();

                        //load game
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
                        Parent root = loader.load();
                        OfflineGameController controller = loader.getController();
                        controller.initialize(gameState);

                        Stage stage = (Stage) ((Node) loadSlotContainer).getScene().getWindow();
                        Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

                        // Set new Scene
                        stage.setScene(gameScene);
                        stage.setTitle("Cờ Tướng");
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
                loadSlotContainer.getItems().add(mode + "| " + timeFile);
            }
            else{
                loadSlotContainer.getItems().add("Trống");
            }
        }
    }


    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            mainMenuController.hideLoad(); // Hide the load overlay
        }
    }
}