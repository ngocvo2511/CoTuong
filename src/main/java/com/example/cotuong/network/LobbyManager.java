package com.example.cotuong.network;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.java_websocket.enums.ReadyState;


public class LobbyManager {
    private static LobbyManager instance;
    private LobbyWebSocketClient client;
    private String currentIp;
    private ErrorNotificationHandler errorHandler;

    private LobbyManager() {
    }

    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    public void setErrorHandler(ErrorNotificationHandler handler) {
        this.errorHandler = handler;
    }

    public ErrorNotificationHandler getErrorHandler() {
        return errorHandler;
    }

    public void ensureClientInitialized(String ip) {
        try {
            URI uri = new URI("ws://" + ip + ":8080/ws/lobby");
            // Tạo mới client mỗi lần kết nối
            client = new LobbyWebSocketClient(uri);
            currentIp = ip;
        } catch (URISyntaxException e) {
            if (errorHandler != null) {
                errorHandler.showErrorNotification("Địa chỉ IP không hợp lệ");
            }
        }
    }

    public LobbyWebSocketClient getClient() {
        return client;
    }

    public void connectClient() {
        if (client != null) {
            try {
                // Tạo mới client nếu client cũ đã đóng
                if (client.isClosed()) {
                    ensureClientInitialized(currentIp);
                }
                boolean connected = client.connectBlocking(2, java.util.concurrent.TimeUnit.SECONDS);
                if (!connected) {
                    if (errorHandler != null) {
                        errorHandler.showErrorNotification("Không thể kết nối đến server. Vui lòng kiểm tra lại địa chỉ IP.");
                    }
                }
            } catch (InterruptedException e) {
                if (errorHandler != null) {
                    errorHandler.showErrorNotification("Không thể kết nối đến server. Vui lòng kiểm tra lại địa chỉ IP.");
                }
            } catch (Exception e) {
                if (errorHandler != null) {

                }
            }
        }
    }
}