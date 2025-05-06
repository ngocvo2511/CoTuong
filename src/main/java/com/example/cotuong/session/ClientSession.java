package com.example.cotuong.session;

public class ClientSession {
    private static ClientSession instance;

    private String clientId;

    private ClientSession() {}

    public static ClientSession getInstance() {
        if (instance == null) {
            instance = new ClientSession();
        }
        return instance;
    }

    // Getters và setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
