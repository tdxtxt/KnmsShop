package com.knms.shop.android.activity.details;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseFragmentActivity;

/**
 * Created by Administrator on 2017/3/30.
 */

public class BBpriceDetailsActivity extends BaseFragmentActivity {
    private String id;
    @Override
    protected void getParams(Intent intent) {
        super.getParams(intent);
        id=intent.getStringExtra("id");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_bbprice_details_layout;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_bbprice_details, BBpriceDetailsFragment.newInstance(id,1));
        transaction.commit();
    }

}
