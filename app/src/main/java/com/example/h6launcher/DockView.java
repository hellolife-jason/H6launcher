package com.example.h6launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DockView extends LinearLayout {
    private Context context;
    private List<AppInfo> apps;
    private int position;
    private OnHomeToggleListener homeToggleListener;
    private boolean isHomeMode = true;
    private View scrollContainer;
    private LinearLayout container;
    private ImageButton toggleButton;

    public interface OnHomeToggleListener {
        void onHomeToggle(boolean isHomeMode);
    }

    public DockView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.colorSurface));
        setOrientation(VERTICAL);
        
        container = new LinearLayout(context);
        container.setOrientation(VERTICAL);
        
        scrollContainer = new ScrollView(context);
        ((ScrollView) scrollContainer).setHorizontalScrollBarEnabled(false);
        ((ScrollView) scrollContainer).setVerticalScrollBarEnabled(false);
        ((ScrollView) scrollContainer).addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        toggleButton = new ImageButton(context);
        toggleButton.setImageResource(R.drawable.ic_apps);
        toggleButton.setBackgroundResource(android.R.drawable.btn_default);
        toggleButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        toggleButton.setOnClickListener(v -> {
            isHomeMode = !isHomeMode;
            updateToggleButtonIcon();
            if (homeToggleListener != null) {
                homeToggleListener.onHomeToggle(isHomeMode);
            }
        });
        
        // 左侧布局：主页按钮在顶部
        addView(scrollContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        addView(toggleButton, new LayoutParams(LayoutParams.MATCH_PARENT, 64));
    }

    private void updateToggleButtonIcon() {
        if (isHomeMode) {
            toggleButton.setImageResource(R.drawable.ic_apps);
        } else {
            toggleButton.setImageResource(R.drawable.ic_home);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        
        removeAllViews();
        
        // 创建新的容器，避免视图层次问题
        container = new LinearLayout(context);
        
        if (position == ConfigManager.DOCK_POSITION_LEFT) {
            setOrientation(VERTICAL);
            
            scrollContainer = new ScrollView(context);
            ((ScrollView) scrollContainer).setHorizontalScrollBarEnabled(false);
            ((ScrollView) scrollContainer).setVerticalScrollBarEnabled(false);
            container.setOrientation(VERTICAL);
            
            // 左侧布局：主页按钮固定在左下角（先添加scrollContainer，再添加toggleButton）
            addView(scrollContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
            ((ScrollView) scrollContainer).addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            addView(toggleButton, new LayoutParams(LayoutParams.MATCH_PARENT, 64));
        } else {
            setOrientation(HORIZONTAL);
            
            scrollContainer = new HorizontalScrollView(context);
            ((HorizontalScrollView) scrollContainer).setHorizontalScrollBarEnabled(false);
            ((HorizontalScrollView) scrollContainer).setVerticalScrollBarEnabled(false);
            container = new LinearLayout(context);
            container.setOrientation(HORIZONTAL);
            ((HorizontalScrollView) scrollContainer).addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            
            // 底部布局：主页按钮位于左侧第一个
            addView(toggleButton, new LayoutParams(64, LayoutParams.MATCH_PARENT));
            addView(scrollContainer, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        }
        
        updateLayout();
    }

    public void setApps(List<AppInfo> apps) {
        this.apps = apps != null ? apps : new ArrayList<>();
        updateLayout();
    }

    public List<AppInfo> getApps() {
        return apps;
    }

    public void addApp(AppInfo app) {
        if (apps == null) {
            apps = new ArrayList<>();
        }
        if (!containsApp(app)) {
            apps.add(app);
            updateLayout();
        }
    }

    public void removeApp(String packageName) {
        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                if (apps.get(i).getPackageName().equals(packageName)) {
                    apps.remove(i);
                    break;
                }
            }
            updateLayout();
        }
    }

    private boolean containsApp(AppInfo app) {
        if (apps == null) {
            return false;
        }
        for (AppInfo a : apps) {
            if (a.getPackageName().equals(app.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void updateLayout() {
        container.removeAllViews();
        
        if (apps == null || apps.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        
        for (AppInfo app : apps) {
            View itemView = inflater.inflate(R.layout.dock_item, container, false);
            
            ImageView iconView = itemView.findViewById(R.id.dock_icon);
            TextView labelView = itemView.findViewById(R.id.dock_label);
            
            iconView.setImageDrawable(app.getIcon());
            labelView.setText(app.getLabel());
            
            itemView.setOnClickListener(v -> {
                AppUtils.launchApp(context, app.getPackageName(), app.getClassName());
            });
            
            container.addView(itemView);
        }
    }

    public void setOnHomeToggleListener(OnHomeToggleListener listener) {
        this.homeToggleListener = listener;
    }

    public boolean isHomeMode() {
        return isHomeMode;
    }

    public void setHomeMode(boolean homeMode) {
        this.isHomeMode = homeMode;
        updateToggleButtonIcon();
    }
}
