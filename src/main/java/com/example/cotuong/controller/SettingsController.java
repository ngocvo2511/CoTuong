package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class SettingsController {
    @FXML
    private TextField timeInput;
    @FXML
    private Slider volumeSlider;
    @FXML
    private CheckBox timeLimitCheckBox;
    @FXML
    private RadioButton playerFirst;
    @FXML
    private RadioButton aiFirst;
    @FXML
    private ToggleGroup firstMoveGroup;
    @FXML
    private Label volumeLabel; // Thêm label cho volume

    private int selectedTime = 10; // Giá trị mặc định (phút)
    private double selectedVolume = 0.5;
    private boolean isPlayerFirst = true; // Mặc định người chơi đi trước
    private boolean isTimeLimited = true; // Mặc định bật giới hạn thời gian
    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController controller) {
        this.mainMenuController = controller;
    }

    @FXML
    public void initialize() {
        timeInput.setText("10"); // Giá trị mặc định
        volumeSlider.setValue(50); // Giá trị mặc định
        volumeLabel.setText("50"); // Hiển thị giá trị volume ban đầu
        timeLimitCheckBox.setSelected(true); // Mặc định bật giới hạn thời gian

        // Listener cho volume slider để cập nhật label
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectedVolume = newVal.doubleValue() / 100.0;
            volumeLabel.setText(String.valueOf(Math.round(newVal.doubleValue())));
        });

        timeInput.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                String timeText = newVal.trim();
                if (!timeText.isEmpty()) {
                    selectedTime = Integer.parseInt(timeText);
                    if (selectedTime <= 0) {
                        selectedTime = 10;
                        timeInput.setText("10");
                        timeInput.setPromptText("Nhập số phút lớn hơn 0");
                    }
                }
            } catch (NumberFormatException e) {
                selectedTime = 10;
                timeInput.setText("10");
                timeInput.setPromptText("Vui lòng nhập số hợp lệ");
            }
        });

        timeLimitCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isTimeLimited = newVal;
            timeInput.setDisable(!newVal); // Vô hiệu hóa timeInput nếu không giới hạn thời gian
        });

        // Cập nhật isPlayerFirst dựa trên RadioButton
        firstMoveGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            isPlayerFirst = newToggle == playerFirst;
        });
    }

    @FXML
    private void confirmSelection() {
        Sounds.playButtonClickSound();
        try {
            if (isTimeLimited) {
                String timeText = timeInput.getText().trim();
                selectedTime = Integer.parseInt(timeText);
                if (selectedTime <= 0) {
                    timeInput.setText("");
                    timeInput.setPromptText("Nhập số phút lớn hơn 0");
                    return;
                }
            } else {
                selectedTime = 0; // Không giới hạn thời gian
            }
            if (mainMenuController != null) {
                mainMenuController.setSelectedTime(selectedTime);
                mainMenuController.setPlayerFirst(isPlayerFirst);
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
            Sounds.playButtonClickSound();
            mainMenuController.hideSettings();
        }
    }

    // Getters
    public int getSelectedTime() {
        return selectedTime;
    }

    public boolean isPlayerFirst() {
        return isPlayerFirst;
    }

    public double getSelectedVolume() {
        return selectedVolume;
    }
}