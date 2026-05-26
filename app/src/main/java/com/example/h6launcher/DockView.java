package com.example.h6launcher;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class DockView extends LinearLayout {
    private Context context;
    private List<AppInfo> apps;
    private int position;

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
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.colorSurface));
    }

    public void setPosition(int position) {
        this.position = position;
        setOrientation(position == ConfigManager.DOCK_POSITION_LEFT ? VERTICAL : HORIZONTAL);
        updateLayout();
    }

    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
        updateLayout();
    }

    private void updateLayout() {
        removeAllViews();
        
        if (apps == null || apps.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        
        for (AppInfo app : apps) {
            View itemView = inflater.inflate(R.layout.dock_item, this, false);
            
            ImageView iconView = itemView.findViewById(R.id.dock_icon);
            TextView labelView = itemView.findViewById(R.id.dock_label);
            
            iconView.setImageDrawable(app.getIcon());
            labelView.setText(app.getLabel());
            
            itemView.setOnClickListener(v -> {
                AppUtils.launchApp(context, app.getPackageName(), app.getClassName());
            });
            
            addView(itemView);
        }
    }
}
