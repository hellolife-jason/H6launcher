package com.example.h6launcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplitConfig {
    private int splitMode;
    private List<WindowConfig> windows;
    private float[] ratios;

    public SplitConfig() {
        this.splitMode = 2;
        this.windows = new ArrayList<>();
        this.ratios = new float[]{0.5f, 0.5f};
    }

    public SplitConfig(int splitMode, List<WindowConfig> windows, float[] ratios) {
        this.splitMode = splitMode;
        this.windows = windows;
        this.ratios = ratios;
    }

    public int getSplitMode() {
        return splitMode;
    }

    public void setSplitMode(int splitMode) {
        this.splitMode = splitMode;
    }

    public List<WindowConfig> getWindows() {
        return windows;
    }

    public void setWindows(List<WindowConfig> windows) {
        this.windows = windows;
    }

    public float[] getRatios() {
        return ratios;
    }

    public void setRatios(float[] ratios) {
        this.ratios = ratios;
    }

    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("splitMode", splitMode);
            
            JSONArray windowsArray = new JSONArray();
            for (WindowConfig window : windows) {
                windowsArray.put(window.toJson());
            }
            obj.put("windows", windowsArray);
            
            JSONArray ratiosArray = new JSONArray();
            for (float ratio : ratios) {
                ratiosArray.put(ratio);
            }
            obj.put("ratios", ratiosArray);
            
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static SplitConfig fromJson(JSONObject obj) throws JSONException {
        SplitConfig config = new SplitConfig();
        config.splitMode = obj.getInt("splitMode");
        
        JSONArray windowsArray = obj.getJSONArray("windows");
        List<WindowConfig> windows = new ArrayList<>();
        for (int i = 0; i < windowsArray.length(); i++) {
            windows.add(WindowConfig.fromJson(windowsArray.getJSONObject(i)));
        }
        config.windows = windows;
        
        JSONArray ratiosArray = obj.getJSONArray("ratios");
        float[] ratios = new float[ratiosArray.length()];
        for (int i = 0; i < ratiosArray.length(); i++) {
            ratios[i] = (float) ratiosArray.getDouble(i);
        }
        config.ratios = ratios;
        
        return config;
    }
}
