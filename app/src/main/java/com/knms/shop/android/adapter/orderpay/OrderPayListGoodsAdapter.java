package com.knms.shop.android.adapter.orderpay;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.orderpay.OrderTradingCommoditys;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.LocalDisplay;

/**
 * Created by 654654 on 2017/8/22.
 */

public class OrderPayListGoodsAdapter extends BaseQuickAdapter<OrderTradingCommoditys,BaseViewHolder> {

    private OnRefund onRefundClick;
    private boolean fromDetail = false;
    public OrderPayListGoodsAdapter() {
        super(R.layout.include_orderpay_goods);
    }

    public void setFromDetail(boolean fromDetail) {
        this.fromDetail = fromDetail;
    }

    public void setOnRefundClick(OnRefund onRefundClick) {
        this.onRefundClick = onRefundClick;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderTradingCommoditys item) {
        ImageView icon_goods = helper.getView(R.id.icon_goods);
        TextView goodsName = helper.getView(R.id.goodsName);
        TextView sellPrice = helper.getView(R.id.sellPrice);
        TextView originalPrice = helper.getView(R.id.originalPrice);
        TextView attrs = helper.getView(R.id.attrs);
        TextView number = helper.getView(R.id.number);
        TextView refund = helper.getView(R.id.refund);
        ImageLoadHelper.getInstance().displayImage(item.specificationImg, icon_goods, LocalDisplay.dp2px(160), LocalDisplay.dp2px(160));
        goodsName.setText(item.showName);
        sellPrice.setText(ConstantObj.MONEY + (TextUtils.isEmpty(item.realityMoney)?"0":item.realityMoney));
        originalPrice.setText(ConstantObj.MONEY + (TextUtils.isEmpty(item.showMoney)?"0":item.showMoney));
        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
        attrs.setText(item.parameterBriefing);
        number.setText("x"+item.buyNumber);
        if (!fromDetail) {
            if (!TextUtils.isEmpty(item.specificationStatusTitle))
                refund.setVisibility(View.VISIBLE);
            refund.setText(item.specificationStatusTitle);
        }else {
            if (!TextUtils.isEmpty(item.tradingCommodityType) && ("2".equals(item.tradingCommodityType) || "3".equals(item.tradingCommodityType))) {
                refund.setVisibility(View.VISIBLE);
                refund.setText("查看退款");
                refund.setPadding(15,5,15,5);
                refund.setBackgroundResource(R.drawable.bg_border_yellow_rectangle);
                refund.setTag(item.tradingCommodityId);
                refund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tradingCommodityId = (String) v.getTag();
                        if (TextUtils.isEmpty(tradingCommodityId))
                            return;
                        if (null != onRefundClick)
                            onRefundClick.onRefundClick(tradingCommodityId);
                    }
                });
            }else {
                refund.setVisibility(View.INVISIBLE);
            }
        }
    }
    private String parseInt(String x){
        if(TextUtils.isEmpty(x))
            return "0";
        return x.substring(0,x.indexOf("."));
    }
    public interface OnRefund{
        public void onRefundClick(String tradingCommodityId);
    }
}
