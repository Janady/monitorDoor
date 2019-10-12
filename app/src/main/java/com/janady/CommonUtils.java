package com.janady;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public class CommonUtils {

    public static void toast(String text) {
        Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT).show();
    }
}