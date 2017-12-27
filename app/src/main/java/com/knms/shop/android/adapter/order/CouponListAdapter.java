package com.knms.shop.android.adapter.order;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.order.OrderDetail;

/**
 * Created by 654654 on 2017/9/26.
 */

public class CouponListAdapter extends BaseQuickAdapter<OrderDetail.CouponList,BaseViewHolder> {
    public CouponListAdapter() {
        super(R.layout.item_coupon_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetail.CouponList item) {
        helper.setText(R.id.title,item.title);
        helper.setText(R.id.money,item.money);
        helper.setText(R.id.indate,item.startTime +"-"+item.endTime);
        helper.setVisible(R.id.gettime,!TextUtils.isEmpty(item.dateGainType))
                .setText(R.id.gettime,TextUtils.isEmpty(item.dateGainType)?"":item.dateGainType);
        helper.setText(R.id.count, TextUtils.isEmpty(item.quantity) || "1".equals(item.quantity)? "" :"("+item.quantity+"张)");
        helper.setText(R.id.about,item.explain);

    }
}
