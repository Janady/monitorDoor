package com.janady.device;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseEditFragment;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.Door;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

public class BluetoothEditFragment extends JBaseEditFragment {
    private Bluetooth mBluetooth;
    private EditText mNameEv;

    public void setBluetooth(Bluetooth bluetooth) {
        this.mBluetooth = bluetooth;
    }

    @Override
    protected String title() {
        return "蓝牙门禁";
    }

    @Override
    protected void initGroupListView() {
        if (mBluetooth == null) mBluetooth = new Bluetooth();
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());
                //.setTitle("编辑")
                //.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        QMUICommonListItemView itemWithCustom = mGroupListView.createItemView("名称");
        itemWithCustom.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        EditText editText = new EditText(getContext());
        editText.setHint("请输入名称");
        editText.setText(mBluetooth.name);
        mNameEv = editText;
        itemWithCustom.addAccessoryCustomView(editText);
        ImageButton btnQr = new ImageButton(getContext());
        btnQr.setImageResource(R.drawable.input_sn_core);
        btnQr.setLayoutParams(new RelativeLayout.LayoutParams(28,28));
        itemWithCustom.addAccessoryCustomView(btnQr);
        section.addItemView(itemWithCustom, null);
        section.addTo(mGroupListView);


        QMUIGroupListView.Section section1 = QMUIGroupListView.newSection(getContext())
                .setTitle("已经绑定的门")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mBluetooth.door != null) {
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_remote),
                    mBluetooth.door.name,
                    null,
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_NONE);
            section.addItemView(itemView, null);
        }
        section1.addTo(mGroupListView);
    }

    @Override
    protected void showBottomSheetList() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("绑定门")
                .addItem("退出并删除")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                showNumerousMultiChoiceDialog();
                                break;
                            case 1:
                                deleteAndExit();
                                break;

                            default:
                                break;
                        }
                    }
                })
                .build()
                .show();
    }

    private void showNumerousMultiChoiceDialog() {

        ArrayList<Door> dlists = MyApplication.liteOrm.query(Door.class);
        if (dlists == null || dlists.size() == 0) {
            Toast.makeText(getActivity(), "请先添加智能门", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] items = new String[dlists.size()];
        for (int i = 0; i<dlists.size(); i++) {
            items[i] = dlists.get(i).name;
        }

        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(getActivity())
                .setCheckedItems(new int[]{1, 3})
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction("提交", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                String result = "你选择了 ";
                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                    result += "" + builder.getCheckedItemIndexes()[i] + "; ";
                }
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.create(mCurrentDialogStyle).show();
    }
    @Override
    protected void confirmed() {
        mBluetooth.name = String.valueOf(mNameEv.getText());
        MyApplication.liteOrm.save(mBluetooth);
    }
    private void deleteAndExit() {
        MyApplication.liteOrm.delete(mBluetooth);
        popBackStack();
    }
}
