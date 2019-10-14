package com.janady.device;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseGroupedListFragment;
import com.janady.database.model.Bluetooth;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

public class BluetoothListFragment extends JBaseGroupedListFragment {
    @Override
    protected String title() {
        return "蓝牙门禁";
    }

    @Override
    protected void initGroupListView() {
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle("已经添加的蓝牙设备")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        for (final Bluetooth bluetooth : blists) {
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_bluetooth_black_24dp),
                    bluetooth.name,
                    bluetooth.door == null ? "" : bluetooth.door.name,
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section.addItemView(itemView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BluetoothEditFragment fragment = new BluetoothEditFragment();
                    fragment.setBluetooth(bluetooth);
                    startFragment(fragment);
                }
            });
        }
        if (blists.size() > 0) section.addTo(mGroupListView);

        QMUIGroupListView.Section action = QMUIGroupListView.newSection(getContext());
        QMUICommonListItemView itemView = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.socket_task_add_normal),
                "新增蓝牙设备",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        action.addItemView(itemView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JBaseFragment fragment = new BluetoothEditFragment();
                startFragment(fragment);
            }
        });
        action.addTo(mGroupListView);
    }
}
