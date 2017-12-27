package com.knms.shop.android.adapter.order;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.order.OrderListData;

import java.util.ArrayList;

/**
 * 订单列表(待回复除外)
 * Created by 654654 on 2017/4/30.
 */

public class OrderListAdapter extends BaseQuickAdapter<OrderListData,BaseViewHolder> {

    public OrderListAdapter() {
        super(R.layout.item_order_list, null);
    }
    public void changItemData(int index,OrderListData data){
        if (1 > getItemCount())
            return;
        mData.set(index,data);
        notifyItemChanged(index);
    }
    public void addDataTop(OrderListData data){
        if (null == data)
            return;
        if (null == mData)
            mData = new ArrayList<>();
        mData.add(0,data);
        notifyItemInserted(0);
    }
    public synchronized void removeData(String orderId){
        if (null == mData) return;
        int index = findOrderIndex(orderId);
        if (-1 == index ) return;
        remove(index);
        if (mData.size() < 1) {
            setEmptyView(R.layout.layout_order_empty);
        }
    }
    public int findOrderIndex(String orderId){
        if (TextUtils.isEmpty(orderId) || null == mData || mData.size() < 1)
            return -1;
        int len = mData.size();
        for (int i = 0;i < len; i++){
            OrderListData item = mData.get(i);
            if (null != item && orderId.equals(item.orid)){
                return i;
            }
        }
        return -1;
    }
    @Override
    protected void convert(BaseViewHolder helper, OrderListData item) {
        TextView id = helper.getView(R.id.id);
        TextView state = helper.getView(R.id.state);
        ImageView complain = helper.getView(R.id.complain);
        TextView orderTime = helper.getView(R.id.orderTime);
        TextView deliveryTime = helper.getView(R.id.deliveryTime);
        id.setText(item.orcontractno);
        String sState;
        switch (item.deliverystate){
            case 0:
                sState = "客户退货";
                break;
            case 1:
                sState = "待送货";
                break;
            case 2:
                sState = "待收货";
                break;
            case 3:
                sState = "待评价";
                break;
            case 6:
                sState = "系统默认收货";
                break;
            case 4:
                sState = "待回复";
                break;
            case 5:
                sState = "已回复";
                break;
            default:
                sState = "待送货";
        }
        state.setText(sState);
        if (2 == item.isComplaint){
            complain.setVisibility(View.VISIBLE);
        }else {
            complain.setVisibility(View.GONE);
        }
        orderTime.setText(item.orcreated);
        deliveryTime.setText(item.ordeliverytime);

        LinearLayout showAlterTime = helper.getView(R.id.showAlterTime);
        if (TextUtils.isEmpty(item.delayTime)){
            showAlterTime.setVisibility(View.GONE);
        }else {
            showAlterTime.setVisibility(View.VISIBLE);
            //送货时间调整
            TextView alterTime = helper.getView(R.id.alterTime);
            alterTime.setText(item.delayTime);
        }
    }
}
