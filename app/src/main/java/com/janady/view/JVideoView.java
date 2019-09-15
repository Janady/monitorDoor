package com.janady.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.funsdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class JVideoView extends JVideoViewHelp {
    protected ImageView coverImageView;//封面
    protected ViewGroup bottomContainer;//底部栏
    protected ViewGroup topContainer;//顶部栏
    protected ViewGroup loadingContainer;//初始化
    protected ViewGroup errorContainer;//出错了显示的 重试
    protected ViewGroup bufferingContainer;//缓冲

    protected List<View> changeViews;//根据状态隐藏显示的view集合

    public JVideoView(Context context) {
        this(context, null);
    }

    public JVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public JVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setUIWithStateAndMode(STAT_STOPPED);
    }

    @Override
    protected void gestureLeft() {

    }

    @Override
    protected void gestureRight() {

    }

    @Override
    protected void gestureUp() {

    }

    @Override
    protected void gestureDown() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.jvideo_view;
    }

    protected void initView() {
        topContainer = (ViewGroup) findViewById(R.id.layout_top);
        bottomContainer = (ViewGroup) findViewById(R.id.layout_bottom);

        bufferingContainer = (ViewGroup) findViewById(R.id.buffering_container);
        loadingContainer = (ViewGroup) findViewById(R.id.loading_container);
        errorContainer = (ViewGroup) findViewById(R.id.error_container);

        coverImageView = (ImageView) findViewById(R.id.cover);

        changeViews = new ArrayList<>();

        //会根据播放器状态而改变的view加进去
        changeViews.add(topContainer);
        changeViews.add(bottomContainer);
        changeViews.add(loadingContainer);
        changeViews.add(errorContainer);
        changeViews.add(coverImageView);
        changeViews.add(startButton);
        changeViews.add(progressBar);
    }

    //根据播放状态设置ui显示/隐藏,包括控制UI
    @Override
    protected void changeUiWithStateAndMode(int status) {
        Log.d("zyk", "changeUiWithStateAndMode >> "+status);
        switch (status) {
            case STAT_STOPPED:
                showChangeViews(coverImageView, startButton);//普通状态显示封面和播放按钮
                break;
            case STAT_PLAYING:
            case STAT_PAUSED:
                showChangeViews(startButton,
                        bottomContainer);
                break;
        }
        updateViewImage(status);
        floatCloseView.setVisibility(View.INVISIBLE);
        floatBackView.setVisibility(View.INVISIBLE);
    }

    //根据播放器状态要显示的view
    protected void showChangeViews(View... views) {
        for (View v : changeViews)
            if (v != null)
                v.setVisibility(INVISIBLE);
        for (View v : views)
            if (v != null)
                v.setVisibility(VISIBLE);
    }

    protected void updateViewImage(int status) {
        startButton.setImageResource(status == STAT_PLAYING ?
                R.drawable.jc_click_pause_selector : R.drawable.jc_click_play_selector);
        fullscreenButton.setImageResource(R.drawable.jc_enlarge);
    }
    public ImageView getCoverImageView() {
        return coverImageView;
    }
    @Override
    protected void dismissControlView(int status) {
        bottomContainer.setVisibility(View.INVISIBLE);
        topContainer.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        floatCloseView.setVisibility(View.INVISIBLE);
        floatBackView.setVisibility(View.INVISIBLE);
    }
    //==============================================================================================
    //--------------------- 以下为辅助功能,不需要不用写实现--------------------------------------------
    //==============================================================================================
}
