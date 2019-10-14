package com.janady.base;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.funsdkdemo.R;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.DeviceAddByUser;
import com.janady.manager.DataManager;
import com.janady.model.CategoryItemDescription;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JBaseSegmentFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    QMUITabSegment mTabSegment;
    ViewPager mContentViewPager;

    private Map<Integer, View> mPageMap = new HashMap<>();
    private List<View> mPageViews;
    private int mDestPage = 0;

    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPageViews.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            View view = getPageView(position);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle("添加设备");
    }
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.jfragment_tab_viewpager_layout, null);
        mTopBar = rootView.findViewById(R.id.topbar);
        mTabSegment = rootView.findViewById(R.id.tabSegment);
        mContentViewPager = rootView.findViewById(R.id.contentViewPager);
        initTopBar();
        initTabAndPager();

        return rootView;
    }

    private void initTabAndPager() {
        mPageViews = createPageViews();
        if (mPageViews == null || mPageViews.size() == 0) return;
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage, false);
        for (int i=0; i<mPageViews.size(); i++) {
            mTabSegment.addTab(new QMUITabSegment.Tab("page"+i));
        }
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    protected abstract List<View> createPageViews();
    private View getPageView(int position) {
        Integer page = new Integer(position);
        View view = mPageMap.get(new Integer(position));
        if (view == null) {
            view = mPageViews.get(position);
            mPageMap.put(page, view);
        }
        return view;
    }

}
