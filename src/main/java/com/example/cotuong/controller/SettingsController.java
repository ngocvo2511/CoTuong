package com.example.cotuong.controller;

import com.example.cotuong.utils.SettingsManager;
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

    private Runnable onCancel;

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    private void cancelSettings() {
        Sounds.playButtonClickSound();
        if (onCancel != null) {
            onCancel.run(); // Gọi lại khi người dùng bấm Hủy
        }
    }

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController controller) {
        this.mainMenuController = controller;
    }

    @FXML
    public void initialize() {
        SettingsManager settings = SettingsManager.getInstance();

        // Đặt các giá trị ban đầu từ SettingsManager
        int savedTime = settings.getTimeLimit();
        int savedVolume = settings.getVolume();
        boolean isTimeLimited = settings.isTimeLimitEnabled();
        boolean isPlayerFirst = settings.isPlayerFirst();

        timeInput.setText(String.valueOf(savedTime));
        volumeSlider.setValue(savedVolume);
        volumeLabel.setText(String.valueOf(savedVolume));
        timeLimitCheckBox.setSelected(isTimeLimited);
        timeInput.setDisable(!isTimeLimited);

        // Chọn RadioButton theo người đi trước
        firstMoveGroup.selectToggle(isPlayerFirst ? playerFirst : aiFirst);

        // Volume slider listener
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeLabel.setText(String.valueOf(newVal.intValue()));
        });

        timeInput.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { // Khi mất focus
                String input = timeInput.getText().trim();
                try {
                    int time = Integer.parseInt(input);
                    if (time <= 0) {
                        timeInput.setText("10");
                    }
                } catch (NumberFormatException e) {
                    timeInput.setText("10");
                }
            }
        });

        // Checkbox listener cho giới hạn thời gian
        timeLimitCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            timeInput.setDisable(!newVal);
        });
    }

    public void refreshUIFromSettings() {
        SettingsManager settings = SettingsManager.getInstance();

        int savedTime = settings.getTimeLimit();
        int savedVolume = settings.getVolume();
        boolean isTimeLimited = settings.isTimeLimitEnabled();
        boolean isPlayerFirst = settings.isPlayerFirst();

        timeInput.setText(String.valueOf(savedTime));
        volumeSlider.setValue(savedVolume);
        volumeLabel.setText(String.valueOf(savedVolume));
        timeLimitCheckBox.setSelected(isTimeLimited);
        timeInput.setDisable(!isTimeLimited);

        firstMoveGroup.selectToggle(isPlayerFirst ? playerFirst : aiFirst);
    }


    @FXML
    private void confirmSelection() {

        SettingsManager settings = SettingsManager.getInstance();

        try {
            boolean timeLimited = timeLimitCheckBox.isSelected();
            int timeLimit = Integer.parseInt(timeInput.getText().trim());
            int volume = (int) volumeSlider.getValue();
            boolean isPlayerFirst = firstMoveGroup.getSelectedToggle() == playerFirst;

            // Lưu vào SettingsManager
            settings.setVolume(volume);
            settings.setTimeLimitEnabled(timeLimited);
            settings.setTimeLimit(timeLimit);
            settings.setPlayerFirst(isPlayerFirst);
            Sounds.setVolume(volume);

            // Gửi lại về MainMenu (nếu cần)
            if (mainMenuController != null) {
                mainMenuController.setSelectedTime(timeLimited ? timeLimit : 0);
                mainMenuController.setPlayerFirst(isPlayerFirst);
                mainMenuController.hideSettings();
            } else if (onCancel != null) {
                onCancel.run(); // dùng chung callback để ẩn settings nếu đến từ PauseMenu
            }
        } catch (NumberFormatException e) {
            timeInput.setText("10");
        }
        Sounds.playButtonClickSound();
    }



    @FXML
    private void cancelSelection() {
        Sounds.playButtonClickSound();
        if (mainMenuController != null) {
            mainMenuController.hideSettings();
        }
        else if (onCancel != null) {
            onCancel.run(); // dùng chung callback để ẩn settings nếu đến từ PauseMenu
        }
    }
}