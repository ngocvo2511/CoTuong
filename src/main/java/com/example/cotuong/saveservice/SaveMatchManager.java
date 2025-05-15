package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.example.cotuong.chesslogic.pieces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;


public class SaveMatchManager {
    private static final RuntimeTypeAdapterFactory<GameState> factory = RuntimeTypeAdapterFactory
            .of(GameState.class, "type")
            .registerSubtype(GameState2P.class, "2P")
            .registerSubtype(GameStateAI.class, "AI");
    private static final RuntimeTypeAdapterFactory<Piece> pieceFactory = RuntimeTypeAdapterFactory
            .of(Piece.class, "pieceType")
            .registerSubtype(Soldier.class, "Soldier")
            .registerSubtype(General.class, "General")
            .registerSubtype(Horse.class, "Horse")
            .registerSubtype(Cannon.class, "Cannon")
            .registerSubtype(Advisor.class,"Advisor")
            .registerSubtype(Elephant.class,"Elephant")
            .registerSubtype(Chariot.class,"Chariot")
            ;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(factory)
            .registerTypeAdapterFactory(pieceFactory)
            .setPrettyPrinting()
            .create();
    public void save(GameState gameState, File file) throws Exception {
//        String json = gson.toJson(gameState);
//        byte[] encrypted = CryptoUtil.encrypt(json);
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            fos.write(encrypted);
//        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(gameState, writer);
        }
    }
    public GameState load(File file) throws Exception {
//        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
//        String json = CryptoUtil.decrypt(data);
//        return gson.fromJson(json, GameState.class);
        String json = Files.readString(file.toPath());
        return gson.fromJson(json, GameState.class);
    }

}

