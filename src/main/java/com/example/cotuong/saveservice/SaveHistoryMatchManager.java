package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.HistoryMatchRecord;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.pieces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveHistoryMatchManager {
    private static final File folder = new File("History_game");
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
            .registerTypeAdapterFactory(pieceFactory)
            .setPrettyPrinting()
            .create();
    public static void save(HistoryMatchRecord historyMatchRecord) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        String fileName = "match_" + timestamp + ".hst";
        File file = new File(folder,fileName);
        String json = gson.toJson(historyMatchRecord);
        byte[] encrypted = CryptoUtil.encrypt(json);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted);
        }
//        try (FileWriter writer = new FileWriter(file)) {
//            gson.toJson(historyMatchRecord, writer);
//        }
    }
    public static HistoryMatchRecord load(File file) throws Exception {
        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
        String json = CryptoUtil.decrypt(data);
        return gson.fromJson(json, HistoryMatchRecord.class);
    }
}
