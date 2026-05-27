package com.example.h6launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SplitScreenLayout extends HorizontalScrollView {
    private Context context;
    private LinearLayout container;
    private SplitWindowView[] windows;
    private DividerView[] dividers;
    private int splitMode = 2;
    private float[] ratios = {0.5f, 0.5f};
    private int totalWidth;
    private OnWindowClickListener windowClickListener;

    public SplitScreenLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SplitScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SplitScreenLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        addView(container, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setSplitMode(int mode) {
        this.splitMode = mode;
        ratios = new float[mode];
        float equalRatio = 1f / mode;
        for (int i = 0; i < mode; i++) {
            ratios[i] = equalRatio;
        }
        updateLayout();
    }

    public int getSplitMode() {
        return splitMode;
    }

    public void setRatios(float[] ratios) {
        this.ratios = ratios;
        updateLayout();
    }

    public float[] getRatios() {
        return ratios;
    }

    private void updateLayout() {
        container.removeAllViews();
        
        windows = new SplitWindowView[splitMode];
        dividers = new DividerView[splitMode - 1];
        
        for (int i = 0; i < splitMode; i++) {
            windows[i] = new SplitWindowView(context);
            windows[i].setWindowIndex(i);
            windows[i].setOnWindowClickListener(index -> {
                if (windowClickListener != null) {
                    windowClickListener.onWindowClick(index);
                }
            });
            
            LinearLayout.LayoutParams windowParams = new LinearLayout.LayoutParams(
                0, LayoutParams.MATCH_PARENT, ratios[i]
            );
            container.addView(windows[i], windowParams);
            
            if (i < splitMode - 1) {
                dividers[i] = new DividerView(context);
                dividers[i].setOnDragListener(this::handleDrag);
                
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    8, LayoutParams.MATCH_PARENT
                );
                container.addView(dividers[i], dividerParams);
            }
        }
    }

    public void setWindowConfig(int index, WindowConfig config) {
        if (windows != null && index >= 0 && index < windows.length) {
            windows[index].setWindowConfig(config);
        }
    }

    public WindowConfig getWindowConfig(int index) {
        if (windows != null && index >= 0 && index < windows.length) {
            return windows[index].getWindowConfig();
        }
        return null;
    }

    public View getWindowView(int index) {
        if (windows != null && index >= 0 && index < windows.length) {
            return windows[index];
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    }

    private void handleDrag(float rawX) {
        int dividerIndex = findDraggingDivider();
        if (dividerIndex < 0) return;
        
        int[] location = new int[2];
        getLocationOnScreen(location);
        float relativeX = rawX - location[0];
        
        float newRatio = relativeX / totalWidth;
        
        float minRatio = 0.15f;
        float maxRatio = 0.85f;
        
        float currentSum = 0;
        for (int i = 0; i <= dividerIndex; i++) {
            currentSum += ratios[i];
        }
        
        float leftPortion = Math.max(minRatio, Math.min(maxRatio, newRatio));
        float rightPortion = 1 - leftPortion;
        
        float leftSum = 0;
        for (int i = 0; i <= dividerIndex; i++) {
            leftSum += ratios[i];
        }
        float rightSum = 1 - leftSum;
        
        for (int i = 0; i <= dividerIndex; i++) {
            ratios[i] = (ratios[i] / leftSum) * leftPortion;
        }
        for (int i = dividerIndex + 1; i < ratios.length; i++) {
            ratios[i] = (ratios[i] / rightSum) * rightPortion;
        }
        
        updateLayout();
    }

    private int findDraggingDivider() {
        for (int i = 0; i < dividers.length; i++) {
            if (isDividerDragging(dividers[i])) {
                return i;
            }
        }
        return 0;
    }

    private boolean isDividerDragging(DividerView divider) {
        return true;
    }

    public void setOnWindowClickListener(OnWindowClickListener listener) {
        this.windowClickListener = listener;
    }

    public interface OnWindowClickListener {
        void onWindowClick(int windowIndex);
    }
}
