package com;

import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.JTabSegmentFragment;
import com.janady.base.RecyclerViewHolder;
import com.janady.database.model.Bluetooth;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.manager.DataManager;
import com.janady.model.CategoryItemDescription;
import com.janady.model.ExpandAdapter;
import com.janady.model.ItemDescription;
import com.janady.model.MainItemDescription;
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

public class TestFragment extends JBaseFragment implements ExpandAdapter.OnClickListener {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ExpandAdapter mItemAdapter;
    private List<MainItemDescription> mainItems;
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
        mainItems = DataManager.getInstance().getDescriptions();
        mItemAdapter = new ExpandAdapter(getContext(), mainItems);
        mItemAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
    }

    public List<MainItemDescription> createData() {
        List<MainItemDescription> res = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MainItemDescription items = new MainItemDescription(CameraListFragment.class, "camera-"+i, R.drawable.ic_camera, MainItemDescription.DeviceType.CAM);
            res.add(items);
        }
        MainItemDescription bleDescription = new MainItemDescription(BluetoothListFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, MainItemDescription.DeviceType.BLE);
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        List<Object> items = new ArrayList<>();
        for (int i =0; i< 5; i++) {
            ItemDescription itemDescription = new ItemDescription(BluetoothEditFragment.class, "ble-"+i, R.drawable.ic_bluetooth);
            items.add(itemDescription);
        }
        bleDescription.setList(items);
        res.add(bleDescription);
        for (int i = 0; i < 2; i++) {
            MainItemDescription remoteDescription = new MainItemDescription(BluetoothListFragment.class, "remote-"+i, R.drawable.ic_remote, MainItemDescription.DeviceType.REMOTE);
            List<Object> remote_items = new ArrayList<>();
            for (int j =0; j < 3; j++) {
                ItemDescription itemDescription = new ItemDescription(BluetoothEditFragment.class, "remote-"+i, R.drawable.ic_remote);
                remote_items.add(itemDescription);
            }
            remoteDescription.setList(remote_items);
            res.add(remoteDescription);
        }
        return res;
    }

    @Override
    public void onItemClick(ItemDescription itemDescription) {
        Toast.makeText(getContext(), itemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();
        try {
            JBaseFragment fragment = itemDescription.getDemoClass().newInstance();
            startFragment(fragment);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMainClick(MainItemDescription mainItemDescription) {
        JBaseFragment fragment = null;
        Toast.makeText(getContext(), mainItemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();try {
            fragment = mainItemDescription.getDemoClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        if (fragment instanceof DeviceCameraFragment && mainItemDescription.getDevice() instanceof FunDevice) {
            ((DeviceCameraFragment)fragment).setFunDevice((FunDevice) mainItemDescription.getDevice());
        }

        if (fragment != null) startFragment(fragment);
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
}
