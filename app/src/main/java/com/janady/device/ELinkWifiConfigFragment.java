package com.janady.device;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunWifiPassword;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

public class ELinkWifiConfigFragment extends JBaseFragment {
    private QMUITopBarLayout mTopBar;
    private Button okBtn;
    private EditText mEditWifiSSID = null;
    private EditText mEditWifiPasswd = null;
    private TextView tipsTv;
    private EasyLink el;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jconf_wifi, null);
        mTopBar = root.findViewById(R.id.topbar);
        mEditWifiSSID = root.findViewById(R.id.editWifiSSID);
        mEditWifiPasswd = root.findViewById(R.id.editWifiPasswd);
        String currSSID = getConnectWifiSSID();
        mEditWifiSSID.setText(currSSID);
        mEditWifiPasswd.setText(FunWifiPassword.getInstance().getPassword(currSSID));
        tipsTv = root.findViewById(R.id.tips);
        okBtn = root.findViewById(R.id.ok_button);
        okBtn.setText("发送配网");
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okBtn.getText().toString().equalsIgnoreCase("发送配网")) {
                    okBtn.setText("关闭配网");
                    okBtn.setBackgroundColor(Color.rgb(255, 0, 0));
                    startQuickSetting();
                } else {
                    okBtn.setText("发送配网");
                    okBtn.setBackgroundColor(Color.rgb(63, 81, 181));
                    stopQuickSetting();
                }
            }
        });
        initTopBar();

        el = new EasyLink(getContext());
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
        EasyLinkParams elp = new EasyLinkParams();
        elp.ssid = mEditWifiSSID.getText().toString().trim();
        elp.password = mEditWifiPasswd.getText().toString().trim();
        Toast.makeText(getContext(), "open easylink", Toast.LENGTH_SHORT).show();

        el.startEasyLink(elp, new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                tipsTv.setText(message);
            }

            @Override
            public void onFailure(int code, String message) {
                tipsTv.setText(message);
            }
        });
    }

    private void stopQuickSetting() {
        el.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
            }

            @Override
            public void onFailure(int code, String message) {
            }
        });
    }
}
