package com.janady;

import android.Manifest;
import android.os.Bundle;

import com.example.funsdkdemo.R;
import com.example.funsdkdemo.dialog.PermissionDialog;
import com.janady.setup.FragmentSetup;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

@DefaultFirstFragment(value = FragmentSetup.class)
public class MainActivity extends QMUIFragmentActivity {
    private PermissionDialog mPermissionDialog;
    private RxPermissions rxPermissions;
    @Override
    protected int getContextViewId() {
        return R.id.setup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);

        checkPermission(getString(R.string.all_permission), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    private boolean checkPermission(final String permissionTitle, final String... permission) {
        try{
            final boolean[] result = new boolean[1];
            rxPermissions.requestEach(permission).subscribe(new io.reactivex.functions.Consumer<Permission>() {
                @Override
                public void accept(final Permission permission) throws Exception {
                    if (permission.granted) {
                        result[0] = true;
                        permissionGranted(permission);
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        result[0] = false;
                        if (mPermissionDialog == null) {
                            mPermissionDialog = new PermissionDialog();
                        }
                        if (!mPermissionDialog.isAdded()) {
                            mPermissionDialog.setTitle(permissionTitle);
                            mPermissionDialog.setOperateListener(new PermissionDialog.OperateListener() {
                                @Override
                                public void onCancel() {
                                    result[0] = false;
                                    permissionResult(false,permission);
                                }

                                @Override
                                public void onConfirm() {
                                    permissionResult(true,permission);
                                    result[0] = true;
                                }
                            });
                            mPermissionDialog.show(getSupportFragmentManager(), "mPermissionDialog");
                        }
                    } else {
                        // Need to go to the settings
                        if (mPermissionDialog == null) {
                            mPermissionDialog = new PermissionDialog();
                        }
                        if (!mPermissionDialog.isAdded()) {
                            mPermissionDialog.setTitle(permissionTitle);
                            mPermissionDialog.setOperateListener(new PermissionDialog.OperateListener() {
                                @Override
                                public void onCancel() {
                                    result[0] = false;
                                    permissionResult(false,permission);
                                }

                                @Override
                                public void onConfirm() {
                                    permissionResult(true,permission);
                                    result[0] = true;
                                }
                            });
                            mPermissionDialog.show(getSupportFragmentManager(), "mPermissionDialog");
                        }

                    }
                }
            });
            return result[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void permissionGranted(Permission permission) {

    }

    private void permissionResult(boolean isSuccess,Permission permission){

    }
}
