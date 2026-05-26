package com.example.h6launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DividerView extends View {
    private Paint paint;
    private float handleWidth = 40;
    private float handleHeight = 80;
    private OnDragListener listener;
    private boolean isDragging = false;

    public DividerView(Context context) {
        super(context);
        init();
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#e94560"));
        paint.setStyle(Paint.Style.FILL);
        setBackgroundColor(Color.parseColor("#2a2a4a"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        
        canvas.drawRect(
            centerX - handleWidth / 2,
            centerY - handleHeight / 2,
            centerX + handleWidth / 2,
            centerY + handleHeight / 2,
            paint
        );
        
        paint.setColor(Color.WHITE);
        float lineWidth = 24;
        float lineHeight = 4;
        float gap = 6;
        
        canvas.drawRect(
            centerX - lineWidth / 2,
            centerY - handleHeight / 4 - gap,
            centerX + lineWidth / 2,
            centerY - handleHeight / 4 - gap + lineHeight,
            paint
        );
        
        canvas.drawRect(
            centerX - lineWidth / 2,
            centerY - lineHeight / 2,
            centerX + lineWidth / 2,
            centerY + lineHeight / 2,
            paint
        );
        
        canvas.drawRect(
            centerX - lineWidth / 2,
            centerY + handleHeight / 4 + gap - lineHeight,
            centerX + lineWidth / 2,
            centerY + handleHeight / 4 + gap,
            paint
        );
        
        paint.setColor(Color.parseColor("#e94560"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging && listener != null) {
                    listener.onDrag(event.getRawX());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    public void setOnDragListener(OnDragListener listener) {
        this.listener = listener;
    }

    public interface OnDragListener {
        void onDrag(float rawX);
    }
}
