package com.janady.model;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;

import java.util.List;

public class BaseItemAdapter extends BaseRecyclerAdapter<ItemDescription> {
    private Context context;
    public BaseItemAdapter(Context ctx, List<ItemDescription> data) {
        super(ctx, data);
        this.context = ctx;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jtest_item_layout;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, int position, final ItemDescription item) {
        if (item.getIconRes() != 0) holder.getImageView(R.id.img).setImageResource(item.getIconRes());
        holder.getTextView(R.id.name).setText(item.getName());
    }
}
