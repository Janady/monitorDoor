package com.janady.base;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.funsdkdemo.R;
import com.google.zxing.activity.CaptureActivity;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public abstract class JBaseEditFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    protected QMUIGroupListView mGroupListView;
    protected int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Button button;
    @Override
    protected View onCreateView() {View root = LayoutInflater.from(getActivity()).inflate(R.layout.jfragment_edit_view, null);
        mTopBar = root.findViewById(R.id.topbar);
        mGroupListView = root.findViewById(R.id.groupListView);
        button = root.findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmed();
                popBackStack();
            }
        });
        initTopBar();

        initGroupListView();

        return root;
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(title());
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetList();
                    }
                });
    }

    // 扫描二维码
    private void startScanQrCode(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CaptureActivity.class);
        startActivityForResult(intent, 1);
    }
    protected abstract String title();
    protected abstract void initGroupListView();
    protected abstract void showBottomSheetList();
    protected abstract void confirmed();
}
