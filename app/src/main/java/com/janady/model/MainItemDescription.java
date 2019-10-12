package com.janady.model;

import com.janady.setup.JBaseFragment;

import java.util.List;

public class MainItemDescription {
    public enum DeviceType {
        BLE, REMOTE, CAM
    }
    private Class<? extends JBaseFragment> mKitDemoClass;
    private String mKitName;
    private int mIconRes;
    private List<Object> mList;
    private DeviceType mDeviceType;
    private Object mDevice;
    public Boolean expanded;
    public MainItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName, DeviceType type){
        this(kitDemoClass, kitName, 0, type);
    }


    public MainItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName, int iconRes, DeviceType type) {
        mKitDemoClass = kitDemoClass;
        mKitName = kitName;
        mIconRes = iconRes;
        mDeviceType = type;
        expanded = false;
    }
    public Class<? extends JBaseFragment> getDemoClass() {
        return mKitDemoClass;
    }

    public String getName() {
        return mKitName;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public DeviceType getDeviceType() {
        return mDeviceType;
    }

    public Object getDevice() {
        return mDevice;
    }

    public void setDevice(Object device) {
        this.mDevice = device;
    }

    public List<Object> getList() {
        return mList;
    }

    public void setList(List<Object> list) {
        this.mList = list;
    }
}
