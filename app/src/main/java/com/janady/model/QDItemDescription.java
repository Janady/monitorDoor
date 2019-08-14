package com.janady.model;

import com.janady.setup.JBaseFragment;

/**
 * @author cginechen
 * @date 2016-10-21
 */

public class QDItemDescription {
    private Class<? extends JBaseFragment> mKitDemoClass;
    private String mKitName;
    private int mIconRes;
    private String mDocUrl;

    public QDItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName){
        this(kitDemoClass, kitName, 0, "");
    }


    public QDItemDescription(Class<? extends JBaseFragment> kitDemoClass, String kitName, int iconRes, String docUrl) {
        mKitDemoClass = kitDemoClass;
        mKitName = kitName;
        mIconRes = iconRes;
        mDocUrl = docUrl;
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

    public String getDocUrl() {
        return mDocUrl;
    }
}