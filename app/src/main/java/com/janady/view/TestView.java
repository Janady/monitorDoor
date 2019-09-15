package com.janady.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.funsdkdemo.R;
import com.lib.funsdk.support.widget.FunVideoView;

public class TestView extends FunVideoView {
    private GestureListner gestureListner;
    public TestView(Context context) {
        this(context, null);
    }
    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private float gestureX = 0;
    private float gestureY = 0;
    private int gestureMoveLen = 0;
    private void initView(Context context) {
        //controlContainer = (ViewGroup) View.inflate(context, getLayoutId(), null);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (gestureMoveLen == 0)
                    gestureMoveLen = ViewConfiguration.get(v.getContext()).getScaledTouchSlop() + 30;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gestureX = x;
                        gestureY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        float deltaX = x - gestureX;
                        float deltaY = y - gestureY;
                        Log.d("zyk", " >> playRealMedia");
                        if (gestureListner == null) return false;
                        if (Math.abs(deltaX) > gestureMoveLen) {
                            if (deltaX > 0) gestureListner.onGestureRight();
                            else gestureListner.onGestureLeft();
                        }
                        if (Math.abs(deltaY) > gestureMoveLen) {
                            if (deltaY > 0) gestureListner.onGestureDown();
                            else gestureListner.onGestureUp();
                        }
                        break;

                }

                return true;
            }
        });
    }

    public void setGestureListner(GestureListner gestureListner) {
        this.gestureListner = gestureListner;
    }

    public interface GestureListner {
        void onGestureRight();
        void onGestureLeft();
        void onGestureUp();
        void onGestureDown();
    }
}
