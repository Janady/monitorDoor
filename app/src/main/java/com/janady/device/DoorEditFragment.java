package com.janady.device;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseEditFragment;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.Door;
import com.janady.database.model.Remote;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

public class DoorEditFragment extends JBaseEditFragment {
    private Door mDoor;
    private EditText mNameEv;

    public void setDoor(Door door) {
        this.mDoor = door;
    }

    @Override
    protected String title() {
        return "智能门";
    }

    @Override
    protected void initGroupListView() {
        if (mDoor == null) mDoor = new Door();
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle("编辑")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        QMUICommonListItemView itemWithCustom = mGroupListView.createItemView("名称");
        itemWithCustom.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        EditText editText = new EditText(getContext());
        editText.setHint("请输入名称");
        editText.setText(mDoor.name);
        mNameEv = editText;
        itemWithCustom.addAccessoryCustomView(editText);
        section.addItemView(itemWithCustom, null);
        section.addTo(mGroupListView);


        QMUIGroupListView.Section section1 = QMUIGroupListView.newSection(getContext())
                .setTitle("已经添加的设备")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<Camera> blists = mDoor.cameraList;
        if (blists != null) {
            for (Camera camera : blists) {
                QMUICommonListItemView itemView = mGroupListView.createItemView(
                        ContextCompat.getDrawable(getContext(), R.drawable.ic_camera),
                        camera.name,
                        null,
                        QMUICommonListItemView.HORIZONTAL,
                        QMUICommonListItemView.ACCESSORY_TYPE_NONE);
                section.addItemView(itemView, null);
            }
        }
        if (mDoor.remote != null) {
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_remote),
                    mDoor.remote.name,
                    null,
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_NONE);
            section.addItemView(itemView, null);
        }
        if (mDoor.bluetooth != null) {
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_bluetooth),
                    mDoor.bluetooth.name,
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
                .addItem("绑定摄像机")
                .addItem("绑定蓝牙门禁")
                .addItem("绑定远程控制")
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
                                showBluetoothChoiceDialog();
                                break;
                            case 2:
                                showRemoteChoiceDialog();
                                break;
                            case 3:
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

    @Override
    protected void confirmed() {
        mDoor.name = String.valueOf(mNameEv.getText());
        MyApplication.liteOrm.save(mDoor);
    }

    private void deleteAndExit() {
        MyApplication.liteOrm.delete(mDoor);
        popBackStack();
    }
    private void showNumerousMultiChoiceDialog() {

        ArrayList<Camera> dlists = MyApplication.liteOrm.query(Camera.class);
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
    private void showBluetoothChoiceDialog() {

        ArrayList<Bluetooth> dlists = MyApplication.liteOrm.query(Bluetooth.class);
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
    private void showRemoteChoiceDialog() {

        ArrayList<Remote> dlists = MyApplication.liteOrm.query(Remote.class);
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
}
