package com.example.h6launcher;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String packageName;
    private String className;
    private String label;
    private Drawable icon;

    public AppInfo() {
    }

    public AppInfo(String packageName, String className, String label, Drawable icon) {
        this.packageName = packageName;
        this.className = className;
        this.label = label;
        this.icon = icon;
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

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
