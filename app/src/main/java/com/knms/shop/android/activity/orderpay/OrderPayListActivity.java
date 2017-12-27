package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.widget.TextView;

import com.knms.shop.android.activity.order.CommonOptionListActivity;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.fragment.orderpay.OrderPayListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表
 * Created by 654654 on 2017/7/20.
 */

public class OrderPayListActivity extends CommonOptionListActivity {
    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("APP支付订单");
    }

    private List<BaseFragment>  fragments;
    @Override
    protected List<BaseFragment> getFragments() {
        if (null == fragments){
            fragments = new ArrayList<>();
        }else if (fragments.size() > 0){
            fragments.clear();
        }
        fragments.add(OrderPayListFragment.newInstance(0));
        fragments.add(OrderPayListFragment.newInstance(1));
        fragments.add(OrderPayListFragment.newInstance(2));
        fragments.add(OrderPayListFragment.newInstance(3));
        fragments.add(OrderPayListFragment.newInstance(4));
        fragments.add(OrderPayListFragment.newInstance(5));
        return fragments;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getParams(intent);
        viewPager.setCurrentItem(state);
        ((OrderPayListFragment)fragments.get(state)).refreshData();
    }

    private int state = 0;
    @Override
    protected void getParams(Intent intent) {
        if (null != intent)
            state = intent.getIntExtra("state",0);
    }

    @Override
    protected void initView() {
        viewPager.setCurrentItem(state);
    }
}
