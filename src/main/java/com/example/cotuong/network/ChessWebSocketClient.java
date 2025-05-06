package com.example.cotuong.network;

import com.example.cotuong.chesslogic.Move;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.Position;
import com.example.cotuong.controller.OnlineGameController;
import com.example.cotuong.session.ClientSession;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import org.json.JSONObject;

public class ChessWebSocketClient extends WebSocketClient {

    private OnlineGameController controller;

    public ChessWebSocketClient(URI serverUri, OnlineGameController controller) {
        super(serverUri);
        this.controller = controller;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        String type = json.getString("type");

        switch (type) {
            case "MoveTo":
                System.out.println("Nhận nước đi từ server: " + message);
                int x1 = json.getInt("x1");
                int y1 = json.getInt("y1");
                int x2 = json.getInt("x2");
                int y2 = json.getInt("y2");

                if(controller.getColor() == controller.getGameState().currentPlayer) {
                    controller.handleMove(new Move(new Position(x1, y1), new Position(x2, y2)));
                    System.out.println("moveto1");

                }
                else{
                    System.out.println("moveto2");
                    controller.handleMove(new Move(new Position(9 - x1, 8 - y1), new Position(9 - x2, 8 - y2)));
                }

                break;

            case "PlayerJoined":
                System.out.println("Người chơi đã tham gia: " + json.getString("joinerUsername"));
                break;

            case "RoomJoined":
                System.out.println("Tham gia phòng thành công: " + json.getString("roomName"));
                Player color = Player.valueOf(json.getString("color"));
                controller.setPlayerColor(color);
                break;

            case "Error":
                System.out.println("Lỗi: " + json.getString("message"));
                break;

            default:
                System.out.println("Không rõ message: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    // Gửi yêu cầu join phòng
    public void joinRoom(String roomName, String username) {
        JSONObject json = new JSONObject();
        json.put("action", "joinRoom");
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("roomName", roomName);
        json.put("username", username);
        send(json.toString());
    }

    // Gửi nước đi
    public void makeMove(int x1, int y1, int x2, int y2) {
        JSONObject json = new JSONObject();
        json.put("action", "makeMove");
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("x1", x1);
        json.put("y1", y1);
        json.put("x2", x2);
        json.put("y2", y2);
        send(json.toString());
        System.out.println("makemove");
    }

    public void registerGameSession(String roomName){
        JSONObject json = new JSONObject();
        json.put("action", "registerGameSession");
        String clientId = ClientSession.getInstance().getClientId();
        json.put("clientId", clientId);
        json.put("roomName", roomName);
        send(json.toString());
    }
}