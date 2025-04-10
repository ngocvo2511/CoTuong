package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;


public class SaveMatchManager {
    private static final RuntimeTypeAdapterFactory<GameState> factory = RuntimeTypeAdapterFactory
            .of(GameState.class, "type")
            .registerSubtype(GameState2P.class, "2P")
            .registerSubtype(GameStateAI.class, "AI");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(factory)
            .setPrettyPrinting()
            .create();
//    public void save(GameState gameState){
//
//
//    }
//    public GameState load(String filePath){
//
//    }

}

