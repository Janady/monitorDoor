package com.janady.adapter;

import android.content.Context;

import com.example.funsdkdemo.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.List;

public class BaseItemAdapter extends BaseRecyclerAdapter<String> {
    private Context context;
    public BaseItemAdapter(Context ctx, List<String> data) {
        super(ctx, data);
        this.context = ctx;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jdevice_category;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, int position, String item) {
        holder.getTextView(R.id.name).setText(item);
        QMUILinearLayout mTestLayout = (QMUILinearLayout) holder.getView(R.id.test_layout);
        mTestLayout.setRadius(QMUIDisplayHelper.dp2px(context, 10));
    }
}
