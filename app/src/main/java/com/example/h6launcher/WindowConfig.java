package com.example.h6launcher;

import org.json.JSONException;
import org.json.JSONObject;

public class WindowConfig {
    private String packageName;
    private String className;
    private String label;

    public WindowConfig() {
        this.packageName = "";
        this.className = "";
        this.label = "";
    }

    public WindowConfig(String packageName, String className, String label) {
        this.packageName = packageName;
        this.className = className;
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEmpty() {
        return packageName == null || packageName.isEmpty();
    }

    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("packageName", packageName);
            obj.put("className", className);
            obj.put("label", label);
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static WindowConfig fromJson(JSONObject obj) throws JSONException {
        WindowConfig config = new WindowConfig();
        config.packageName = obj.optString("packageName", "");
        config.className = obj.optString("className", "");
        config.label = obj.optString("label", "");
        return config;
    }
}
