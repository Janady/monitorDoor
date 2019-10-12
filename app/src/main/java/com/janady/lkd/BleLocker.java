package com.janady.lkd;


import android.os.Handler;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.CommonUtils;
import com.janady.StringUtils;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

public class BleLocker {
    private IBleLockerListener mIBleLockerListener;

    public void setBleLockerCallBack(IBleLockerListener iBleLockerListener){
        this.mIBleLockerListener = iBleLockerListener;
    }

    private String mBleName;

    public String getmBleName() {
        return mBleName;
    }

    public void setmBleName(String mBleName) {
        this.mBleName = mBleName;
    }

    private String mMac;

    public String getmMac() {
        return mMac;
    }

    public void setmMac(String mMac) {
        this.mMac = mMac;
    }

    public String getmService() {
        return mService.toString();
    }

    public void setmService(String mService) {
        this.mService = UUID.fromString(mService);
    }

    public String getmNotifitesCharacter() {
        return mNotifitesCharacter.toString();
    }

    public void setmNotifitesCharacter(String mNotifitesCharacter) {
        this.mNotifitesCharacter = UUID.fromString(mNotifitesCharacter);
    }

    public String getmWriteCharacter() {
        return mWriteCharacter.toString();
    }

    public void setmWriteCharacter(String mWriteCharacter) {
        this.mWriteCharacter = UUID.fromString(mWriteCharacter);
    }

    public BleGattProfile getmBleGattProfile() {
        return mBleGattProfile;
    }

