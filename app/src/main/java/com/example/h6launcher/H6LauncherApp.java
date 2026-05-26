package com.example.h6launcher;

import android.app.Application;

public class H6LauncherApp extends Application {
    private static H6LauncherApp instance;
    private ConfigManager configManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configManager = new ConfigManager(this);
    }

    public static H6LauncherApp getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
