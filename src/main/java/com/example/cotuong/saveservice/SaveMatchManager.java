package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.gamestate.GameState2P;
import com.example.cotuong.chesslogic.gamestate.GameStateAI;
import com.google.gson.Gson;


public class SaveMatchManager {
//    RuntimeTypeAdapterFactory<GameState> factory = RuntimeTypeAdapterFactory
//            .of(GameState.class, "type")  // "type" sẽ là trường lưu tên class con
//            .registerSubtype(GameState2P.class, "2P")
//            .registerSubtype(GameStateAI.class, "AI");
    private static final Gson gson = new Gson();
    public void save(GameState gameState){


    }
    public void load(){

    }
//    public GameStateForLoad fromSave(GameStateForSave gameStateForSave){
//
//    }
//    public GameStateForSave toSave(GameState gameState){
//
//    }
}
