package com.knms.shop.android.adapter.orderpay;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.orderpay.OrderTradingBos;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by 654654 on 2017/8/22.
 */


public class OrderPayListAdapter extends BaseQuickAdapter<OrderTradingBos,BaseViewHolder> {

    private OnRightCLick onRightCLick;
    public OrderPayListAdapter(){
        super(R.layout.item_orderpay_list, null);
    }
    public void changItemData(int index,OrderTradingBos data){
        if (1 > getItemCount())
            return;
        mData.set(index,data);
        notifyItemChanged(index);
    }

    public void setOnRightCLick(OnRightCLick onRightCLick) {
        this.onRightCLick = onRightCLick;
    }

    public void addDataTop(OrderTradingBos data){
        if (null == data)
            return;
        if (null == mData)
            mData = new ArrayList<>();
        mData.add(0,data);
        notifyItemInserted(0);
    }
    public synchronized void removeData(String shopId){
        if (null == mData) return;
        int index = findOrderIndex(shopId);
        if (-1 == index ) return;
        remove(index);
        if (mData.size() < 1) {
            setEmptyView(R.layout.layout_order_empty);
        }
    }
    public int findOrderIndex(String shopId){
        if (TextUtils.isEmpty(shopId) || null == mData || mData.size() < 1)
            return -1;
        int len = mData.size();
        for (int i = 0;i < len; i++){
            OrderTradingBos item = mData.get(i);
            if (null != item && shopId.equals(item.shopId)){
                return i;
            }
        }
        return -1;
    }
    /** 修改商品锁定状态 (未测试)*/
    public void alterOrderLock(String shopId ,String tradingLocking,String showId,String tradingCommodityType,String specificationStatusTitle){
        int index = findOrderIndex(shopId);
        if ( -1 == index) return;
        OrderTradingBos item = mData.get(index);
        mData.get(index).tradingLocking =  tradingLocking;
        if (null != item.orderTradingCommoditys && !TextUtils.isEmpty(showId) && !TextUtils.isEmpty(tradingCommodityType)){
            for (int i = 0;i < item.orderTradingCommoditys.size();i++){
                if (item.orderTradingCommoditys.get(i).showId.equals(showId)){
                    item.orderTradingCommoditys.get(i).tradingCommodityType = tradingCommodityType;
                    item.orderTradingCommoditys.get(i).specificationStatusTitle = specificationStatusTitle;
                    break;
                }
            }
        }
        this.notifyDataSetChanged();
    }

    /** 更新某一项数据 */
   /* @Override
    public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //change item
            convert(holder,mData.get(position));
        }
    }*/

    @Override
    protected void convert(final BaseViewHolder helper, OrderTradingBos item) {
        TextView storeName =  helper.getView(R.id.storeName);
        TextView orderState =  helper.getView(R.id.orderState);
        RecyclerView goods =  helper.getView(R.id.goods);
        TextView totalPrice =  helper.getView(R.id.totalPrice);
        FrameLayout showBottom =  helper.getView(R.id.showBottom);
        TextView completeDelivery =  helper.getView(R.id.completeDelivery);
        ImageView complain =  helper.getView(R.id.complain);

        helper.getView(R.id.storeClick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        if (2 == item.isComplaint){
            complain.setVisibility(View.VISIBLE);
        }else {
            complain.setVisibility(View.GONE);
        }
        goods.setFocusable(false);//无效
        OrderPayListGoodsAdapter adapter = new OrderPayListGoodsAdapter();
        adapter.setOnItemClickListener(new OnItemClickListener() {//事件传递
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrderPayListAdapter.this.getOnItemClickListener().onItemClick(OrderPayListAdapter.this,helper.getView(R.id.goods),helper.getPosition());
            }
        });
        adapter.setNewData(item.orderTradingCommoditys);
//        goods.addItemDecoration(new ItemDecoration());
        goods.setLayoutManager(new FullyLinearLayoutManager(mContext));
        goods.setAdapter(adapter);

        storeName.setText(item.shopName);
        orderState.setText(item.tradingStatusTitle);
        if (TextUtils.isEmpty(item.tradingTransportMoneyTitle))
            item.tradingTransportMoneyTitle = "免配送费";
        item.tradingTransportMoneyTitle = item.tradingTransportMoneyTitle.contains("费") || item.tradingTransportMoneyTitle.contains(ConstantObj.MONEY) ? item.tradingTransportMoneyTitle : "含配送费 "+ ConstantObj.MONEY+" "+item.tradingTransportMoneyTitle;
        totalPrice.setText(Html.fromHtml("共"+item.effectiveNumber+"件商品，合计: <font color='#FB6161'>"+ConstantObj.MONEY+" "+item.actualMoney+"</font>（"+item.tradingTransportMoneyTitle+"）"));
        if ("300".equals(item.tradingStatus)){
            showBottom.setVisibility(View.VISIBLE);
            completeDelivery.setText("送货完成");
            setClick(completeDelivery,0,item.tradingId,item.tradingLocking);
        }else if ("500".equals(item.tradingStatus) ){
            if ("0".equals(item.businessmenAppraise) && "1".equals(item.userAppraise)) {
                completeDelivery.setText("回复评价");
                showBottom.setVisibility(View.VISIBLE);
                setClick(completeDelivery,1,item.tradingId,item.tradingLocking);
            }else if ("1".equals(item.businessmenAppraise)){
                completeDelivery.setText("查看评价");
                showBottom.setVisibility(View.VISIBLE);
                setClick(completeDelivery,2,item.tradingId,item.tradingLocking);
            }else {
                showBottom.setVisibility(View.GONE);
            }

        }else {
            showBottom.setVisibility(View.GONE);
        }
        /*if ("1".equals(item.tradingLocking)){//订单锁定的时候直接隐藏
            showBottom.setVisibility(View.GONE);
        }*/
    }

    private int parseInt(double x){
        return new Double(x).intValue();
    }
    private void setClick(View view, final int tag, String tradingId, final String tradingLocking){
        view.setTag(tradingId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == onRightCLick)
                    return;
                String stradingId = (String) v.getTag();
                if (0 == tag){//送货完成
                    onRightCLick.onCompleteDelivery(stradingId,tradingLocking);
                }else {//查看评价 && 回复评价
                    onRightCLick.onComment(stradingId,tag);
                }
            }
        });
    }

    public interface OnRightCLick{
        void onCompleteDelivery(String stradingId,String tradingLocking);
        void onComment(String stradingId,int falg);
    }
}
