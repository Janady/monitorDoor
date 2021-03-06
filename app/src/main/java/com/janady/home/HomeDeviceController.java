package com.janady.home;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.R;
import com.janady.adapter.CategoryItemAdapter;
import com.janady.base.JTabSegmentFragment;
import com.janady.device.AddDeviceFragment;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.device.DeviceCameraFragment;
import com.janady.manager.DataManager;
import com.janady.model.CategoryItemDescription;
import com.janady.setup.JBaseFragment;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.OPPTZPreset;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.List;

public class HomeDeviceController extends HomeController<CategoryItemDescription> implements OnFunDeviceOptListener {
    private List<CategoryItemDescription> mFunDeviceslist;
    private FunVideoView mFunVideoView;
    public HomeDeviceController(Context context) {
        super(context);
    }

    private String getTitle() {
        return String.format(getResources().getString(R.string.device_list_for_user),
                FunSupport.getInstance().getUserName());
    }

    @Override
    protected void initTopBar() {
        mTopBar.setTitle(getTitle());
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new JTabSegmentFragment());
            }
        });

        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }

    private CategoryItemAdapter mItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected BaseRecyclerAdapter<CategoryItemDescription> getItemAdapter() {
        if (mItemAdapter == null) {
            List<CategoryItemDescription> list = DataManager.getInstance().getCategoryDesciptions();
            mFunDeviceslist = list;
            mItemAdapter = new CategoryItemAdapter(getContext(), list);
        }
        return mItemAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        if (mLayoutManager == null) mLayoutManager = new GridLayoutManager(getContext(), 2);
        return mLayoutManager;
    }

    @Override
    protected void onItemClicked(int pos) {
        CategoryItemDescription funDevice = mFunDeviceslist.get(pos);
        try {
            JBaseFragment fragment = funDevice.getDemoClass().newInstance();
            startFragment(fragment);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
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

    private void playRealMedia(FunVideoView mFunVideoView, FunDevice mFunDevice) {
        if (mFunDevice.isRemote) {
            mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
        }

        // 打开声音
        mFunVideoView.setMediaSound(true);
    }
    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {
        Log.d("zyk", funDevice.getDevName() + " >> login success!");
        requestSystemInfo(funDevice);
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
            int index = mFunDeviceslist.indexOf(funDevice);
            Log.d("zyk", funDevice.getDevName() + " >> index:"+index);
            if (index >= 0) {
                View view = mLayoutManager.getChildAt(index);
                if (view != null) mFunVideoView = view.findViewById(R.id.funVideoView);
                if (mFunVideoView != null) playRealMedia(mFunVideoView, funDevice);
            }
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
}
