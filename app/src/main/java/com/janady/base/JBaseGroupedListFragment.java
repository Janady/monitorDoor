package com.janady.base;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;
import java.util.List;

public abstract class JBaseGroupedListFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    protected QMUIGroupListView mGroupListView;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jfragment_grouplistview, null);

        mTopBar = root.findViewById(R.id.topbar);
        mGroupListView = root.findViewById(R.id.groupListView);
        initTopBar();

        initGroupListView();

        return root;
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(title());
    }
    protected abstract String title();
    protected void initGroupListView() {
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle("Section 1: 默认提供的样式")
                .setDescription("Section 1 的描述")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (QMUICommonListItemView itemView : getItems()) {
            section.addItemView(itemView, null);
        }
        section.addTo(mGroupListView);
    }
    List<QMUICommonListItemView> getItems() {
        List<QMUICommonListItemView> list = new ArrayList<>();
        return list;
    }
}
