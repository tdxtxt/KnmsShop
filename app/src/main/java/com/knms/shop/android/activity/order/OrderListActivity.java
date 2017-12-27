package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.fragment.order.OrderCommentListFragment;
import com.knms.shop.android.fragment.order.OrderListFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表
 * Created by 654654 on 2017/4/30.
 */

public class OrderListActivity extends CommonOptionListActivity {
    private List<BaseFragment>  fragments;
    @Override
    protected List<BaseFragment> getFragments() {
        if (null == fragments){
            fragments = new ArrayList<>();
        }else if (fragments.size() > 0){
            fragments.clear();
        }
        fragments.add(OrderListFragment.newInstance(0));
        fragments.add(OrderListFragment.newInstance(1));
        fragments.add(OrderListFragment.newInstance(2));
        fragments.add(OrderListFragment.newInstance(3));
        fragments.add(OrderCommentListFragment.newInstance());
        return fragments;
    }

    private int state = 0;
    @Override
    protected void getParams(Intent intent) {
        if (null != intent)
            state = intent.getIntExtra("state",0);
    }

    @Override
    protected void initView() {
        super.initView();
        viewPager.setCurrentItem(state);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                String event;
                switch (position){
                    case 0:
                        event = "orderListStateAll";
                        break;
                    case 1:
                        event = "orderListStateDelivery";
                        break;
                    case 2:
                        event = "orderListStateHarvest";
                        break;
                    case 3:
                        event = "orderListStateReviews";
                        break;
                    case 4:
                        event = "orderListStateReply";
                        break;
                    default:
                        event = "orderListStateAll";
                }
                MobclickAgent.onEvent(OrderListActivity.this,event);
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("商场支付订单");
    }
}
