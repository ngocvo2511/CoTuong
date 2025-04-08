package com.example.cotuong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.io.IOException;

public class MainMenu extends BorderPane {
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private Button playButton;


    public MainMenu(Stage stage) {
        this.stage = stage;
        initializeUI();
    }

    private void initializeUI() {
        // Set background image
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResourceAsStream("/com/example/cotuong/images/background.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        setBackground(new Background(backgroundImage));

        // Set up the title bar for dragging
        HBox titleBar = createTitleBar();
        setTop(titleBar);

        // Set up the center content with main title and buttons
        StackPane centerContent = createCenterContent();
        setCenter(centerContent);

        // Make the stage draggable
        titleBar.setOnMousePressed(event -> {
            Stage stage = (Stage) getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setPrefHeight(30);
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPadding(new Insets(0, 30, 0, 0));

        // Create window control buttons
        Button minimizeButton = createWindowControlButton(MaterialDesign.MDI_WINDOW_MINIMIZE);
        Button maximizeButton = createWindowControlButton(MaterialDesign.MDI_WINDOW_MAXIMIZE);
        Button closeButton = createWindowControlButton(MaterialDesign.MDI_CLOSE);

        minimizeButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.setIconified(true);
        });

        maximizeButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });

        closeButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        titleBar.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
        return titleBar;
    }

    private Button createWindowControlButton(Object iconCode) {
        Button button = new Button();
        button.setPrefSize(30, 30);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        FontIcon icon = new FontIcon();
        if (iconCode instanceof MaterialDesign) {
            icon.setIconCode((MaterialDesign) iconCode);
        } else if (iconCode instanceof FontAwesomeSolid) {
            icon.setIconCode((FontAwesomeSolid) iconCode);
        }
        icon.setIconSize(20);
        icon.setIconColor(Color.WHITE);

        button.setGraphic(icon);
        HBox.setMargin(button, new Insets(0, 10, 0, 0));
        return button;
    }

    private StackPane createCenterContent() {
        StackPane centerContent = new StackPane();

        // Create semi-transparent backdrop
        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6); -fx-background-radius: 10;");
        backdrop.setMaxWidth(800);
        backdrop.setMaxHeight(520);

        // Create title text
        Text titleText = new Text("cờ tướng");
        titleText.setFont(Font.font("System", 180));
        titleText.setFill(Color.BLACK);

        // Apply drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(15);
        dropShadow.setOffsetY(15);
        dropShadow.setColor(Color.WHITE);
        titleText.setEffect(dropShadow);

        // Create buttons container
        HBox buttonsBox = createButtonsBox();

        // Create main content container
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(titleText, buttonsBox);

        centerContent.getChildren().addAll(backdrop, mainContent);
        return centerContent;
    }

    private HBox createButtonsBox() {
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create the buttons
        Button instructionsButton = createMenuButton(MaterialDesign.MDI_BOOK_OPEN_VARIANT, "Hướng dẫn chơi");
        Button settingsButton = createMenuButton(FontAwesomeSolid.BOX, "Cài đặt");
        playButton = createMenuButton(FontAwesomeSolid.PLAY, "Chơi");
        Button historyButton = createMenuButton(MaterialDesign.MDI_HISTORY, "Lịch sử chơi");
        Button downloadButton = createMenuButton(FontAwesomeSolid.DOWNLOAD, "Tải");

        // Make the play button larger
        playButton.setPrefSize(100, 100);
        FontIcon playIcon = (FontIcon) playButton.getGraphic();
        playIcon.setIconSize(50);

        // Add action handlers
        instructionsButton.setOnAction(e -> handleInstructionsButton());
        settingsButton.setOnAction(e -> handleSettingsButton());
        playButton.setOnAction(e -> handlePlayButton());
        historyButton.setOnAction(e -> handleHistoryButton());
        downloadButton.setOnAction(e -> handleDownloadButton());

        buttonsBox.getChildren().addAll(instructionsButton, settingsButton, playButton, historyButton, downloadButton);
        return buttonsBox;
    }

    private Button createMenuButton(Object iconCode, String tooltipText) {
        Button button = new Button();
        button.setPrefSize(80, 80);
        button.setStyle("-fx-background-color: #333333; -fx-background-radius: 40; -fx-border-radius: 40;");

        FontIcon icon = new FontIcon();
        if (iconCode instanceof MaterialDesign) {
            icon.setIconCode((MaterialDesign) iconCode);
        } else if (iconCode instanceof FontAwesomeSolid) {
            icon.setIconCode((FontAwesomeSolid) iconCode);
        }
        icon.setIconSize(40);
        icon.setIconColor(Color.WHITE);

        button.setGraphic(icon);

        // Set tooltip
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("-fx-font-size: 16px;");
        button.setTooltip(tooltip);

        return button;
    }

    // Button event handlers
    private void handleInstructionsButton() {
        System.out.println("Instructions button clicked");
        // Add your implementation
    }

    private void handleSettingsButton() {
        System.out.println("Settings button clicked");
        // Add your implementation
    }

    private void handlePlayButton() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineGameScreen.fxml"));
            Parent root = loader.load();

            // Tạo scene mới
            Scene gameScene = new Scene(root, 1200, 720);

            // Lấy stage hiện tại từ nút hoặc bất kỳ node nào
            Stage stage = (Stage) ((Node) playButton).getScene().getWindow();  // `playButton` là ID của nút

            // Đặt scene mới
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng - Game");

        } catch (IOException e) {
            System.err.println("Lỗi chuyển sang màn hình chơi game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleHistoryButton() {
        System.out.println("History button clicked");
        // Add your implementation
    }

    private void handleDownloadButton() {
        System.out.println("Download button clicked");
        // Add your implementation
    }

    // Main method for testing


}