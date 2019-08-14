package com.janady.setup;

import android.widget.Toast;

import com.example.common.DialogWaitting;
import com.qmuiteam.qmui.arch.QMUIFragment;

public abstract class JBaseFragment extends QMUIFragment {
    private Toast mToast;
    protected void showToast(int id) {
        showToast(getContext().getResources().getString(id));
    }
    protected void showToast(String text){
        if ( null != text ) {
            if ( null != mToast ) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
    private DialogWaitting mWaitDialog;
    protected void showWaitDialog() {
        if ( null == mWaitDialog ) {
            mWaitDialog = new DialogWaitting(getContext());
        }
        mWaitDialog.show();
    }
    protected void hideWaitDialog() {
        if ( null != mWaitDialog ) {
            mWaitDialog.dismiss();
        }
    }
}
