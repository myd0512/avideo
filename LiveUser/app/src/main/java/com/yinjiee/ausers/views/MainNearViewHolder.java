package com.yinjiee.ausers.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yinjiee.ausers.R;
import com.yinjiee.ausers.adapter.ViewPagerAdapter;
import com.yinjiee.ausers.interfaces.MainAppBarExpandListener;
import com.yinjiee.ausers.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 附近
 */

public class MainNearViewHolder extends AbsMainParentViewHolder {

    public MainNearViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String[] getIndicatorTitles() {
        return new String[]{
                WordUtil.getString(R.string.near)
        } ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_near;
    }

    @Override
    public void init() {
        super.init();
        mViewHolders = new AbsMainChildTopViewHolder[1];
        mViewHolders[0] = new MainNearNearViewHolder(mContext, mViewPager);
        MainAppBarExpandListener expandListener = new MainAppBarExpandListener() {
            @Override
            public void onExpand(boolean expand) {
                if (mViewPager != null) {
                    mViewPager.setCanScroll(expand);
                    mViewHolders[mViewPager.getCurrentItem()].setCanRefresh(expand);
                }
            }
        };
        List<View> list = new ArrayList<>();
        for (AbsMainChildTopViewHolder vh : mViewHolders) {
            vh.setAppBarExpandListener(expandListener);
            list.add(vh.getContentView());
        }
        mViewPager.setAdapter(new ViewPagerAdapter(list));

        mIndicator.setViewPager(mViewPager);
        mViewHolders[0].addTopView(mTopView);
    }

}
