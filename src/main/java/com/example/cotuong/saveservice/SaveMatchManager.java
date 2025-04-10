package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SaveMatchManager {
    private static final RuntimeTypeAdapterFactory<GameState> factory = RuntimeTypeAdapterFactory
            .of(GameState.class, "type")
            .registerSubtype(GameState2P.class, "2P")
            .registerSubtype(GameStateAI.class, "AI");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(factory)
            .setPrettyPrinting()
            .create();
    public void save(GameState gameState, String filePath) throws Exception {
        String json = gson.toJson(gameState);
        File file = new File(filePath);
        byte[] encrypted = CryptoUtil.encrypt(json);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted);
        }
    }
    public GameState load(String filePath) throws Exception {
        File file = new File(filePath);
        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
        String json = CryptoUtil.decrypt(data);
        return gson.fromJson(json, GameState.class);
    }

}

