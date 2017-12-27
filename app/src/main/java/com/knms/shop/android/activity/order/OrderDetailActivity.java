package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.fragment.order.OrderDetailFragment;
import com.knms.shop.android.fragment.order.OrderStateFragment;
import com.knms.shop.android.helper.CommonHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情
 * Created by 654654 on 2017/5/2.
 */

public class OrderDetailActivity extends CommonOptionListActivity {
    private List<BaseFragment>  fragments;
    @Override
    protected List<BaseFragment> getFragments() {
        if (null == fragments){
            fragments = new ArrayList<>();
        }else if (fragments.size() > 0){
            fragments.clear();
        }
        fragments.add(OrderStateFragment.newInstance(orderId));
        fragments.add(OrderDetailFragment.newInstance(orderId));
        return fragments;
    }
    private String orderId = "";

    private String sid;

    @Override
    protected void getParams(Intent intent) {
        if (null != intent) {
            orderId = intent.getStringExtra("orderId");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRightIcon(R.drawable.icon_5);
        iv_icon_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.goChat(OrderDetailActivity.this,sid);
            }
        });
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
//                int v;
                String event;
                if (1 == position){
                    event = "orderDetail";
//                    v = View.VISIBLE;
                }else {
//                    v = View.GONE;
                    event = "orderState";
                }
                MobclickAgent.onEvent(OrderDetailActivity.this,event);
//                setRightVisibility(v);
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
        tv_center.setText("订单信息");
    }
}
