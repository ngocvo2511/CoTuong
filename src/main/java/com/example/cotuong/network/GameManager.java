package com.example.cotuong.network;

import com.example.cotuong.controller.OnlineGameController;
import java.net.URI;

public class GameManager {
    private static GameManager instance;
    private ChessWebSocketClient client;

    private GameManager() {
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public ChessWebSocketClient createClient(OnlineGameController controller) {
        try {
            // Kết nối tới endpoint game
            URI uri = new URI("ws://localhost:8080/ws/game");
            client = new ChessWebSocketClient(uri, controller);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ChessWebSocketClient getClient() {
        return client;
    }
}