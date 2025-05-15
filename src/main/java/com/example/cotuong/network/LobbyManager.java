package com.example.cotuong.network;

import java.net.URI;

public class LobbyManager {
    private static LobbyManager instance;
    private LobbyWebSocketClient client;

    private LobbyManager() {
    }

    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    public void ensureClientInitialized() {
        if (client == null) {
            try {
                // Kết nối tới endpoint lobby
                URI uri = new URI("ws://192.168.112.203:8080/ws/lobby");
                client = new LobbyWebSocketClient(uri);
                // KHÔNG connect ở đây
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LobbyWebSocketClient getClient() {
        return client;
    }

    public void connectClient() {
        if (client != null && !client.isOpen()) {
            try {
                client.connectBlocking(); // hoặc connect() nếu không cần chặn
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}