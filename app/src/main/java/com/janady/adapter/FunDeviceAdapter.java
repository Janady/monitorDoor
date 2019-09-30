package com.janady.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.example.funsdkdemo.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.models.FunDevice;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.io.File;
import java.util.List;

public class FunDeviceAdapter extends BaseRecyclerAdapter<FunDevice> {
    private Context context;
    public FunDeviceAdapter(Context ctx, List<FunDevice> data) {
        super(ctx, data);
        this.context = ctx;
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
