package com.janady.device;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseGroupedListFragment;
import com.janady.database.model.Door;
import com.janady.database.model.Remote;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

public class DoorListFragment extends JBaseGroupedListFragment {
    @Override
    protected String title() {
        return "远程控制";
    }

    @Override
    protected void initGroupListView() {
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle("已经添加的蓝牙设备")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<Door> blists = MyApplication.liteOrm.query(Door.class);
        for (final Door door : blists) {
            String detail = "";
            if (door.cameraList != null) detail += "摄像机："+ door.cameraList.size() + " ";
            if (door.bluetooth != null) detail += "安装蓝牙门禁 ";
            if (door.remote != null) detail += "安装远程控制 ";
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_bluetooth),
                    door.name,
                    detail,
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section.addItemView(itemView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DoorEditFragment fragment = new DoorEditFragment();
                    fragment.setDoor(door);
                    startFragment(fragment);
                }
            });
        }
        if (blists.size() > 0) section.addTo(mGroupListView);

        QMUIGroupListView.Section action = QMUIGroupListView.newSection(getContext());
        QMUICommonListItemView itemView = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.socket_task_add_normal),
                "新增智能门",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        action.addItemView(itemView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JBaseFragment fragment = new DoorEditFragment();
                startFragment(fragment);
            }
        });
        action.addTo(mGroupListView);
    }
}
