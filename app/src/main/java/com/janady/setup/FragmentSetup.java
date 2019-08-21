package com.janady.setup;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.ActivityGuideDeviceList;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.alarm.ServiceGuideLanAlarmNotification;
import com.example.funsdkdemo.alarm.ServiceGuidePushAlarmNotification;
import com.janady.HomeActivity;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunLoginListener;
import com.qmuiteam.qmui.arch.QMUIFragment;

public class FragmentSetup extends QMUIFragment implements OnFunLoginListener {
    private final int MESSAGE_ENTER_MAINMENU = 0x100;
    private final int MESSAGE_LOGIN_FUNISHED = 0x101;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jsetup, null);
        //startPushAlarmNotification();
        //startLanAlarmNotification();
        FunSupport.getInstance().registerOnFunLoginListener(this);
        return root;
    }

    private void startPushAlarmNotification() {
        Intent intent = new Intent(getContext(), ServiceGuidePushAlarmNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startService(intent);
    }

    private void startLanAlarmNotification() {
        Intent intent = new Intent(getContext(), ServiceGuideLanAlarmNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 最小延时2秒打开主界面
        mHandler.sendEmptyMessageDelayed(MESSAGE_ENTER_MAINMENU, 2000);
        if ( !FunSupport.getInstance().getAutoLogin()
                || !FunSupport.getInstance().loginByLastUser() ) {
            // 之前没有账号成功登录或者没有设置为自动登录,结束登录流程
            mHandler.sendEmptyMessage(MESSAGE_ENTER_MAINMENU);
        }
    }

    @Override
    public void onDestroy() {
        FunSupport.getInstance().removeOnFunLoginListener(this);
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_ENTER_MAINMENU:
                {
                    // login
                    startFragmentAndDestroyCurrent(new FragmentUserLogin());
                }
                break;
                case MESSAGE_LOGIN_FUNISHED:
                {
                    Intent intent = new Intent();

                    intent.setClass(getContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                    getActivity().finish();
                }
                break;
            }
        }
    };
    @Override
    public void onLoginSuccess() {
        if ( null != mHandler ) {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_FUNISHED);
        }
    }

    @Override
    public void onLoginFailed(Integer errCode) {

    }

    @Override
    public void onLogout() {

    }
}
