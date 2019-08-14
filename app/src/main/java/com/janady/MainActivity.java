package com.janady;

import android.os.Bundle;

import com.example.funsdkdemo.R;
import com.janady.setup.FragmentSetup;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;

@DefaultFirstFragment(value = FragmentSetup.class)
public class MainActivity extends QMUIFragmentActivity {
    @Override
    protected int getContextViewId() {
        return R.id.setup;
    }
}
