package com.example.cotuong.network;

import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.session.ClientSession;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LobbyWebSocketClient extends WebSocketClient {

    private QuadConsumer<String, String, Integer, String> onRoomJoined;
    private TriConsumer<String, String, Integer> onRoomCreated;
    private PentaConsumer<String, String, String, Integer, String> onRandomMatchFound; // Thêm handler cho tìm trận ngẫu nhiên
    private Consumer<String> onError;
    private Consumer<String> onWaitingStatus; // Thêm handler cho trạng thái đang chờ

    private BiConsumer<String, String> onPlayerJoined;

    public void setOnPlayerJoined(BiConsumer<String, String> handler) {
        this.onPlayerJoined = handler;
    }

    public LobbyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public URI getServerUri() {
        return super.getURI(); // Lấy URI từ lớp cha WebSocketClient
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to lobby server");
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        String type = json.getString("type");

        switch (type) {
            case "RoomCreated":
                if (onRoomCreated != null) {
                    String roomName = json.getString("roomName");
                    String username = json.getString("username");
                    int time = json.getInt("time");
                    onRoomCreated.accept(roomName, username, time);
                }
                break;

            case "RoomJoined":
                if (onRoomJoined != null) {
                    String roomName = json.getString("roomName");
                    String username = json.getString("username");
                    int time = json.getInt("time");
                    String creatorUsername = json.getString("creatorUsername");
                    onRoomJoined.accept(roomName, username, time, creatorUsername);
                }
                break;

            case "RandomMatchFound": // Thêm xử lý sự kiện tìm thấy trận ngẫu nhiên
                if (onRandomMatchFound != null) {
                    String roomName = json.getString("roomName");
                    String username = json.getString("username");
                    String opponentUsername = json.getString("opponentUsername");
                    String color = json.getString("color");
                    int time = json.getInt("time");
                    onRandomMatchFound.accept(roomName, username, color, time, opponentUsername);
                }
                break;

            case "WaitingStatus": // Xử lý thông báo trạng thái đang chờ
                if (onWaitingStatus != null) {
                    String status = json.getString("status");
                    onWaitingStatus.accept(status);
                }
                break;

            case "PlayerJoined":
                if (onPlayerJoined != null) {
                    String creator = json.getString("creatorUsername");
                    String joiner = json.getString("joinerUsername");
                    onPlayerJoined.accept(creator, joiner);
                }
                break;

            case "Error":
                if (onError != null) {
                    String messageText = json.getString("message");
                    onError.accept(messageText);
                }
                // Add error notification handling
                ErrorNotificationHandler errorHandler = LobbyManager.getInstance().getErrorHandler();
                if (errorHandler != null) {
                    String messageText = json.getString("message");
                    errorHandler.showErrorNotification(messageText);
                }
                break;

            default:
                System.out.println("Unknown message: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Lobby connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    // ==== Set callback ====

    public void setOnRoomJoined(QuadConsumer<String, String, Integer, String> handler) {
        this.onRoomJoined = handler;
    }

    public void setOnRoomCreated(TriConsumer<String, String, Integer> handler) {
        this.onRoomCreated = handler;
    }

    public void setOnRandomMatchFound(PentaConsumer<String, String, String, Integer, String> handler) {
        this.onRandomMatchFound = handler;
    }

    public void setOnWaitingStatus(Consumer<String> handler) {
        this.onWaitingStatus = handler;
    }

    public void setOnError(Consumer<String> handler) {
        this.onError = handler;
    }

    // ==== Send actions ====

    public void createRoom(String roomName, String username, int time) {
        JSONObject json = new JSONObject();
        json.put("action", "createRoom");
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("roomName", roomName);
        json.put("username", username);
        json.put("time", time);
        send(json.toString());
    }

    public void joinRoom(String roomName, String username) {
        JSONObject json = new JSONObject();
        json.put("action", "joinRoom");
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("roomName", roomName);
        json.put("username", username);
        send(json.toString());
    }

    public void findRandomMatch(String username, int time) {
        JSONObject json = new JSONObject();
        json.put("action", "joinRandomMatch"); // Sử dụng phương thức có sẵn
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("username", username);
        json.put("time", time);
        send(json.toString());
        System.out.println("Đã gửi yêu cầu tìm trận ngẫu nhiên: " + json.toString());
    }

    public void cancelRandomMatchSearch(String username) {
        JSONObject json = new JSONObject();
        json.put("action", "cancelFindMatch"); // Sử dụng phương thức có sẵn
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("username", username);
        send(json.toString());
        System.out.println("Đã gửi yêu cầu hủy tìm trận: " + json.toString());
    }
}