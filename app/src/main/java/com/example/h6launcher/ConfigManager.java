package com.example.h6launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final String PREF_NAME = "h6launcher_config";
    
    private static final String KEY_DOCK_POSITION = "dock_position";
    private static final String KEY_SPLIT_MODE = "split_mode";
    private static final String KEY_CONFIG_1 = "config_1";
    private static final String KEY_CONFIG_2 = "config_2";
    private static final String KEY_CONFIG_3 = "config_3";
    private static final String KEY_ACTIVE_CONFIG = "active_config";
    
    private SharedPreferences preferences;

    public static final int DOCK_POSITION_LEFT = 0;
    public static final int DOCK_POSITION_BOTTOM = 1;
    
    public static final int SPLIT_MODE_2 = 2;
    public static final int SPLIT_MODE_3 = 3;

    public ConfigManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getDockPosition() {
        return preferences.getInt(KEY_DOCK_POSITION, DOCK_POSITION_LEFT);
    }

    public void setDockPosition(int position) {
        preferences.edit().putInt(KEY_DOCK_POSITION, position).apply();
    }

    public int getSplitMode() {
        return preferences.getInt(KEY_SPLIT_MODE, SPLIT_MODE_2);
    }

    public void setSplitMode(int mode) {
        preferences.edit().putInt(KEY_SPLIT_MODE, mode).apply();
    }

    public int getActiveConfigIndex() {
        return preferences.getInt(KEY_ACTIVE_CONFIG, 0);
    }

    public void setActiveConfigIndex(int index) {
        preferences.edit().putInt(KEY_ACTIVE_CONFIG, index).apply();
    }

    public SplitConfig getConfig(int index) {
        String key = getConfigKey(index);
        String json = preferences.getString(key, null);
        if (json != null) {
            try {
                return SplitConfig.fromJson(new JSONObject(json));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse config", e);
            }
        }
        return null;
    }

    public void saveConfig(int index, SplitConfig config) {
        String key = getConfigKey(index);
        preferences.edit().putString(key, config.toJson().toString()).apply();
    }

    private String getConfigKey(int index) {
        switch (index) {
            case 0: return KEY_CONFIG_1;
            case 1: return KEY_CONFIG_2;
            case 2: return KEY_CONFIG_3;
            default: return KEY_CONFIG_1;
        }
    }

    public boolean hasConfig(int index) {
        String key = getConfigKey(index);
        return preferences.contains(key);
    }
}
