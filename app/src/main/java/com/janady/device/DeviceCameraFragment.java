package com.janady.device;

import android.media.MediaPlayer;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.janady.view.TestView;
import com.lib.EPTZCMD;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceCaptureListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.OPPTZPreset;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunStreamType;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class DeviceCameraFragment extends JBaseFragment
        implements OnFunDeviceOptListener,
        OnFunDeviceCaptureListener,
        FunVideoView.GestureListner,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener {
    private QMUITopBarLayout mTopBar;
    private FunDevice mFunDevice;
    private FunVideoView mFunVideoView;
    private TextView mTextVideoStat = null;

    public void setFunDevice(FunDevice mFunDevice) {
        this.mFunDevice = mFunDevice;
    }

    private float gestureX = 0;
    private float gestureY = 0;
    private int gestureMoveLen = 0;
    @Override
    protected View onCreateView() {
        Log.d("zyk", " >> onCreateView!");
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jdevice_camera_layout, null);
        mTopBar = root.findViewById(R.id.topbar);
        mFunVideoView = root.findViewById(R.id.funVideoView);
        mFunVideoView.setGestureListner(this);
        mFunVideoView.setOnPreparedListener(this);
        mFunVideoView.setOnErrorListener(this);
        mFunVideoView.setOnInfoListener(this);
        mTextVideoStat = (TextView) root.findViewById(R.id.textVideoStat);
        initTopBar();
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        initVideoView();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(mFunDevice == null ? "" : mFunDevice.getDevName());
    }

    private void initVideoView() {
        if (mFunDevice == null) return;
        if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
            FunSupport.getInstance().requestDeviceLogin(mFunDevice);
        } else {
            FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
        }
    }

    private void requestSystemInfo(FunDevice funDevice) {
        FunSupport.getInstance().requestDeviceConfig(funDevice, SystemInfo.CONFIG_NAME);
    }

    public void onDeviceSaveNativePws(FunDevice funDevice, String password) {
        FunDevicePassword.getInstance().saveDevicePassword(funDevice.getDevSn(),
                password);
        // 库函数方式本地保存密码
        if (FunSupport.getInstance().getSaveNativePassword()) {
            FunSDK.DevSetLocalPwd(funDevice.getDevSn(), "admin", password);
            // 如果设置了使用本地保存密码，则将密码保存到本地文件
        }
    }
    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog(final FunDevice funDevice) {
        DialogInputPasswd inputDialog = new DialogInputPasswd(getContext(),
                getResources().getString(R.string.device_login_input_password), "", R.string.common_confirm,
                R.string.common_cancel) {

            @Override
            public boolean confirm(String editText) {
                // 重新以新的密码登录
                if (null != funDevice) {
                    onDeviceSaveNativePws(funDevice, editText);

                    // 重新登录
                    FunSupport.getInstance().requestDeviceLogin(funDevice);
                }
                return super.confirm(editText);
            }

            @Override
            public void cancel() {
                super.cancel();

                // 取消输入密码,直接退出

            }

        };

        inputDialog.show();
    }

    private void playRealMedia() {
        Log.d("zyk", " >> playRealMedia");
        mTextVideoStat.setText(R.string.media_player_opening);
        mTextVideoStat.setVisibility(View.VISIBLE);
        if (mFunDevice.isRemote) {
            mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
        }

        // 打开声音
        mFunVideoView.setMediaSound(true);
    }

    private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
        Log.d("zyk", " >> onContrlPTZ1-"+nPTZCommand);
        FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
                nPTZCommand, bStop, mFunDevice.CurrChannel);
    }
    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {
        if (funDevice!=null && mFunDevice!=null && funDevice.getId()==mFunDevice.getId()) requestSystemInfo(mFunDevice);
    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
        Log.d("zyk", funDevice.getDevName() + " >> login failed:" + errCode);
        if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
            showInputPasswordDialog(funDevice);
        }
    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        Log.d("zyk", funDevice.getDevName() + " >> Config success!");
        if (SystemInfo.CONFIG_NAME.equals(configName)) {
            if (funDevice.channel == null) {
                FunSupport.getInstance().requestGetDevChnName(funDevice);
                requestSystemInfo(funDevice);
                return;
            }
            if (mFunVideoView != null && funDevice != null && funDevice.getId() == mFunDevice.getId()) playRealMedia();
            if (funDevice.isSupportPTZ()) {
                //requestPTZPreset();
            }
        }
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

        if (errCode == -11406) {
            funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
        }
    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

    }

    @Override
    public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

    }

    @Override
    public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }

    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {

    }

    @Override
    public void onGestureRight() {
        onContrlPTZ1(EPTZCMD.PAN_RIGHT, false);
    }

    @Override
    public void onGestureLeft() {
        onContrlPTZ1(EPTZCMD.PAN_LEFT, false);
    }

    @Override
    public void onGestureUp() {
        onContrlPTZ1(EPTZCMD.TILT_UP, false);
    }

    @Override
    public void onGestureDown() {
        onContrlPTZ1(EPTZCMD.TILT_DOWN, false);
    }@Override
    public void onGestureStop() {
        onContrlPTZ1(EPTZCMD.TILT_DOWN, true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        showToast(getResources().getString(R.string.media_play_error)
                + " : "
                + FunError.getErrorStr(extra));

        if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
                || FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
            // 不支持高清码流,设置为标清码流重新播放
            if (null != mFunVideoView) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
                playRealMedia();
            }
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mTextVideoStat.setText(R.string.media_player_buffering);
            mTextVideoStat.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mTextVideoStat.setVisibility(View.GONE);
            tryToCapture();
        }
        return true;
    }
    /**
     * 视频截图,并延时一会提示截图对话框
     */
    private void tryToCapture() {
        Log.d("zyk", "tryToCapture: "+mFunVideoView.isPlaying());
        if (!mFunVideoView.isPlaying()) {
            showToast(R.string.media_capture_failure_need_playing);
            return;
        }

        final String path = mFunVideoView.captureCover(mFunDevice.getDevSn());
    }
    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCaptureSuccess(String picStr) {
        Log.d("zyk", "onCaptureSuccess:" + picStr);
    }

    @Override
    public void onCaptureFailed(int ErrorCode) {
        Log.d("zyk", "onCaptureFailed");

    }
}
