package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ExitConfirmController {

    @FXML
    private StackPane exitConfirmPane;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmExitButton;

    @FXML
    private void initialize() {
        // Khởi tạo dialog ở trạng thái ẩn

        // Thêm hiệu ứng focus cho các button
        cancelButton.setFocusTraversable(true);
        confirmExitButton.setFocusTraversable(true);

        // Xử lý khi click vào background để đóng dialog
        exitConfirmPane.setOnMouseClicked(event -> {
            if (event.getTarget() == exitConfirmPane) {
                handleCancel();
            }
        });
    }

    private Runnable onCancel;
    private Runnable onConfirmExit;

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void setOnConfirmExit(Runnable onConfirmExit) {
        this.onConfirmExit = onConfirmExit;
    }

    @FXML
    private void handleCancel() {
        Sounds.playButtonClickSound();
        if (onCancel != null) {
            onCancel.run();
        }
    }

    @FXML
    private void handleConfirmExit() {
        Sounds.playButtonClickSound();
        if (onConfirmExit != null) {
            onConfirmExit.run();
        }
    }
}