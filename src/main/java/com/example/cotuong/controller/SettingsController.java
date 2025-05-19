package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML
    private TextField timeInput;
    @FXML
    private Slider volumeSlider;

    private double selectedVolume = 0.5;
    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController controller) {
        this.mainMenuController = controller;
    }

    @FXML
    public void initialize() {
        timeInput.setText("10"); // Giá trị mặc định
        volumeSlider.setValue(50); // Giá trị mặc định

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedVolume = newVal.doubleValue() / 100.0;
        });
    }

    @FXML
    private void confirmSelection() {
        try {
            String timeText = timeInput.getText().trim();
            int minutes = Integer.parseInt(timeText);
            if (minutes <= 0) {
                timeInput.setText("");
                timeInput.setPromptText("Nhập số phút lớn hơn 0");
                return;
            }
            if (mainMenuController != null) {
                mainMenuController.hideSettings();
            }
        } catch (NumberFormatException e) {
            timeInput.setText("");
            timeInput.setPromptText("Vui lòng nhập số hợp lệ");
        }
    }

    @FXML
    private void cancelSelection() {
        if (mainMenuController != null) {
            mainMenuController.hideSettings();
        }
    }
}