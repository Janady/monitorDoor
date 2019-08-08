package com.example.funsdkdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.cloud.CloudDirectory;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceFileListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunFileData;
import com.lib.funsdk.support.utils.Define;
import com.lib.funsdk.support.utils.FileDataUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Jeff on 16/5/16.
 *
 */
public class DeviceCameraRecordFileAdapter extends BaseAdapter implements OnFunDeviceFileListener,IFunSDKResult {

    private FunDevice mFunDevice;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<FunFileData> mDatas = null;
    private LruCache<String, Bitmap> mLruCache;
    private ListView mImagList;
    private int mPlayingIndex = -1;
    private List<Integer> mDispPosition = new ArrayList<Integer>();

    //尝试重新获取没加载的缩略图的计数器和最大次数
    private int retryCounter = 0;
    private final int RETRY_MAX_NUM = 10;

    private final int MESSAGE_SEARCH_FILE_INFO = 0x100;
    private final int MESSAGE_SEARCH_FILE_PICTURE = 0x101;
    private final int MESSAGE_SEARCH_FILE_SUCCESS = 0x102;
    private int mMediaType = Define.MEDIA_TYPE_DEVICE;
    private int mUserId;
    private Map<String, JSONObject> mExFileInfo = new HashMap<String, JSONObject>();
    public DeviceCameraRecordFileAdapter(Context context, ListView imgList, FunDevice funDevice, List<FunFileData> datas) {
        mContext = context;
        mImagList = imgList;
        mInflater = LayoutInflater.from(mContext);
        mFunDevice = funDevice;
        mDatas = datas;
        initDtata();
        FunSupport.getInstance().registerOnFunDeviceFileListener(this);
        mUserId = FunSDK.GetId(mUserId,this);
    }

    private void initDtata() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为maxMemory的1/3
        int cacheSize = maxMemory / 3;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public void release() {
        FunSupport.getInstance().removeOnFunDeviceFileListener(this);
        
        if ( null != mHandler ) {
        	mHandler.removeCallbacksAndMessages(null);
        	mHandler = null;
        }
        
        if ( null != mLruCache ) {
            mLruCache.evictAll();
            mLruCache = null;
        }
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public FunFileData getRecordFile(int position) {
        return (FunFileData)getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        FunFileData info = mDatas.get(position);
        int oldPosition = -1;
        if (null == convertView) {
            convertView = mInflater.inflate(
                    R.layout.item_device_camera_pic, parent, false);
            holder = new ViewHolder();
            holder.baseLayout = (RelativeLayout) convertView
                    .findViewById(R.id.rl_push_result_layout);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.iv_push_result_checked);
            holder.id = (TextView) convertView
                    .findViewById(R.id.tv_push_result_id);
            holder.time = (TextView) convertView
                    .findViewById(R.id.tv_push_result_time);
            holder.event = (TextView) convertView
                    .findViewById(R.id.tv_push_result_event);
            holder.status = (TextView) convertView
                    .findViewById(R.id.tv_push_result_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            oldPosition = holder.position;
        }

        if (oldPosition != position) {
            holder.position = position;
            retryCounter = 0;
            if (oldPosition >= 0) {
                if ( mDispPosition.contains(oldPosition) ) {
                    mDispPosition.remove((Integer)oldPosition);
                }
            }
            if ( !mDispPosition.contains(position) ) {
                mDispPosition.add(position);
            }

            if ( !info.hasSeachedFile() ) {
                // 文件信息还没搜索到,是需要搜索的.
                resetSearchFileInfo();
            }
        }

        if (info != null) {
            holder.baseLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(info.getBeginTimeStr())) {
                String timeStr;
                if (info.getFileName().endsWith(".h264")) {
                    // 当前正在播放的高亮显示
                    if ( mPlayingIndex == position ) {
                        holder.event.setTextColor(0xffe97425);
                        holder.time.setTextColor(0xffe97425);
                    } else {
                        holder.event.setTextColor(0xff636363);
                        holder.time.setTextColor(0xff636363);
                    }

                    timeStr = info.getBeginTimeStr() + " - " + info.getEndTimeStr();
                } else {
                    timeStr = info.getBeginTimeStr();
                }
                holder.time.setText(timeStr);
            } else {
                holder.time.setText("");
            }
            holder.id.setText(info.getFileName());
            String type = FileDataUtils.getStrFileType(mContext, info.getFileName());
            holder.event.setText(type);
            if (!TextUtils.isEmpty(type) && type.equals(mContext.getString(R.string.device_pic_type_manual))) {
                holder.event.setCompoundDrawables(null, null,
                        setTopDrawable(R.drawable.icon_devpicture_hand),
                        null);
            } else {
                holder.event.setCompoundDrawables(null, null, null, null);
            }
        }else {
            holder.baseLayout.setVisibility(View.INVISIBLE);
        }


        setImageForImageView(position,info.getCapTempPath(),holder.image);

        return convertView;
    }

