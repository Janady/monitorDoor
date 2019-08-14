package com.janady.home;

import android.content.Context;

import com.janady.model.QDItemDescription;

import java.util.ArrayList;
import java.util.List;

public class HomeDeviceController extends HomeController {
    public HomeDeviceController(Context context) {
        super(context);
    }

    @Override
    protected String getTitle() {
        return "设备";
    }

    @Override
    protected ItemAdapter getItemAdapter() {
        List<QDItemDescription> list = new ArrayList<>();
        return new ItemAdapter(getContext(), list);
    }
}
