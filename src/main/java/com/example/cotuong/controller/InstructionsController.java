package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructionsController {

    @FXML
    private VBox instructionsPane;
    @FXML
    private TextArea instructionsText;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        instructionsText.setText(
                "ĐIỀN LUẬT CHƠI NHÉ"
        );
    }

    @FXML
    private void handleCloseButton() {
        if (stage != null) {
            stage.close();
        }
    }
}