package com.example.cotuong.network;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class LobbyManager {
    private static LobbyManager instance;
    private LobbyWebSocketClient client;
    private String currentIp;

    // Callback lưu tạm để gán lại khi tạo client mới
    private TriConsumer<String, String, Integer> onRoomCreated;
    private TriConsumer<String, String, Integer> onRoomJoined;
    private QuadConsumer<String, String, String, Integer> onRandomMatchFound;
    private Consumer<String> onWaitingStatus;
    private Consumer<String> onError;

    private LobbyManager() {
    }

    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }


    public void ensureClientInitialized(String ip) {
        if (client != null && client.getServerUri().getHost().equals(ip)) {
            return; // Đã kết nối đúng IP
        }

        try {
            URI uri = new URI("ws://" + ip + ":8080/ws/lobby"); // Sửa đúng cổng của bạn
            client = new LobbyWebSocketClient(uri);
            currentIp = ip;
        } catch (URISyntaxException e) {
            e.printStackTrace();
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