package com.janady.setup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.common.DialogWaitting;
import com.example.common.UIFactory;
import com.example.funsdkdemo.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.xm.ui.widget.SpinnerSelectItem;

import java.util.Arrays;
import java.util.List;

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
    /**
     *  判断某个字符串是否存在于数组中
     *  用来判断该配置是否通道相关
     *  @param stringArray 原数组
     *  @param source 查找的字符串
     *  @return 是否找到
     */
    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);

        // 利用list的包含方法,进行判断
        return tempList.contains(source);
    }

    protected int getIntValue(View layout,int id) {
        if (layout == null) {
            return 0;
        }
        View v = layout.findViewById(id);
        return getIntValue(v);
    }

    protected int getIntValue(View v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof EditText) {
            EditText v0 = (EditText) v;
            return Integer.valueOf(v0.getText().toString());
        } else if (v instanceof CheckBox) {
            CheckBox v0 = (CheckBox) v;
            return v0.isChecked() ? 1 : 0;
        } else if (v instanceof SeekBar) {
            SeekBar v0 = (SeekBar) v;
            return v0.getProgress();
        } else if (v instanceof Spinner) {
            Spinner sp = (Spinner) v;
            Object iv = v.getTag();
            if (iv != null && iv instanceof int[]) {
                int[] values = (int[]) iv;
                int i = sp.getSelectedItemPosition();
                if (i >= 0 && i < values.length) {
                    return values[i];
                }
                return 0;
            }
        } else {
//            System.err.println("GetIntValue:" + id);
        }
        return 0;
    }

    protected int initSpinnerText(Spinner sp, String[] texts, int[] values) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item,
                texts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        if (values == null) {
            values = new int[texts.length];
            for (int i = 0; i < texts.length; ++i) {
                values[i] = i;
            }
        }
        sp.setTag(values);
        return 0;
    }

    public int setValue(View v, int value) {
        if (v instanceof SpinnerSelectItem) {
            v = ((SpinnerSelectItem) v).getSpinner();
        }
        if (v instanceof EditText) {
            EditText v0 = (EditText) v;
            v0.setText(String.valueOf(value));
        } else if (v instanceof CheckBox) {
            CheckBox v0 = (CheckBox) v;
            v0.setChecked(value != 1);
        } else if (v instanceof SeekBar) {
            SeekBar v0 = (SeekBar) v;
            v0.setProgress(value);
        } else if (v instanceof Spinner) {
            Spinner sp = (Spinner) v;
            Object iv = v.getTag();
            if (iv != null && iv instanceof int[]) {
                int values[] = (int[]) iv;
                for (int i = 0; i < values.length; ++i) {
                    if (value == values[i]) {
                        sp.setSelection(i);
                        break;
                    }
                }
            }
        }
        return 0;
    }
}
