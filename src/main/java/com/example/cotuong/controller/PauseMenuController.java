package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseMenuController {

    @FXML
    private StackPane pausePane;

    @FXML
    private Button resumeButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button mainMenuButton;

    private OfflineGameController offlineGameController;

    private Parent settingsPane;
    private SettingsController settingsController;

    @FXML
    private StackPane exitConfirmPane;

    private ExitConfirmController exitConfirmController;


    @FXML
    private void initialize() {
        pausePane.setVisible(true);
    }

    @FXML
    private void handleResume() {
        Sounds.playButtonClickSound();
        if (offlineGameController != null) {
            offlineGameController.removePauseOverlay();
            offlineGameController.ContinueTimer();
        }
    }

    @FXML
    private void handleNewGame() {
        Sounds.playButtonClickSound();
        // Logic sẽ được thêm sau
    }

    @FXML
    private void handleSettings() {
        Sounds.playButtonClickSound();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/settings.fxml"));
            Parent settingsView = loader.load();
            SettingsController settingsController = loader.getController();

            // Gọi phương thức khởi tạo lại giao diện mỗi khi mở
            settingsController.refreshUIFromSettings();

            // Đặt hành vi khi bấm nút "Hủy"
            settingsController.setOnCancel(() -> {
                pausePane.getChildren().remove(settingsView); // Xóa view settings khỏi StackPane
            });

            pausePane.getChildren().add(settingsView); // Hiển thị settings lên trên pausePane

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMainMenu() {
        Sounds.playButtonClickSound();
        if (offlineGameController != null) {
            showExitConfirm();
        }
    }

    public void showExitConfirm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/exit_confirm.fxml"));
            StackPane confirmPane = loader.load();

            exitConfirmController = loader.getController();
            exitConfirmController.setOnCancel(() -> {
                pausePane.getChildren().remove(confirmPane); // rootPane là StackPane gốc của pause menu
            });

            exitConfirmController.setOnConfirmExit(() -> {
                // Gọi lại logic thoát về Main Menu
                try {
                    // Tải FXML của màn hình chính
                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml"));
                    Parent root = loader1.load();

                    // Lấy stage hiện tại
                    Stage stage = (Stage) pausePane.getScene().getWindow();

                    MainMenuController controller = loader1.getController();
                    controller.setStage(stage); //

                    // Hiển thị màn hình chính
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            pausePane.getChildren().add(confirmPane); // Đưa confirmPane lên top
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOfflineGameController(OfflineGameController controller) {
        this.offlineGameController = controller;
    }
}
