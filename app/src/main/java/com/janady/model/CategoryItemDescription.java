package com.janady.model;

import com.janady.setup.JBaseFragment;

public class CategoryItemDescription {
    private Class<? extends JBaseFragment> mKitDemoClass;
    private String mKitName;
    private int mIconRes;
    private int mCount;
    private Object mDevice;
    public CategoryItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName){
        this(kitDemoClass, kitName, 0, 0);
    }


    public CategoryItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName, int iconRes, int count) {
        mKitDemoClass = kitDemoClass;
        mKitName = kitName;
        mIconRes = iconRes;
        mCount = count;
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

    public int getCount() {
        return mCount;
    }

    public Object getDevice() {
        return mDevice;
    }
}
