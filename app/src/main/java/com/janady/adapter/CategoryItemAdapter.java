package com.janady.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.model.CategoryItemDescription;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.List;

public class CategoryItemAdapter extends BaseRecyclerAdapter<CategoryItemDescription> {
    private Context context;
    public CategoryItemAdapter(Context ctx, List<CategoryItemDescription> data) {
        super(ctx, data);
        this.context = ctx;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jdevice_category;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, int position, CategoryItemDescription item) {
        holder.getTextView(R.id.name).setText(item.getName());
        ImageView iv = holder.getImageView(R.id.img);
        if (item.getIconRes() != 0) iv.setImageResource(item.getIconRes());
        else iv.setVisibility(View.GONE);
        TextView tagTv = holder.getTextView(R.id.tag);
        tagTv.setVisibility(View.GONE);
        TextView tv = holder.getTextView(R.id.countTv);
        tv.setText(""+item.getCount());
        QMUILinearLayout mTestLayout = (QMUILinearLayout) holder.getView(R.id.test_layout);
        mTestLayout.setRadius(QMUIDisplayHelper.dp2px(context, 10));
    }
}
