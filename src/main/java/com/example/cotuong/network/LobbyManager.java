package com.example.cotuong.network;

import java.net.URI;

public class LobbyManager {
    private static LobbyManager instance;
    private LobbyWebSocketClient client;

    private LobbyManager() {
        try {
            URI uri = new URI("ws://localhost:8080/ws/chess");
            client = new LobbyWebSocketClient(uri);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    public LobbyWebSocketClient getClient() {
        return client;
    }
}
