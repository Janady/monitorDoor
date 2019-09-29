package com.janady.device;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.funsdkdemo.ActivityGuideMain;
import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.FunWifiPassword;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class AddDeviceFragment extends JBaseFragment {
    private QMUITopBarLayout mTopBar;
    private Button fastBtn;
    private Button lanBtn;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jadd_device, null);
        mTopBar = root.findViewById(R.id.topbar);

        fastBtn = root.findViewById(R.id.fast_config_button);
        fastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new WifiConfigFragment());
            }
        });
        lanBtn = root.findViewById(R.id.lan_device);
        lanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new DeviceLanFragment());
            }
        });
        initTopBar();
        return root;
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.guide_module_title_device_setwifi);
        mTopBar.addRightImageButton(R.drawable.ic_video_slowplay, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setClass(getContext(), ActivityGuideMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });
    }
}
