package com.example.cotuong.network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LobbyWebSocketClient extends WebSocketClient {

    private BiConsumer<String, String> onRoomJoined;
    private TriConsumer<String, String, Integer> onRoomCreated;
    private Consumer<String> onError;

    public LobbyWebSocketClient(URI serverUri) {
        super(serverUri);
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
                    onRoomJoined.accept(roomName, username);
                }
                break;

            case "Error":
                if (onError != null) {
                    String messageText = json.getString("message");
                    onError.accept(messageText);
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

    public void setOnRoomJoined(BiConsumer<String, String> handler) {
        this.onRoomJoined = handler;
    }

    public void setOnRoomCreated(TriConsumer<String, String, Integer> handler) {
        this.onRoomCreated = handler;
    }

    public void setOnError(Consumer<String> handler) {
        this.onError = handler;
    }

    // ==== Send actions ====

    public void createRoom(String roomName, String username, int time) {
        JSONObject json = new JSONObject();
        json.put("action", "createRoom");
        json.put("roomName", roomName);
        json.put("username", username);
        json.put("time", time);
        send(json.toString());
    }

    public void joinRoom(String roomName, String username) {
        JSONObject json = new JSONObject();
        json.put("action", "joinRoom");
        json.put("roomName", roomName);
        json.put("username", username);
        send(json.toString());
    }

    public void joinRandomMatch(String username, int time) {
        JSONObject json = new JSONObject();
        json.put("action", "joinRandomMatch");
        json.put("username", username);
        json.put("time", time);
        send(json.toString());
    }

    public void cancelFindMatch() {
        JSONObject json = new JSONObject();
        json.put("action", "cancelFindMatch");
        send(json.toString());
    }
}