    public void resetDispItem() {
        mDispPosition.clear();
    }

    private void resetSearchFileInfo() {
        if ( null != mHandler ) {
            mHandler.removeMessages(MESSAGE_SEARCH_FILE_INFO);
            mHandler.sendEmptyMessageDelayed(MESSAGE_SEARCH_FILE_INFO, 1000);
        }
    }

    private void resetSearchFileBmp() {
        if ( null != mHandler ) {
            mHandler.removeMessages(MESSAGE_SEARCH_FILE_PICTURE);
            mHandler.sendEmptyMessageDelayed(MESSAGE_SEARCH_FILE_PICTURE, 1000);
        }
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent ex) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case EUIMSG.DOWN_RECODE_BPIC_START: //录像缩略图下载开始
                break;
            case EUIMSG.MC_DownloadMediaThumbnail:
            case EUIMSG.DOWN_RECODE_BPIC_FILE: //录像缩略图下载--文件下载结果返回
                if (ex.seq < mDatas.size()) {
                    FunFileData funFileData =  mDatas.get(ex.seq);
                    if (funFileData != null) {
                        H264_DVR_FILE_DATA info = funFileData.getFileData();
                        info.downloadStatus = Define.ON_DOWNLOAD_COMPLETE;
                        funFileData.setCapTempPath(ex.str);
                        setItemBitmap(ex.seq);
                    }
                }
                break;
            case EUIMSG.DOWN_RECODE_BPIC_COMPLETE://录像缩略图下载-下载完成（结束）

