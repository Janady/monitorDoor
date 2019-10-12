package com.janady.model;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.models.FunDevice;

import java.util.ArrayList;
import java.util.List;

public class ExpandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MainItemDescription> items;
    private Context context;
    private OnClickListener onClickListener;
    public void setOnItemClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }
    public ExpandAdapter(Context ctx, List<MainItemDescription> items) {
        context = ctx;
        this.items = items;
    }

    public void setData() {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.jtest_list_item_layout, viewGroup, false);
        return new MainViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MainItemDescription mainItemDescription = items.get(i);
        final MainViewHolder mainViewHolder = (MainViewHolder) viewHolder;
        List<ItemDescription> items = new ArrayList<>();
        List<Object> l = mainItemDescription.getList();
        if (l != null) {
            for (Object o : l){
                items.add((ItemDescription) o);
            }
        }
        mainViewHolder.showItems(items);
        if (items.size() > 4 && mainItemDescription.getDeviceType() != MainItemDescription.DeviceType.CAM) {
            mainViewHolder.exBtn.setVisibility(View.VISIBLE);
            mainViewHolder.exBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainItemDescription.expanded = !mainItemDescription.expanded;
                    LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
                    final float scale = context.getResources().getDisplayMetrics().density;
                    linerarParams.height = mainItemDescription.expanded ? -1 : (int)(100 * scale);
                    mainViewHolder.recyclerView.setLayoutParams(linerarParams);
                }
            });
            LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
            final float scale = context.getResources().getDisplayMetrics().density;
            linerarParams.height = mainItemDescription.expanded ? -1 : (int)(100 * scale);
            mainViewHolder.recyclerView.setLayoutParams(linerarParams);
        } else {
            mainViewHolder.exBtn.setVisibility(View.GONE);
        }

        mainViewHolder.imgView.setImageResource(mainItemDescription.getIconRes());

        mainViewHolder.nameTv.setText(mainItemDescription.getName());
        mainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) onClickListener.onMainClick(mainItemDescription);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public RecyclerView recyclerView;
        public Button exBtn;
        public ImageView imgView;
        private GridLayoutManager layoutManager;
        private BaseItemAdapter itemAdapter;
        private List<ItemDescription> items;

        public void showItems(List<ItemDescription> cItems) {
            items = cItems;
            itemAdapter.setData(cItems);
            recyclerView.setAdapter(itemAdapter);
        }
        public MainViewHolder(final Context context, View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
            imgView = itemView.findViewById(R.id.img);
            recyclerView = itemView.findViewById(R.id.listview);
            exBtn = itemView.findViewById(R.id.expandBtn);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(40, 40, 40, 4);//设置itemView中内容相对边框左，上，右，下距离
                }
            });
            itemAdapter = new BaseItemAdapter(context, null);
            itemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    if (onClickListener != null) onClickListener.onItemClick(items.get(pos));
                }
            });
            layoutManager = new GridLayoutManager(context, 4);
            recyclerView.setLayoutManager(layoutManager);
        }
    }
    public interface OnClickListener {
        void onItemClick(ItemDescription itemDescription);
        void onMainClick(MainItemDescription mainItemDescription);
    }
}