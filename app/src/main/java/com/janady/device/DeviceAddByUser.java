package com.janady.device;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.ActivityGuideUserLogin;
import com.example.funsdkdemo.DeviceActivitys;
import com.example.funsdkdemo.ListAdapterSimpleFunDevice;
import com.example.funsdkdemo.R;
import com.google.zxing.activity.CaptureActivity;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.janady.AppConstants;
import com.janady.lkd.ClientManager;
import com.janady.view.PullRefreshListView;
import com.janady.view.PullToRefreshFrameLayout;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;


public class DeviceAddByUser extends ActivityDemo implements OnClickListener, OnFunDeviceListener, OnItemSelectedListener, OnItemClickListener, OnFunDeviceOptListener {

	
	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
    private TextView mTextTip = null;
	
	private Spinner mSpinnerDevType = null;
	private EditText mEditDevSN;
	private EditText mEditSceneName;
	private EditText mEditPassword;
	private Button mBtnDevAdd = null;
	private ImageButton mBtnScanQrCode = null;
	
//	private ListView mListViewDev = null;
	private PullToRefreshFrameLayout mRefreshLayout;
	private PullRefreshListView mListViewDev = null;
	private ListAdapterSimpleFunDevice mAdapterDev = null;
	
	private FunDevice mFunDevice = null;
	private FunDevType mCurrDevType = null;

    private List<SearchResult> mBleDevices;

    private boolean isBleScanning = false;

	private final int MESSAGE_DELAY_FINISH = 0x100;
	
	
	// 定义当前支持通过序列号登录的设备类型 
	// 如果是设备类型特定的话,固定一个就可以了
	private final FunDevType[] mSupportDevTypes = {
			/*FunDevType.EE_DEV_NORMAL_MONITOR,
			FunDevType.EE_DEV_INTELLIGENTSOCKET,
			FunDevType.EE_DEV_SCENELAMP,
			FunDevType.EE_DEV_LAMPHOLDER,
			FunDevType.EE_DEV_CARMATE,
			FunDevType.EE_DEV_BIGEYE,
			FunDevType.EE_DEV_SMALLEYE,
			FunDevType.EE_DEV_BOUTIQUEROTOT,
			FunDevType.EE_DEV_SPORTCAMERA,
			FunDevType.EE_DEV_SMALLRAINDROPS_FISHEYE,
			FunDevType.EE_DEV_LAMP_FISHEYE,
			FunDevType.EE_DEV_MINIONS,
			FunDevType.EE_DEV_MUSICBOX,
			FunDevType.EE_DEV_SPEAKER,
			FunDevType.EE_DEV_LINKCENTER,
			FunDevType.EE_DEV_DASH_CAMERA,
			FunDevType.EE_DEV_POWER_STRIP,
			FunDevType.EE_DEV_FISH_FUN,
			FunDevType.EE_DEV_UFO,
			FunDevType.EE_DEV_IDR,
			FunDevType.EE_DEV_BULLET,
			FunDevType.EE_DEV_DRUM,*/
			FunDevType.EE_DEV_CAMERA,
			FunDevType.EE_DEV_BLUETOOTH,
			FunDevType.EE_DEV_REMOTER

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.jdevice_add_by_user);
		
		mTextTitle = (TextView)findViewById(R.id.textViewInTopLayout);
        mTextTip = (TextView)findViewById(R.id.textTip);
        mTextTip.setText("搜索设备：");

        mTextTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBleScanning) {
                    searchDevice();
                }else{
                    isBleScanning=false;
                    ClientManager.getClient().stopSearch();
                }
            }
        });

		mBtnBack = (ImageButton)findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);

        Intent intent = getIntent();
        int spinnerArrNo = intent.getIntExtra("DeviceTypsSpinnerNo",0);

		// 初始化设备类型选择器
		mSpinnerDevType = (Spinner)findViewById(R.id.spinnerDeviceType);
		String[] spinnerStrs = new String[mSupportDevTypes.length];
		for ( int i = 0; i < mSupportDevTypes.length; i ++ ) {
			spinnerStrs[i] = getResources().getString(mSupportDevTypes[i].getTypeStrId());
		}
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerStrs);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerDevType.setAdapter(adapter);
		//mSpinnerDevType.setSelection(0);
		//mCurrDevType = mSupportDevTypes[0];
        mSpinnerDevType.setSelection(spinnerArrNo);
        mCurrDevType = mSupportDevTypes[spinnerArrNo];
		mSpinnerDevType.setOnItemSelectedListener(this);
		
		mEditDevSN = (EditText)findViewById(R.id.editDeviceSN);
		mEditSceneName = (EditText)findViewById(R.id.editSceneName);
		mEditPassword = (EditText)findViewById(R.id.editDeviceSN);
		mBtnDevAdd = (Button)findViewById(R.id.devAddBtn);
		mBtnDevAdd.setOnClickListener(this);
		
		mBtnScanQrCode = (ImageButton)findViewById(R.id.btnScanCode);
		mBtnScanQrCode.setOnClickListener(this);

		mRefreshLayout = (PullToRefreshFrameLayout) findViewById(R.id.listOtherDevices);

		mListViewDev = mRefreshLayout.getPullToRefreshListView();
		mAdapterDev = new ListAdapterSimpleFunDevice(this, mCurrDevType);
		mAdapterDev.setOnClickListener(new ListAdapterSimpleFunDevice.OnClickListener() {
			@Override
			public void OnClickedBle(SearchResult searchResult) {
				mEditDevSN.setText(searchResult.getAddress());
				mEditSceneName.setText(searchResult.getName());
			}

			@Override
			public void OnClickedFun(FunDevice funDevice) {
				mEditDevSN.setText(funDevice.devSn);
				mEditSceneName.setText(funDevice.devName);
			}
		});

		mListViewDev.setAdapter(mAdapterDev);
		mListViewDev.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				searchDevice();
			}

		});

		// 以局域网内搜索过的设备,显示在下方作为测试设备添加
		if ( null != mAdapterDev ) {
			mAdapterDev.updateDevice(FunSupport.getInstance().getLanDeviceList());
		}

        mBleDevices = new ArrayList<SearchResult>();

		//mListViewDev.setOnItemClickListener(this);
		
		mTextTitle.setText(R.string.guide_module_title_device_add);

        searchDevice();

		// 设置登录方式为互联网方式
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
		
		// 监听设备类事件
		FunSupport.getInstance().registerOnFunDeviceListener(this);
		
		// 设备操作类事件(登录是否成功需要)
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
		
		if ( !FunSupport.getInstance().hasLogin() ) {
			// 用户还未登录,需要先登录
			startLogin();
		}

	}


	@Override
	protected void onDestroy() {
		
		// 注销设备事件监听
		FunSupport.getInstance().removeOnFunDeviceListener(this);
		
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		
		if ( null != mHandler ) {
			mHandler.removeCallbacksAndMessages(null);
		}
		
		super.onDestroy();
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtnInTopLayout:
			{
				// 返回/退出
				finish();
			}
			break;
		case R.id.devAddBtn:
			{
				requestDeviceLogin();
			}
			break;
		case R.id.btnScanCode:
			{
				startScanQrCode();
			}
			break;
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_DELAY_FINISH:
				{
					hideWaitDialog();
					
					// 启动/打开设备操作界面
					if ( null != mFunDevice ) {
						DeviceActivitys.startDeviceActivity(DeviceAddByUser.this, mFunDevice);
					}
					
					mFunDevice = null;
					finish();
				}
				break;
			}
		}
		
	};
	
	private int getSpinnerIndexByDeviceType(FunDevType type) {
		for ( int i = 0; i < mSupportDevTypes.length; i ++ ) {
			if ( type == mSupportDevTypes[i] ) {
				return i;
			}
		}
		return 0;
	}
	
	private void startLogin() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityGuideUserLogin.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	// 扫描二维码
	private void startScanQrCode() {
		Intent intent = new Intent();
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}
	
	// 设备登录
	private void requestDeviceLogin() {
		String devSN = mEditDevSN.getText().toString().trim();
		
		if ( devSN.length() == 0 ) {
			showToast(R.string.device_login_error_sn);
			return;
		}
		
		mFunDevice = null;
		
		showWaitDialog();
		
		if ( null == mFunDevice ) {
			// 虚拟一个设备, 只需要序列号和设备类型即可添加
			mFunDevice = new FunDevice();
			mFunDevice.devSn = devSN;
			mFunDevice.devName = devSN;
			mFunDevice.devType = mCurrDevType;
			mFunDevice.loginName = "admin";
			mFunDevice.loginPsw = "";
		}
		
		// 添加设备之前都必须先登录一下,以防设备密码错误,也是校验其合法性
		FunSupport.getInstance().requestDeviceLogin(mFunDevice);
//		for (int i = 10; i < 60; i++) {
//				// 虚拟一个设备, 只需要序列号和设备类型即可添加
//				mFunDevice = new FunDevice();
//				mFunDevice.devSn = "123456789123as" + i;
//				mFunDevice.devName = "12345678912345" + i;
//				mFunDevice.devType = FunDevType.EE_DEV_NORMAL_MONITOR;
//				mFunDevice.loginName = "admin";
//				mFunDevice.loginPsw = "";
//			FunSupport.getInstance().requestDeviceAdd(mFunDevice);
//		}
	}
	
	private void requestReloginByPasswd() {
		if ( null != mFunDevice ) {
			
//			mFunDevice.loginPsw = passwd;
			
			showWaitDialog();
			
			FunSupport.getInstance().requestDeviceLogin(mFunDevice);
		}
	}
	
	private void requestDeviceAdd() {
		if ( null != mFunDevice ) {
			FunSupport.getInstance().requestDeviceAdd(mFunDevice);
		}
	}

	/**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog() {
    	DialogInputPasswd inputDialog = new DialogInputPasswd(this,
    			getResources().getString(R.string.device_login_input_password),
    			"",
    			R.string.common_confirm,
    			R.string.common_cancel
    			){

					@Override
					public boolean confirm(String editText) {

						//保存密码
						FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),
								editText);
						// 库函数方式本地保存密码
							FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", editText);
							// 如果设置了使用本地保存密码，则将密码保存到本地文件
						// 重新以新的密码登录
						requestReloginByPasswd();
						return super.confirm(editText);
					}

					@Override
					public void cancel() {
						super.cancel();
						
						// 取消输入密码,直接退出
						finish();
					}
    		
    	};
    	
    	inputDialog.show();
    }

	@Override
	public void onDeviceListChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceStatusChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onDeviceAddedSuccess() {
		hideWaitDialog();
		showToast(R.string.device_opt_add_success);
		
		// 如果需要,在添加设备成功之后,可以更新一次设备列表
		FunSupport.getInstance().requestDeviceList();
	}


	@Override
	public void onDeviceAddedFailed(Integer errCode) {
		hideWaitDialog();
		showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceRemovedSuccess() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceRemovedFailed(Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAPDeviceListChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanDeviceListChanged() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if ( position >= 0 && position < mSupportDevTypes.length ) {
			mCurrDevType = mSupportDevTypes[position];
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if ( null != mAdapterDev ) {
			FunDevice funDevice = mAdapterDev.getFunDevice(position);
			if ( null != funDevice ) {
				// 当前选择的设备
				mFunDevice = funDevice;
				mCurrDevType = funDevice.devType;
				// 在Sipnner中设置当前选择设备的类型
				mSpinnerDevType.setSelection(getSpinnerIndexByDeviceType(mCurrDevType));
				// 在EditText中设置当前选择设备的序列号
				mEditDevSN.setText(mFunDevice.getDevSn());
			}
		}
		
	}


	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		if ( null != mFunDevice
				&& null != funDevice
				&& mFunDevice.getId() == funDevice.getId() ) {
			requestDeviceAdd();
		}
	}


	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		hideWaitDialog();
		
		showToast(FunError.getErrorStr(errCode));
		
		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		if ( errCode == FunError.EE_DVR_PASSWORD_NOT_VALID ) {
			showInputPasswordDialog();
		}
	}


	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice,
			String configName, int nSeq) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceSetConfigFailed(FunDevice funDevice, String configName,
			Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceChangeInfoSuccess(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceOptionSuccess(FunDevice funDevice, String option) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceOptionFailed(FunDevice funDevice, String option,
			Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListChanged(FunDevice funDevice,
			H264_DVR_FILE_DATA[] datas) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data) {
		if ( requestCode == 1 
				&& responseCode == 1 ) {
			// Demo, 扫描二维码的结果
			if ( null != data ) {
				String deviceSn = data.getStringExtra("SN");
				if ( null != deviceSn && null != mEditDevSN ) {
					mEditDevSN.setText(deviceSn);
				}
			}
		}
		
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

    /**
     * -----------------
     * 搜索蓝牙设备
     */
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2).build();

        ClientManager.getClient().search(request, mSearchResponse);
        mRefreshLayout.showState(AppConstants.EMPTY);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {

            mTextTip.setText("停止扫描设备....");
            isBleScanning = true;
            mRefreshLayout.showState(AppConstants.LIST);
            BluetoothLog.w("正在搜索蓝牙设备");
            //toolbar.setTitle(R.string.string_refreshing);
            mBleDevices.clear();
        }


        @Override
        public void onDeviceFounded(SearchResult device) {
//            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
            if (!mBleDevices.contains(device)) {
                mBleDevices.add(device);
                mAdapterDev.updateBleDevice(mBleDevices); }

            if (mBleDevices.size() > 0) {
                BluetoothLog.w("DeviceAddByUser.Bluetooth founds count: " + mBleDevices.size());
                mRefreshLayout.showState(AppConstants.LIST);
            }
        }

        @Override
        public void onSearchStopped() {
            isBleScanning = false;
            mTextTip.setText("扫描设备");
            BluetoothLog.w("DeviceAddByUser.onSearchStopped");
            mRefreshLayout.showState(AppConstants.LIST);
            //mTextTip.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {
            isBleScanning = false;
			mListViewDev.onRefreshComplete(true);
			mRefreshLayout.showState(AppConstants.LIST);
            BluetoothLog.w("DeviceAddByUser.onSearchCanceled");

            mTextTip.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }
    };
}