    public void setmBleGattProfile(BleGattProfile mBleGattProfile) {
        this.mBleGattProfile = mBleGattProfile;
    }
    private String mPassword;

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }


    private UUID mService;
    private UUID mNotifitesCharacter;
    private UUID mWriteCharacter;

    private int mHeartBeatInterval = 2000;
    private int mTimeOut;

    private boolean mAutoConnect = false;

    private boolean mConnected = false;
    private BleGattProfile mBleGattProfile;

    private Handler mHandler = new Handler();


    private String mBleNotifyValue = "";

    public BleLocker(){

    }

    public BleLocker(String BleMacAddr, Boolean isAutoConnect, String ServiceUUID, String NotifitesCharacterUUID, String WriteCharacterUUID, String Password, int heartBeatInterval, IBleLockerListener callBack) {
        this.mMac = BleMacAddr;
        this.mHeartBeatInterval = heartBeatInterval;
        this.mIBleLockerListener = callBack;
        this.mService = UUID.fromString(ServiceUUID);
        this.mNotifitesCharacter = UUID.fromString(NotifitesCharacterUUID);
        this.mWriteCharacter = UUID.fromString(WriteCharacterUUID);
        this.mPassword = Password;
        this.mAutoConnect= isAutoConnect;
        if(mAutoConnect){
            this.connect();
        }
    }

    public BleLocker(String BleName, String ServiceUUID, String NotifitesCharacterUUID, String WriteCharacterUUID, String Password, int heartBeatInterval, IBleLockerListener callBack) {
        this.mBleName = BleName;
        this.mHeartBeatInterval = heartBeatInterval;
        this.mIBleLockerListener = callBack;
        this.mService = UUID.fromString(ServiceUUID);
        this.mNotifitesCharacter = UUID.fromString(NotifitesCharacterUUID);
        this.mWriteCharacter = UUID.fromString(WriteCharacterUUID);
        this.mPassword = Password;
    }

    Runnable Heartbeat=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub

            sendDataByString(getmPassword());
            BluetoothLog.v(String.format("发送密码"));

            //要做的事情
            if(mIBleLockerListener!=null){
                mConnected = true;
                mIBleLockerListener.onHeartBeatting(0, "发送心跳...");
            }

            mHandler.postDelayed(Heartbeat, mHeartBeatInterval);//每n秒执行一次runnable.
        }
    };

    public void connect(){
        BluetoothLog.v(String.format("onBluetooth Connecting... %s", mMac));

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().registerConnectStatusListener(mMac, mConnectStatusListener);

        ClientManager.getClient().connect(mMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));

                if (code == REQUEST_SUCCESS) {
                    //mAdapter.setGattProfile(profile);
                    mBleGattProfile = profile;
                    BluetoothLog.v(String.format("mBleGattProfile:\n%s", mBleGattProfile));
                    ClientManager.getClient().notify(mMac, mService, mNotifitesCharacter, mNotifyRsp);

                    if(mIBleLockerListener!=null){
                        mConnected = true;
                        mIBleLockerListener.onConnected(code, "蓝牙设备连接成功");

                        sendDataByString(getmPassword());
                        //mHandler.postDelayed(Heartbeat,0);
                    }
                }
            }
        });
    }


    public void disconnect(){
        mConnected = false;
        ClientManager.getClient().disconnect(mMac);
        ClientManager.getClient().unregisterConnectStatusListener(mMac, mConnectStatusListener);
        mHandler.removeCallbacks(Heartbeat);

        if(mIBleLockerListener!=null){
            mIBleLockerListener.onDisconnected(REQUEST_SUCCESS, "蓝牙设备断开连接");
        }
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            if(status == STATUS_CONNECTED){
                mConnected=true;
            }else{
                mConnected=false;
            }
            connectIfNeeded();
        }
    };

    private void connectIfNeeded() {
        if (!mConnected && mAutoConnect) {
            mHandler.removeCallbacks(Heartbeat);
            connect();
        }
    }

    public void lock(){
        sendDataByString(getmPassword()+"SETL");
        BluetoothLog.v(String.format("发送锁指令：SETL"));
    }

    public void open(){
        sendDataByString(getmPassword()+"SETO");
        BluetoothLog.v(String.format("发送开指令：SETO"));
    }

    public void close(){
        sendDataByString(getmPassword()+"SETC");
        BluetoothLog.v(String.format("发送关指令：SETC"));
    }

    public void stop(){
        sendDataByString(getmPassword()+"SETS");
        BluetoothLog.v(String.format("发送停指令：SETS"));
    }

    public void changePassword(String newPass){
        sendDataByString(getmPassword()+"CHA"+newPass);
        BluetoothLog.v(String.format("修改密码指令：CHA"));

    }

    public void sendDataByString(String content){
        if(ClientManager.getClient().getConnectStatus(mMac)== Constants.STATUS_DEVICE_CONNECTED) {
            /*ClientManager.getClient().write(mMac, mService, mWriteCharacter,
                    ByteUtils.stringToBytes(content), mWriteRsp);*/
            ClientManager.getClient().write(mMac, mService, mWriteCharacter,
                    content.getBytes(), mWriteRsp);
        }else{
            BluetoothLog.v(String.format("设备未连接"));
        }
    }

    private final BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {
            String rtv ="";
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v(String.format("read: %s", ByteUtils.byteToString(data)));
                rtv=ByteUtils.byteToString(data);

                //CommonUtils.toast("success");

            } else {
                //CommonUtils.toast("failed");
                rtv="bluetooth read failed";
                BluetoothLog.v(String.format("read: "));
            }

            if(mIBleLockerListener!=null){ mIBleLockerListener.onBleReadResponse(code, rtv); }
        }
    };

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            String rtv ="";
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v(String.format("write success"));
                //CommonUtils.toast("success");
                rtv="发送成功";
            } else {
                rtv="发送失败";
                BluetoothLog.v(String.format("write failed"));
                //CommonUtils.toast("failed");
            }

            if(mIBleLockerListener!=null){ mIBleLockerListener.onBleWriteResponse(code, rtv); }
        }
    };

    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            mBleNotifyValue = "";
            if (service.equals(mService) && character.equals(mNotifitesCharacter)) {
                //mBleNotifyValue = String.format("%s", ByteUtils.byteToString(value));
                mBleNotifyValue = String.format("%s", StringUtils.asciiToString(value));
                BluetoothLog.v(String.format("%s", mBleNotifyValue));
            }

            String rtvMsg="";
            switch (mBleNotifyValue) {
                case "ERROR PASS":
                    rtvMsg = "密码错误";
                    break;
                case "ERROR COMM":
                    rtvMsg = "密码正确，命令错误";
                    break;
                case "SET ERROR":
                    rtvMsg = "密码正确，命令正确，操作码不正确";
                    break;
                case "CHA OK":
                    rtvMsg = "更改密码成功";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onPasswordChanged(REQUEST_SUCCESS,rtvMsg);}
                    break;
                case "SET OPEN":
                    rtvMsg = "控制开";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onOpened(REQUEST_SUCCESS,rtvMsg);}
                    break;
                case "SET CLOSE":
                    rtvMsg = "控制关";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onClosed(REQUEST_SUCCESS,rtvMsg);}
                    break;
                case "SET LOCK":
                    rtvMsg = "控制锁";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onLock(REQUEST_SUCCESS,rtvMsg);}
                    break;
                case "SET STOP":
                    rtvMsg = "控制停";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onStoped(REQUEST_SUCCESS,rtvMsg);}
                    break;
            }

            if(mIBleLockerListener!=null){ mIBleLockerListener.onBleNotifyResponse(REQUEST_SUCCESS, mBleNotifyValue, rtvMsg); }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("Ble Notify success");
                BluetoothLog.v("Ble Notify success");
            } else {
                CommonUtils.toast("Ble Notify failed");
                BluetoothLog.v("Ble Notify failed");
            }

            //if(mIBleLockerListener!=null){ mIBleLockerListener.onBleNotifyResponse(code, mBleNotifyValue); }
        }
    };

    private final BleUnnotifyResponse mUnnotifyRsp = new BleUnnotifyResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v("Ble UnnotifyRespionse success");
                CommonUtils.toast("UnnotifyRespionse success");
            } else {
                BluetoothLog.v("Ble UnnotifyRespionse failed");
                CommonUtils.toast("UnnofityResponse failed");
            }
        }
    };

    private final BleMtuResponse mMtuResponse = new BleMtuResponse() {
        @Override
        public void onResponse(int code, Integer data) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("request mtu success,mtu = " + data);
            } else {
                CommonUtils.toast("request mtu failed");
            }
        }
    };


    public interface IBleLockerListener {
        void onPasswordChanged(int code, String rtvMsg);

        void onClosed(int code, String rtvMsg);

        void onStoped(int code, String rtvMsg);

        void onLock(int code, String rtvMsg);

        void onOpened(int code, String rtvMsg);

        void onBleReadResponse(int code, String rtvMsg);

        void onBleWriteResponse(int code, String rtvMsg);

        void onBleNotifyResponse(int code, String NotifyValue, String rtvMsg);

        void onConnected(int code, String rtvMsg);

        void onDisconnected(int code, String rtvMsg);

        void onHeartBeatting(int code, String rtvMsg);
    }
}
