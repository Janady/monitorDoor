package com.example.funsdkdemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;

public class ListAdapterSimpleFunDevice extends BaseAdapter implements Comparator<SearchResult> {
	
	private Context mContext = null;
	private LayoutInflater mInflater;
	private List<FunDevice> mListDevs = new ArrayList<FunDevice>();
	private List<SearchResult> mListBleDevs = new ArrayList<SearchResult>();

	public FunDevType getCurrentDevType() {
		return currentDevType;
	}

	public void setCurrentDevType(FunDevType currentDevType) {
		this.currentDevType = currentDevType;
	}

	private FunDevType currentDevType;
	private OnClickListener mOnClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.mOnClickListener = onClickListener;
	}

	public ListAdapterSimpleFunDevice(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public ListAdapterSimpleFunDevice(Context context, FunDevType funDevType) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		currentDevType = funDevType;
	}


	public void updateBleDevice(List<SearchResult> devBleList) {
		mListBleDevs.clear();
		mListBleDevs.addAll(devBleList);
		Collections.sort(devBleList, this);
		this.notifyDataSetInvalidated();
	}

	public void updateDevice(List<FunDevice> devList) {
		mListDevs.clear();
		mListDevs.addAll(devList);
		this.notifyDataSetChanged();
		//this.notifyDataSetInvalidated();
	}
	
	public FunDevice getFunDevice(int position) {
		return (FunDevice)getItem(position);
	}
	
	@Override
	public Object getItem(int position) {
		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH ) {
			if (position >= 0 && position < mListBleDevs.size()) {
				return mListBleDevs.get(position);
			}

			if (position >= 0 && position < mListBleDevs.size()) {
				return mListBleDevs.get(position);
			}
		}else{
			if (position >= 0 && position < mListDevs.size()) {
				return mListDevs.get(position);
			}

			if (position >= 0 && position < mListDevs.size()) {
				return mListDevs.get(position);
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH ) {
			return mListBleDevs.size();
		}else{
			return mListDevs.size();
		}


	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition,
			View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_device_list_item,
					null);

			holder = new ViewHolder();
			holder.imgDevIcon = (ImageView) convertView
					.findViewById(R.id.imgDevIcon);
			holder.txtDevName = (TextView) convertView
					.findViewById(R.id.txtDevName);
			holder.txtDevStatus = (TextView) convertView
					.findViewById(R.id.txtDevStatus);
			holder.imgArrowIcon = (ImageView) convertView
					.findViewById(R.id.imgArrowIcon);
			holder.imgArrowIcon.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH) {
			//SearchResult bleDevice = mListBleDevs.get(groupPosition);
			final SearchResult result = (SearchResult) getItem(groupPosition);
			holder.imgDevIcon.setImageResource(currentDevType.getDrawableResId());
			holder.txtDevName.setText(result.getName());

			holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.drawable.common_title_color));

			holder.txtDevStatus.setText(String.format("Rssi: %d", result.rssi));
			//holder.txtDevStatus.setTextColor(0xff177fca);
			holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnClickListener != null) mOnClickListener.OnClickedBle(result);
				}
			});
		} else {
			final FunDevice funDevice = mListDevs.get(groupPosition);

			holder.imgDevIcon.setImageResource(funDevice.devType.getDrawableResId());
			holder.txtDevName.setText(funDevice.getDevName());

			//holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.drawable.common_title_color));

			holder.txtDevStatus.setText(funDevice.devStatus.getStatusResId());
			if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
				holder.txtDevStatus.setTextColor(0xff177fca);
			} else if (funDevice.devStatus == FunDevStatus.STATUS_OFFLINE) {
				holder.txtDevStatus.setTextColor(0xffda202e);
			} else {
				holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));
			}
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnClickListener != null) mOnClickListener.OnClickedFun(funDevice);
				}
			});
		}
		return convertView;
	}

	@Override
	public int compare(SearchResult lhs, SearchResult rhs)  {
		return rhs.rssi - lhs.rssi;
	}

	private class ViewHolder {
		ImageView imgDevIcon;
		TextView txtDevName;
		TextView txtDevStatus;
		ImageView imgArrowIcon;
	}

	public interface OnClickListener {
		public void OnClickedBle(SearchResult searchResult);
		public void OnClickedFun(FunDevice funDevice);
	}
}
