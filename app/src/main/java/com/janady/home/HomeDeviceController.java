package com.janady.home;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.funsdkdemo.R;
import com.janady.device.AddDeviceFragment;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceCaptureListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunStreamType;
import com.lib.funsdk.support.widget.FunVideoView;

import java.util.List;

public class HomeDeviceController extends HomeController<FunDevice> {
    public HomeDeviceController(Context context) {
        super(context);
    }

    private String getTitle() {
        return String.format(getResources().getString(R.string.device_list_for_user),
                FunSupport.getInstance().getUserName());
    }

    @Override
    protected void initTopBar() {
        mTopBar.setTitle(getTitle());
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new AddDeviceFragment());
            }
        });
    }

    @Override
    protected ItemAdapter getItemAdapter() {
        List<FunDevice> list = FunSupport.getInstance().getDeviceList();
        return new ItemAdapter(getContext(), list);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        return layoutManager;
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
            FunVideoView funVideoView = (FunVideoView) holder.getView(R.id.funVideoView);
            playRealMedia(funVideoView, item);
//            if (item.getIconRes() != 0) {
//                holder.getImageView(R.id.item_icon).setImageResource(item.getIconRes());
//            }
        }
        private void playRealMedia(FunVideoView mFunVideoView, FunDevice mFunDevice) {
            if (mFunDevice.isRemote) {
                mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
            } else {
                String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
                mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
            }

            // 打开声音
            mFunVideoView.setMediaSound(true);
        }

    }
}
