package com.example.h6launcher;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SplitWindowView extends FrameLayout {
    private Context context;
    private WindowConfig config;
    private int windowIndex;
    private OnWindowClickListener listener;

    public SplitWindowView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SplitWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SplitWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.split_window, this, true);
        
        view.findViewById(R.id.window_content).setOnClickListener(v -> {
            if (config != null && !config.isEmpty()) {
                AppUtils.launchApp(context, config.getPackageName(), config.getClassName());
            } else if (listener != null) {
                listener.onWindowClick(windowIndex);
            }
        });
    }

    public void setWindowConfig(WindowConfig config) {
        this.config = config;
        
        ImageView iconView = findViewById(R.id.window_icon);
        TextView labelView = findViewById(R.id.window_label);
        TextView placeholderView = findViewById(R.id.window_placeholder);
        
        if (config != null && !config.isEmpty()) {
            iconView.setImageDrawable(getAppIcon(config.getPackageName()));
            labelView.setText(config.getLabel());
            placeholderView.setVisibility(GONE);
        } else {
            iconView.setImageResource(R.drawable.ic_add);
            labelView.setText("");
            placeholderView.setVisibility(VISIBLE);
        }
    }

    public WindowConfig getWindowConfig() {
        return config;
    }

    public void setWindowIndex(int index) {
        this.windowIndex = index;
    }

    public void setOnWindowClickListener(OnWindowClickListener listener) {
        this.listener = listener;
    }

    private android.graphics.drawable.Drawable getAppIcon(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (Exception e) {
            return context.getResources().getDrawable(R.drawable.ic_add);
        }
    }

    public interface OnWindowClickListener {
        void onWindowClick(int windowIndex);
    }
}
