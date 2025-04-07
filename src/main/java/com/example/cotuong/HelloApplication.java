package com.example.cotuong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private GridPane mainWindowGrid;
    private Pane view;

    @Override
    public void start(Stage primaryStage) {
        // Thiết lập thuộc tính cho cửa sổ
        primaryStage.setTitle("Cờ Tướng");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(720);
        try {
            String imagePath = "/com/example/cotuong/images/icon.png";
            Image icon = new Image(getClass().getResourceAsStream(imagePath));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Lỗi tải hình ảnh (phương pháp 1): " + e.getMessage());
        }
        primaryStage.centerOnScreen();

        // Tạo grid chính
        mainWindowGrid = new GridPane();

        // Tạo view container
        view = new Pane();
        mainWindowGrid.getChildren().add(view);



        MainMenu mainMenu = new MainMenu(primaryStage);
        Scene scene = new Scene(mainMenu);
        scene.setFill(Color.TRANSPARENT);

        // Tạo scene với background trong suốt


        // Tạo border với góc bo tròn
        Border border = new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(30),
                BorderWidths.DEFAULT));

        mainWindowGrid.setBorder(border);
        mainWindowGrid.setStyle("-fx-background-color: white; -fx-background-radius: 30;");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}