                break;
        }
        return 0;
    }


    class ViewHolder {
        RelativeLayout baseLayout;
        ImageView image;
        TextView id;
        TextView time;
        TextView event;
        TextView status;
        int position;
    }

    public void setPlayingIndex(int index) {
        mPlayingIndex = index;
        notifyDataSetChanged();
    }
    
    public void setBitmapTempPath(String path){
    	mDatas.get(mPlayingIndex).setCapTempPath(path);
    	notifyDataSetChanged();
    }

    private Drawable setTopDrawable(int src) {
        Drawable topDrawable = mContext.getResources().getDrawable(src);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(),
                topDrawable.getMinimumHeight());
        return topDrawable;
    }

    private Bitmap loadBitmap(int position, boolean toDownload) {

    	if ( position >= 0 && position < mDatas.size() ) {
	        FunFileData funFileData = mDatas.get(position);
	        H264_DVR_FILE_DATA info = funFileData.getFileData();
	        if (null == info || info.st_3_beginTime.st_0_year == 0) {
	            return null;
	        }

	        String fileName = FunPath.PATH_PHOTO_TEMP
	                + File.separator
	                + FunPath.getDownloadFileNameByData(info, 1, true);
	        final long fileSize_thumb = FunPath.isFileExists(fileName);
	        if (fileSize_thumb > 0) {
	            Bitmap bitmap = getBitmapFromLruCache(fileName);
	            if (null == bitmap) {
	                bitmap = dealBitmap(fileName);
	            }
	
	            if (null != bitmap) {
	                addBitmapToLruCache(fileName, bitmap);
                    return bitmap;
	            } else {
	                FunPath.deleteFile(fileName);
	            }
	        }else if (toDownload) {
                int time = FunSDK.ToTimeType(new int[]{info.st_3_beginTime.st_0_year,
                        info.st_3_beginTime.st_1_month, info.st_3_beginTime.st_2_day,
                        info.st_3_beginTime.st_4_hour, info.st_3_beginTime.st_5_minute,
                        info.st_3_beginTime.st_6_second});
                if (mMediaType == Define.MEDIA_TYPE_DEVICE) {
                    FunSDK.DownloadRecordBImage(mUserId, mFunDevice.getDevSn(), 0, time,
                            fileName, 0, position);
                } else if (mMediaType == Define.MEDIA_TYPE_CLOUD) {
                    String downFileParam = getJSONByFileName(info.getFileName());

                    // 如果文件的完整JSON信息不存在,传文件名,兼容新老版本
                    if ( null == downFileParam ) {
                        downFileParam = info.getFileName();
                    }
                    CloudDirectory.DownloadThumbnail(mUserId, mFunDevice.getDevSn(),
                            downFileParam, fileName, 160, 90, position);
                }
	        }
    	}
        return null;
    }

    private String getJSONByFileName(String fileName) {
        String retStr = null;
        synchronized (mExFileInfo) {
            JSONObject fileObj = mExFileInfo.get(fileName);
            if ( null != fileObj ) {
                retStr = fileObj.toString();
            }
        }
        return retStr;
    }

    public void updateExInfo(String exStr) {
        // 获取到JSON里面的VideoArray，作为缩略图下载的参数
        synchronized (mExFileInfo) {
            mExFileInfo.clear();

            if ( null != exStr ) {
                try {
                    JSONObject jsonObj = new JSONObject(exStr);
                    JSONArray fileArray = jsonObj.optJSONObject("AlarmCenter").optJSONObject("Body").optJSONArray("VideoArray");
                    for ( int i = 0; i < fileArray.length(); i ++ ) {
                        JSONObject fileObj = fileArray.getJSONObject(i);
                        String IndexFile = fileObj.optString("IndexFile");
                        if ( null != IndexFile && IndexFile.length() > 0 ) {
                            mExFileInfo.put(IndexFile, fileObj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Bitmap dealBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap == null)
            return null;
        Bitmap newBtimap = Bitmap.createScaledBitmap(bitmap, 160, 90, true);
        bitmap.recycle();
        return newBtimap;
    }

    private void checkAndLoadBmps() {
        new Thread() {

            @Override
            public void run() {
                FunLog.d("test", "-------------checkAndLoadBmps Begin");
                for ( int i = 0; i < mDispPosition.size(); i ++ ) {
                    //checkItemBitmap(mDispPosition.get(i), true);
                	if (mDispPosition.size() <= 0) {
						return;
					}
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    FunLog.d("test", "-------------Position:" + mDispPosition.get(i));
                    if ( checkItemBitmap(mDispPosition.get(i)) ) {
                        if ( null != mHandler ) {
                            Message msg = new Message();
                            msg.what = MESSAGE_SEARCH_FILE_SUCCESS;
                            msg.arg1 = mDispPosition.get(i);
                            mHandler.sendMessage(msg);
                        }
                    }
                }
                if (!checkDispItemBitmap() && retryCounter < RETRY_MAX_NUM) {
                    retryCounter++;
                    if (mHandler != null) {
                    	mHandler.removeMessages(MESSAGE_SEARCH_FILE_PICTURE);
                    	mHandler.sendEmptyMessageDelayed(MESSAGE_SEARCH_FILE_PICTURE, 5000);
					}
                }
            }

        }.start();

    }

    private boolean checkItemBitmap(int position) {
        FunFileData fileData = mDatas.get(position);
        if ( null == fileData ) {
            return false;
        }

        if ( FunPath.isValidPath(fileData.getFileName()) ) {
            Bitmap bmp = loadBitmap(position, true);
            if ( null != bmp ) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDispItemBitmap() {
        for ( int i = 0; i < mDispPosition.size(); i ++ ) {
            FunFileData fileData = mDatas.get(mDispPosition.get(i));
            if (null == fileData || loadBitmap(mDispPosition.get(i), false) == null) {
                return false;
            }
        }
        return true;
    }

    private void setItemBitmap(int position) {
        FunFileData fileData = mDatas.get(position);
        if ( null == fileData ) {
            return;
        }

        ImageView iv = mImagList.findViewWithTag("image:" + position);
        if ( null != iv ) {
            Bitmap bmp = loadBitmap(position, false);
            if ( null != bmp ) {
                iv.setImageBitmap(bmp);
            }
        }
    }

    /**
     * 将图片存储到LruCache
     */
    public void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (mLruCache != null) {
            synchronized (mLruCache) {
                if (getBitmapFromLruCache(key) == null && bitmap != null) {
                    mLruCache.put(key, bitmap);
                }
            }
        }
    }

    /**
     * 从LruCache缓存获取图片
     */
    public Bitmap getBitmapFromLruCache(String key) {
        if ( null == mLruCache ) {
            return null;
        }

        return mLruCache.get(key);
    }

    /**
     * 为ImageView设置图片(Image) 1 从缓存中获取图片 2 若图片不在缓存中则为其设置默认图片
     */
    private void setImageForImageView(int position,String imagePath, ImageView imageView) {
        imageView.setTag("image:" + position);
        Bitmap bitmap;
        if ( null != imagePath && imagePath.length() > 0 ) {
            bitmap = getBitmapFromLruCache(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                return;
            }
        }

        bitmap = loadBitmap(position,true);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else {
            imageView.setImageResource(R.color.thumbnail_bg_color);
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_SEARCH_FILE_INFO:
                {
                    // check and search file
                    //TODO 写一个回调接口
                }
                break;
                case MESSAGE_SEARCH_FILE_PICTURE:
                {
                    // load Bitmap
                    checkAndLoadBmps();
                }
                break;
                case MESSAGE_SEARCH_FILE_SUCCESS:
                {
                    setItemBitmap(msg.arg1);
                }
                break;
            }
        }

    };


    @Override
    public void onDeviceFileDownCompleted(FunDevice funDevice, String path, int nSeq) {
        if ( null != mHandler ) {
            Message msg = new Message();
            msg.what = MESSAGE_SEARCH_FILE_SUCCESS;
            msg.arg1 = nSeq;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onDeviceFileDownProgress(int totalSize, int progress, int nSeq) {

    }

    @Override
    public void onDeviceFileDownStart(boolean isStartSuccess, int nSeq) {

    }

}
