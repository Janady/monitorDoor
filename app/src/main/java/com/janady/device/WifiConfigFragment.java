package com.janady.device;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.FunWifiPassword;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.DeviceWifiManager;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.funsdk.support.utils.StringUtils;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class WifiConfigFragment extends JBaseFragment implements OnFunDeviceWiFiConfigListener {
    private QMUITopBarLayout mTopBar;
    private Button okBtn;
    private EditText mEditWifiSSID = null;
    private EditText mEditWifiPasswd = null;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jconf_wifi, null);
        mTopBar = root.findViewById(R.id.topbar);
        mEditWifiSSID = root.findViewById(R.id.editWifiSSID);
        mEditWifiPasswd = root.findViewById(R.id.editWifiPasswd);
        String currSSID = getConnectWifiSSID();
        mEditWifiSSID.setText(currSSID);
        mEditWifiPasswd.setText(FunWifiPassword.getInstance().getPassword(currSSID));
        okBtn = root.findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSetting();
            }
        });
        initTopBar();
        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);
        return root;
    }

    @Override
    public void onDestroy() {
        stopQuickSetting();

        super.onDestroy();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.guide_module_title_device_setwifi);
    }
    private String getConnectWifiSSID() {
        try {
            WifiManager wifimanage=(WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifimanage.getConnectionInfo().getSSID().replace("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // 开始快速配置
    private void startQuickSetting() {

        try {
            WifiManager wifiManage = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManage.getConnectionInfo();
            DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

            if ( null == wifiInfo ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            String ssid = wifiInfo.getSSID().replace("\"", "");
            if ( StringUtils.isStringNULL(ssid) ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            ScanResult scanResult = DeviceWifiManager.getInstance(getContext()).getCurScanResult(ssid);
            if ( null == scanResult ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            int pwdType = MyUtils.getEncrypPasswordType(scanResult.capabilities);
            String wifiPwd = mEditWifiPasswd.getText().toString().trim();

            if ( pwdType != 0 && StringUtils.isStringNULL(wifiPwd) ) {
                // 需要密码
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            StringBuffer data = new StringBuffer();
            data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);

            String submask;
            if (wifiDhcp.netmask == 0) {
                submask = "255.255.255.0";
            } else {
                submask = MyUtils.formatIpAddress(wifiDhcp.netmask);
            }

            String mac = wifiInfo.getMacAddress();
            StringBuffer info = new StringBuffer();
            info.append("gateway:").append(MyUtils.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
                    .append(" dns1:").append(MyUtils.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
                    .append(" ");

            showWaitDialog();

            FunSupport.getInstance().startWiFiQuickConfig(ssid,
                    data.toString(), info.toString(),
                    MyUtils.formatIpAddress(wifiDhcp.gateway),
                    pwdType, 0, mac, -1);

            FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopQuickSetting() {
        FunSupport.getInstance().stopWiFiQuickConfig();
    }
    @Override
    public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
        hideWaitDialog();
        if ( null != funDevice ) {
            showToast(String.format(
                    getResources().getString(R.string.device_opt_set_wifi_success),
                    funDevice.getDevSn()));
        }
    }
}
