package com.janady.model;

import com.janady.setup.JBaseFragment;

public class ItemDescription {
    private Class<? extends JBaseFragment> mKitDemoClass;
    private String mKitName;
    private int mIconRes;
    private Object mItem;
    public ItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName){
        this(kitDemoClass, kitName, 0);
    }


    public ItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName, int iconRes) {
        mKitDemoClass = kitDemoClass;
        mKitName = kitName;
        mIconRes = iconRes;
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

    public Object getItem() {
        return mItem;
    }

    public void setItem(Object item) {
        this.mItem = item;
    }
}
