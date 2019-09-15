package com.janady.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.funsdkdemo.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.RecyclerViewHolder;
import com.janady.home.HomeDeviceController;
import com.janady.setup.JBaseFragment;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnAddSubDeviceResultListener;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class DeviceLanFragment extends JBaseFragment implements OnFunDeviceListener, OnAddSubDeviceResultListener {
    private QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ItemAdapter mItemAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<FunDevice> mLanDeviceList = new LinkedList<>();
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jbase_recycle_layout, null);
        mTopBar = root.findViewById(R.id.topbar);
        mRecyclerView = root.findViewById(R.id.listview);
        mPullRefreshLayout = root.findViewById(R.id.pull_to_refresh);
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
                requestToGetLanDeviceList();
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });
        initTopBar();
        initRecyclerView();

        refreshLanDeviceList();
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
        FunSupport.getInstance().registerOnFunDeviceListener(this);

        // 打开之后进行一次搜索
        requestToGetLanDeviceList();
        return root;
    }

    @Override
    public void onDestroy() {

        // 注销设备事件监听
        FunSupport.getInstance().removeOnFunDeviceListener(this);

        // 切换回网络访问
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);

        super.onDestroy();
    }
    private void requestToGetLanDeviceList() {
        if (!FunSupport.getInstance().requestLanDeviceList()) {
            showToast(R.string.guide_message_error_call);
        } else {
            showWaitDialog();
        }
    }
    private void refreshLanDeviceList() {
        hideWaitDialog();

        mLanDeviceList.clear();

        mLanDeviceList.addAll(FunSupport.getInstance().getLanDeviceList());

        mItemAdapter.notifyDataSetChanged();
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle("局域网中的设备");
    }
    private void initRecyclerView() {
        mLanDeviceList.clear();
        mLanDeviceList.addAll(FunSupport.getInstance().getLanDeviceList());
        mItemAdapter = new ItemAdapter(getContext(), mLanDeviceList);
        mItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                onItemClicked(pos);
            }
        });
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
    }
    protected void onItemClicked(int pos) {
        FunDevice funDevice = mLanDeviceList.get(pos);
        DeviceCameraFragment deviceCameraFragment = new DeviceCameraFragment();
        deviceCameraFragment.setFunDevice(funDevice);
        startFragment(deviceCameraFragment);
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        if (mLayoutManager == null) mLayoutManager = new GridLayoutManager(getContext(), 1);
        return mLayoutManager;
    }

    @Override
    public void onDeviceListChanged() {
        refreshLanDeviceList();
    }

    @Override
    public void onDeviceStatusChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceAddedSuccess() {

    }

    @Override
    public void onDeviceAddedFailed(Integer errCode) {

    }

    @Override
    public void onDeviceRemovedSuccess() {

    }

    @Override
    public void onDeviceRemovedFailed(Integer errCode) {

    }

    @Override
    public void onAPDeviceListChanged() {
        refreshLanDeviceList();
    }

    @Override
    public void onLanDeviceListChanged() {
        refreshLanDeviceList();
    }

    @Override
    public void onAddSubDeviceFailed(FunDevice funDevice, MsgContent msgContent) {

    }

    @Override
    public void onAddSubDeviceSuccess(FunDevice funDevice, MsgContent msgContent) {

    }

    static class ItemAdapter extends BaseRecyclerAdapter<FunDevice> {
        public ItemAdapter(Context ctx, List<FunDevice> data) {
            super(ctx, data);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.fun_device_item;
        }

        @Override
        public void bindData(RecyclerViewHolder holder, int position, FunDevice item) {
            holder.getTextView(R.id.item_name).setText(item.getDevName());
            ImageView iv = holder.getImageView(R.id.cover);
            String path = FunPath.getCoverPath(item.getDevSn());

            File file = new File(path);
            if (file.exists()) {
                iv.setVisibility(View.VISIBLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                iv.setImageBitmap(bitmap);
            } else {
                iv.setVisibility(View.GONE);
            }
//            FunVideoView funVideoView = (FunVideoView) holder.getView(R.id.funVideoView);
//            if (!item.hasLogin() || !item.hasConnected()) {
//                FunSupport.getInstance().requestDeviceLogin(item);
//            } else {
//                FunSupport.getInstance().requestDeviceConfig(item, SystemInfo.CONFIG_NAME);
//            }
            // playRealMedia(funVideoView, item);
//            if (item.getIconRes() != 0) {
//                holder.getImageView(R.id.item_icon).setImageResource(item.getIconRes());
//            }
        }
    }
}
