package com.example.cotuong.saveservice;

import com.example.cotuong.chesslogic.HistoryMatchRecord;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveHistoryMatchManager {
    private static final File folder = new File("History_game");
    private static final Gson gson = new Gson();

    public static void save(HistoryMatchRecord historyMatchRecord) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        String fileName = "match_" + timestamp + ".xqi";
        File file = new File(folder,fileName);
        String json = gson.toJson(historyMatchRecord);
        byte[] encrypted = CryptoUtil.encrypt(json);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted);
        }
    }
    public static HistoryMatchRecord load(File file) throws Exception {
        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
        String json = CryptoUtil.decrypt(data);
        return gson.fromJson(json, HistoryMatchRecord.class);
    }
}
