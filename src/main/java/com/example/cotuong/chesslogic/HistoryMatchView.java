package com.example.cotuong.chesslogic;

import java.io.File;

public class HistoryMatchView {
    public final File file;
    public final String display;

    public HistoryMatchView(File file, String display) {
        this.file = file;
        this.display = display;
    }
    @Override
    public String toString() {
        return display; // để ListView hiển thị đẹp
    }
}
