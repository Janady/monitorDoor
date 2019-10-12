package com.janady.lkd;

import com.example.funsdkdemo.MyApplication;
import com.inuker.bluetooth.library.BluetoothClient;

/**
 * Created by kuki.
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(MyApplication.context);
                }
            }
        }
        return mClient;
    }
}