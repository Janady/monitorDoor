package com.janady.manager;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.Door;
import com.janady.database.model.Remote;
import com.janady.device.AddDeviceFragment;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.BluetoothLockFragment;
import com.janady.device.BluetoothOperatorFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.device.DoorEditFragment;
import com.janady.device.DoorListFragment;
import com.janady.device.RemoteEditFragment;
import com.janady.device.RemoteListFragment;
import com.janady.model.CategoryItemDescription;
import com.janady.model.ItemDescription;
import com.janady.model.MainItemDescription;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager _sInstance;
    public static DataManager getInstance() {
        if (_sInstance == null) {
            _sInstance = new DataManager();
        }
        return _sInstance;
    }
    public List<CategoryItemDescription> getCategoryDesciptions() {
        List<CategoryItemDescription> list = new ArrayList<>();

//        Door d = new Door();
//        d.name = "default";
//        MyApplication.liteOrm.save(d);
        // ArrayList<Camera> clists = MyApplication.liteOrm.query(Camera.class);
        List<FunDevice> funDevices = FunSupport.getInstance().getDeviceList();
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        ArrayList<Remote> rlists = MyApplication.liteOrm.query(Remote.class);
        ArrayList<Door> dlists = MyApplication.liteOrm.query(Door.class);
        CategoryItemDescription camera = new CategoryItemDescription(CameraListFragment.class, "摄像机", R.drawable.ic_camera, funDevices.size());
        //CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothListFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, blists.size());
        //CategoryItemDescription remote = new CategoryItemDescription(RemoteListFragment.class, "远程控制", R.drawable.ic_remote, rlists.size());
        CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothListFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth_black_24dp, blists.size());
        CategoryItemDescription remote = new CategoryItemDescription(RemoteListFragment.class, "远程控制", R.drawable.ic_remote_3, rlists.size());
        CategoryItemDescription room = new CategoryItemDescription(DoorListFragment.class, "场景", R.drawable.ic_room2, dlists.size());
        list.add(camera);
        list.add(bluetooth);
        list.add(remote);
        list.add(room);
        return list;
    }
    public List<CategoryItemDescription> showCategoryDesciptions() {
        List<CategoryItemDescription> list = new ArrayList<>();

        CategoryItemDescription camera = new CategoryItemDescription(AddDeviceFragment.class, "摄像机", R.drawable.ic_camera, 0);
        /*CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothEditFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, 0);
        CategoryItemDescription remote = new CategoryItemDescription(RemoteEditFragment.class, "远程控制", R.drawable.ic_remote, 0);
        CategoryItemDescription room = new CategoryItemDescription(DoorEditFragment.class, "房间", R.drawable.ic_room, 0);*/
        CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothEditFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth_black_24dp, 0);
        CategoryItemDescription remote = new CategoryItemDescription(RemoteEditFragment.class, "远程控制", R.drawable.ic_remote_3, 0);
        CategoryItemDescription room = new CategoryItemDescription(DoorEditFragment.class, "场景", R.drawable.ic_room2, 0);
        list.add(camera);
        list.add(bluetooth);
        list.add(remote);
        list.add(room);
        return list;
    }
    public List<MainItemDescription> getDescriptions() {
        List<MainItemDescription> list = new ArrayList<>();
        List<FunDevice> funDevices = FunSupport.getInstance().getDeviceList();
        for (FunDevice device : funDevices) {
            MainItemDescription mainDescription = new MainItemDescription(DeviceCameraFragment.class, device.devName, R.drawable.ic_camera, MainItemDescription.DeviceType.CAM);
            mainDescription.setDevice(device);
            list.add(mainDescription);
        }

        //MainItemDescription bleDescription = new MainItemDescription(BluetoothLockFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, MainItemDescription.DeviceType.BLE);
        MainItemDescription bleDescription = new MainItemDescription(BluetoothOperatorFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth_black_24dp, MainItemDescription.DeviceType.BLE);
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        List<Object> bitems = new ArrayList<>();
        for (Bluetooth bluetooth : blists) {
            ItemDescription itemDescription = new ItemDescription(BluetoothOperatorFragment.class, bluetooth.name, R.drawable.icon_check);
            itemDescription.setItem(bluetooth);
            bitems.add(itemDescription);
        }
        bleDescription.setList(bitems);
        list.add(bleDescription);

        ArrayList<Remote> rlists = MyApplication.liteOrm.query(Remote.class);
        for (Remote remote : rlists) {
            MainItemDescription remoteDescription = new MainItemDescription(BluetoothListFragment.class, remote.name, R.drawable.ic_remote_3, MainItemDescription.DeviceType.REMOTE);
            remoteDescription.setDevice(remote);
            List<Object> ritems = new ArrayList<>();
            for (Bluetooth bluetooth : blists) {
                ItemDescription itemDescription = new ItemDescription(BluetoothEditFragment.class, bluetooth.name, R.drawable.icon_check);
                itemDescription.setItem(bluetooth);
                ritems.add(itemDescription);
            }
            remoteDescription.setList(ritems);
            list.add(remoteDescription);
        }
        return list;
    }
}
