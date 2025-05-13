package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class SettingsOffline2PlayersController {
    @FXML
    private TextField timeInput;
    @FXML
    private Slider volumeSlider;

    private int selectedTimeMinutes = -1; // -1 nếu không hợp lệ
    private double selectedVolume = 0.5; // Giá trị mặc định (50%)
    private ModeSelectionController modeSelectionController;
    private boolean isConfirmed = false;

    public void setModeSelectionController(ModeSelectionController controller) {
        this.modeSelectionController = controller;
    }

    public int getSelectedTimeMinutes() {
        return selectedTimeMinutes;
    }

    public double getSelectedVolume() {
        return selectedVolume;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    @FXML
    public void initialize() {
        // Cập nhật giá trị âm lượng khi thanh trượt thay đổi
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedVolume = newVal.doubleValue() / 100.0; // Chuyển sang khoảng 0.0-1.0
        });
    }

    @FXML
    private void confirmSelection() {
        try {
            String timeText = timeInput.getText().trim();
            int minutes = Integer.parseInt(timeText);
            if (minutes > 0) {
                selectedTimeMinutes = minutes;
                isConfirmed = true;
                if (modeSelectionController != null) {
                    modeSelectionController.hideSettingsOverlay();
                }
            } else {
                timeInput.setText(""); // Xóa nếu không hợp lệ
                timeInput.setPromptText("Nhập số phút lớn hơn 0");
            }
        } catch (NumberFormatException e) {
            timeInput.setText(""); // Xóa nếu không hợp lệ
            timeInput.setPromptText("Vui lòng nhập số hợp lệ");
        }
    }

    @FXML
    private void cancelSelection() {
        selectedTimeMinutes = -1;
        selectedVolume = 0.5; // Đặt lại giá trị mặc định
        isConfirmed = false;
        if (modeSelectionController != null) {
            modeSelectionController.hideSettingsOverlay();
        }
    }
}