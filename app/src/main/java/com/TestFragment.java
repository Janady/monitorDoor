package com;

import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.JTabSegmentFragment;
import com.janady.base.RecyclerViewHolder;
import com.janady.device.DeviceCameraFragment;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ExpandAdapter mItemAdapter;
    private List<CListItems> mFunDevices;
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.jbase_recycle_layout, null);

        mTopBar = rootView.findViewById(R.id.topbar);
        mRecyclerView = rootView.findViewById(R.id.listview);
        mPullRefreshLayout = rootView.findViewById(R.id.pull_to_refresh);
        mPullRefreshLayout.setRefreshOffsetCalculator(new QMUICenterGravityRefreshOffsetCalculator());
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //onDataLoaded();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });

        initTopBar();
        initRecyclerView();
        return rootView;
    }
    private void initTopBar() {
        mTopBar.setTitle("我的摄像机");
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new JTabSegmentFragment());
            }
        });

        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }
    private void initRecyclerView() {
        mFunDevices = createData();
        mItemAdapter = new ExpandAdapter(getContext(), mFunDevices);
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
    }

    public List<CListItems> createData() {
        List<CListItems> res = new ArrayList<>();
        for (int i = 0; i < 10+(int)(Math.random()*10); i++) {
            CListItems items = new CListItems();
            items.name = "list-" + i;
            items.expanded = Math.random() > 0.5;
            for (int j=0; j<1+(int)(Math.random()*10);j++) {
                CItem item = new CItem();
                item.name = i+ "-item-"+j;
                items.items.add(item);
            }
            res.add(items);
        }
        return res;
    }
    //***********************************************************
    // item class
    //***********************************************************
    public class CItem {
        public String name = "";
    }
    public class CListItems {
        public CListItems() {
            name = "";
            expanded = false;
            items = new ArrayList<>();
        }
        public String name;
        public boolean expanded;
        public List<CItem> items;
    }

    //***********************************************************
    // adapter container
    //***********************************************************
    public class ItemAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<CItem> items;
        private Context context;
        public ItemAdapter(Context ctx, List<CItem> cItems) {
            context = ctx;
            items = cItems;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(context).inflate(R.layout.jtest_item_layout, viewGroup, false);
            return new ItemViewHolder(context, v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            final CItem item = items.get(i);
            itemViewHolder.nameTv.setText(item.name);
            itemViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, item.name, Toast.LENGTH_LONG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTv;
            public ImageView imageView;
            public ItemViewHolder(Context context, @NonNull View itemView) {
                super(itemView);
                nameTv = itemView.findViewById(R.id.name);
                imageView = itemView.findViewById(R.id.img);
            }
        }
    }
    public class BaseItemAdapter extends BaseRecyclerAdapter<CItem> {
        private Context context;
        public BaseItemAdapter(Context ctx, List<CItem> data) {
            super(ctx, data);
            this.context = ctx;
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.jtest_item_layout;
        }

        @Override
        public void bindData(RecyclerViewHolder holder, int position, final CItem item) {
            holder.getTextView(R.id.name).setText(item.name);
            holder.getImageView(R.id.img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, item.name, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public class ExpandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<CListItems> items;
        private Context context;
        public ExpandAdapter(Context ctx, List<CListItems> items) {
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            final MainViewHolder mainViewHolder = (MainViewHolder) viewHolder;
            final CListItems cItems = items.get(i);
            mainViewHolder.showItems(cItems.items);
            mainViewHolder.nameTv.setText(cItems.name + ": " + cItems.items.size() + " > " + (cItems.expanded ? "+" : "-"));
            mainViewHolder.exBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, cItems.name, Toast.LENGTH_LONG).show();
                    cItems.expanded = !cItems.expanded;
                    LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
                    final float scale = context.getResources().getDisplayMetrics().density;
                    linerarParams.height = cItems.expanded ? -1 : (int)(120 * scale);
                    mainViewHolder.recyclerView.setLayoutParams(linerarParams);
                }
            });
            LinearLayout.LayoutParams linerarParams = (LinearLayout.LayoutParams) mainViewHolder.recyclerView.getLayoutParams();
            final float scale = context.getResources().getDisplayMetrics().density;
            linerarParams.height = cItems.expanded ? -1 : (int)(120 * scale);
            mainViewHolder.recyclerView.setLayoutParams(linerarParams);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class MainViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTv;
            public RecyclerView recyclerView;
            public Button exBtn;
            private GridLayoutManager layoutManager;
            private BaseItemAdapter itemAdapter;

            public void showItems(List<CItem> cItems) {
                itemAdapter.setData(cItems);
                recyclerView.setAdapter(itemAdapter);
            }
            public MainViewHolder(final Context context, View itemView) {
                super(itemView);
                nameTv = itemView.findViewById(R.id.name);
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
                        Toast.makeText(context, pos + "-clicked", Toast.LENGTH_LONG).show();
                    }
                });
                layoutManager = new GridLayoutManager(context, 4);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }
}
