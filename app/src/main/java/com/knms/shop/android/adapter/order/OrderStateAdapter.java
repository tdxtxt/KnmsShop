package com.knms.shop.android.adapter.order;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.order.OrderState;
import com.knms.shop.android.util.CalendarUtils;

import java.util.List;


/**
 * Created by Administrator on 2017/4/25.
 */

public class OrderStateAdapter extends BaseQuickAdapter<OrderState.DeliveryListBean, BaseViewHolder> {
    private String[] orderState = {"客户退货", "生成订单，待送货", "延期送货", "送货完成", "确认收货", "客户评价", "商家回复"};


    public OrderStateAdapter(List<OrderState.DeliveryListBean> data) {
        super(R.layout.item_order_state_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderState.DeliveryListBean item) {
        if(helper.getAdapterPosition()==0){
            helper.getView(R.id.view_1).setVisibility(View.INVISIBLE);
            helper.setTextColor(R.id.orderstate,Color.parseColor("#000000"))
            .setImageResource(R.id.iv_left_state_icon,R.drawable.icon_2);
        }else{
            helper.getView(R.id.view_1).setVisibility(View.VISIBLE);
            helper.setTextColor(R.id.orderstate,Color.parseColor("#999999"))
            .setImageResource(R.id.iv_left_state_icon,R.drawable.bg_oval_gray);
        }
        if (null != mData && mData.size() -1 == helper.getAdapterPosition()){
            helper.getView(R.id.line).setVisibility(View.INVISIBLE);
        }
        helper .setText(R.id.orderstate, orderState[item.orderstate] + ( TextUtils.isEmpty(item.delaytime) ? "" : "(" + CalendarUtils.getDataFormat(item.delaytime) + ")"))
                .setText(R.id.time, item.created)
                .setText(R.id.remark,TextUtils.isEmpty(item.delayRemark) ? "" : item.delayRemark)
                .setVisible(R.id.remark, TextUtils.isEmpty(item.delayRemark) ? false : true);
    }
}
