package com.example.cotuong.utils;

public class SettingsManager {
    private static final SettingsManager instance = new SettingsManager();

    private boolean timeLimitEnabled = true;
    private int timeLimit = 10;
    private int volume = 50;
    private boolean isPlayerFirst = true;

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        return instance;
    }

    public boolean isTimeLimitEnabled() { return timeLimitEnabled; }
    public void setTimeLimitEnabled(boolean enabled) { this.timeLimitEnabled = enabled; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public boolean isPlayerFirst() { return isPlayerFirst; }
    public void setPlayerFirst(boolean playerFirst) { this.isPlayerFirst = playerFirst; }
}
