package com.janady.view;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.lib.funsdk.support.widget.FunVideoView;
import com.janady.view.HandleTouchEvent;

public abstract class JVideoViewHelp extends FunVideoView implements View.OnClickListener {

    public int controlUIHideTime = 2500;//控制UI隐藏时间
    public boolean isDoneShowControlUI = false;//初始化完成/视频播放完成后,是否显示控制UI
    public boolean isWindowGesture = false;//是否非全屏下也可以手势调节进度

    protected ViewGroup controlContainer;//控制ui容器
    protected FrameLayout videoView;
    //提供辅助的控件
    protected TextView titleTextView;//标题
    protected TextView definitionTextView;//清晰度
    protected ImageView startButton, startButton2;//播放按钮
    protected SeekBar seekBar;//拖动条
    protected TextView currentTimeTextView, totalTimeTextView;//播放时间/视频长度
    protected ImageView fullscreenButton;//全屏按钮
    protected ProgressBar progressBar;//第二进度条
    protected View backView, floatCloseView, floatBackView;//返回
    protected final int progressMax = 1000;

    protected boolean isShowControlView;
    protected Handler mHandler;

    public JVideoViewHelp(Context context) {
        this(context, null);
    }
    public JVideoViewHelp(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public JVideoViewHelp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHelpView(context);
    }

    private float gestureX = 0;
    private float gestureY = 0;
    private int gestureMoveLen = 0;
    protected void initHelpView(Context context) {
        videoView = new FrameLayout(context);
        videoView.setId(R.id.qs_videoview);
        addView(videoView, new FrameLayout.LayoutParams(-1, -1));

        mHandler = new Handler(Looper.getMainLooper());

        controlContainer = (ViewGroup) View.inflate(context, getLayoutId(), null);
        videoView.addView(controlContainer, new FrameLayout.LayoutParams(-1, -1));
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
                        if (Math.abs(deltaX) > gestureMoveLen) {
                            if (deltaX > 0) gestureRight();
                            else gestureLeft();
                        }
                        if (Math.abs(deltaY) > gestureMoveLen) {
                            if (deltaY > 0) gestureDown();
                            else gestureUp();
                        }
                        break;

                }

                return true;
            }
        });

        titleTextView = (TextView) findViewById(R.id.help_title);
        startButton = (ImageView) findViewById(R.id.help_start);
        startButton2 = (ImageView) findViewById(R.id.help_start2);
        fullscreenButton = (ImageView) findViewById(R.id.help_fullscreen);
        progressBar = (ProgressBar) findViewById(R.id.help_progress);
        currentTimeTextView = (TextView) findViewById(R.id.help_current);
        totalTimeTextView = (TextView) findViewById(R.id.help_total);
        backView = findViewById(R.id.help_back);
        floatCloseView = findViewById(R.id.help_float_close);
        floatBackView = findViewById(R.id.help_float_goback);
        definitionTextView = findViewById(R.id.help_definition);
        if (progressBar != null)
            progressBar.setMax(progressMax);

        ///videoView.setVisibility(INVISIBLE);
        setClick(videoView, startButton, startButton2, fullscreenButton, backView, floatCloseView, floatBackView, definitionTextView);
    }

    //-----------ui监听start-----------------
    private void setClick(View... vs) {
        for (View v : vs) {
            if (v != null)
                v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        //播放按钮
        if (i == R.id.help_start || i == R.id.help_start2) {
            clickPlay();
        }
        //全屏按钮
        if (i == R.id.help_fullscreen) {
            //clickFull();
        }
        //退出按钮
        if (i == R.id.help_back) {
//            if (currentMode != MODE_WINDOW_NORMAL)
//                quitWindowFullscreen();
//            else
//                Util.scanForActivity(getContext()).finish();
        }
        //点击空白处
        if (view == videoView) {
            if (currentState == STAT_STOPPED)
                clickPlay();
            else if (currentState == STAT_PLAYING ||
                    currentState == STAT_PAUSED) {
                isShowControlView = !isShowControlView;
                setUIWithStateAndMode(currentState);
            }
        }

        //清晰度按钮
        if (i == R.id.help_definition) {
        }

        //点击事件
    }

    //-----------设置UI数据start-----------------
    @Override//覆盖父类监听播放器状态//todo 父类这个方法后面考虑改成final,子类从监听里获取状态,事件
    protected void setUIWithStateAndMode(final int status) {
        cancelDismissControlViewTimer();
        cancelProgressTimer();
        switch (status) {
            case STAT_STOPPED:
                isShowControlView = false;
                break;
            case STAT_PLAYING:
                startDismissControlViewTimer();
                startProgressTimer();
                break;
            case STAT_PAUSED:
                startProgressTimer();
                break;
        }
        changeUiWithStateAndMode(status);
        if ((status == STAT_PLAYING || status == STAT_PAUSED)
                & !isShowControlView)
            dismissControlView(status);
        //调用父类...
        super.setUIWithStateAndMode(status);
        //状态改变监听回调永远放在最后
    }

    //-----------定时任务更新进度start-----------------
    protected void startProgressTimer() {
        cancelProgressTimer();
        mHandler.post(updateProgress);
    }

    protected void cancelProgressTimer() {
        mHandler.removeCallbacks(updateProgress);
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(updateProgress, 500);
            //setProgressAndText();
        }
    };
    //-----------定时任务更新进度end-----------------
    //-----------定时任务隐藏控制栏start-----------------
    protected void startDismissControlViewTimer() {
        startDismissControlViewTimer(controlUIHideTime);
    }

    protected void startDismissControlViewTimer(int delayed) {
        cancelDismissControlViewTimer();
        mHandler.postDelayed(dismissControlViewTimerRunnable, delayed);
    }

    protected void cancelDismissControlViewTimer() {
        mHandler.removeCallbacks(dismissControlViewTimerRunnable);
    }

    private Runnable dismissControlViewTimerRunnable = new Runnable() {
        @Override
        public void run() {
            isShowControlView = false;
            dismissControlView(currentState);
        }
    };


    /**
     * =========================================
     * -------gesture的方法------------
     * ========================================
     */
    protected abstract void gestureLeft();
    protected abstract void gestureRight();
    protected abstract void gestureUp();
    protected abstract void gestureDown();

    /**
     * =========================================
     * -------子类需要实现的重要的方法------------
     * ========================================
     */
    protected abstract int getLayoutId();

    protected abstract void changeUiWithStateAndMode(int status);//根据状态设置ui显示/隐藏

    protected abstract void dismissControlView(int status);//播放时定时隐藏的控制ui
}
