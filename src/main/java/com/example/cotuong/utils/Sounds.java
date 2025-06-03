package com.example.cotuong.utils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class Sounds {
    private static MediaPlayer buttonClickSound;
    private static MediaPlayer gameOverSound;
    private static MediaPlayer moveSound;
    private static double volume = 0.5; // mặc định là 50%

    public static void setVolume(double vol) {
        volume = vol / 100.0;
    }

    public static double getVolume() {
        return volume * 100;
    }

    public static void playButtonClickSound() {
        buttonClickSound = createMediaPlayer("/com/example/cotuong/sounds/buttonClickSound.mp3");
        if (buttonClickSound != null) buttonClickSound.play();
    }

    public static void playGameOverSound() {
        gameOverSound = createMediaPlayer("/com/example/cotuong/sounds/gameOverSound.mp3");
        if (gameOverSound != null) gameOverSound.play();
    }

    public static void playMoveSound() {
        moveSound = createMediaPlayer("/com/example/cotuong/sounds/moveSound.mp3");
        if (moveSound != null) moveSound.play();
    }

    private static MediaPlayer createMediaPlayer(String resourcePath) {
        try {
            URL resource = Sounds.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("Không tìm thấy file âm thanh: " + resourcePath);
                return null;
            }
            Media media = new Media(resource.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(volume); // volume nên là 0.0 đến 1.0
            return player;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}