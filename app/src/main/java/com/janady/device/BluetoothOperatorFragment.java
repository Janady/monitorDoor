package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseSegmentFragment;
import com.janady.database.model.Bluetooth;

import java.util.ArrayList;
import java.util.List;

public class BluetoothOperatorFragment extends JBaseSegmentFragment implements View.OnClickListener {
    @Override
    protected String title() {
        return "蓝牙控制";
    }

    @Override
    protected List<View> createPageViews() {
        List<View> viewList = new ArrayList<>();
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        for (Bluetooth bluetooth : blists) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_operator_layout, null);
            view.findViewById(R.id.open).setOnClickListener(this);
            view.findViewById(R.id.close).setOnClickListener(this);
            view.findViewById(R.id.lock).setOnClickListener(this);
            view.findViewById(R.id.unlock).setOnClickListener(this);
            viewList.add(view);
        }
        return viewList;
    }

    @Override
    protected void tabSelected(int index) {

    }

    @Override
    protected void tabUnSelected(int index) {

    }

    @Override
    public void onClick(View v) {

    }
}
