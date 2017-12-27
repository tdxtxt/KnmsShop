package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.body.orderpay.OrderPayBody;
import com.knms.shop.android.bean.body.orderpay.RefundDetailData;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.SPUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 退款详情
 * Created by 654654 on 2017/8/30.
 */

public class OrderPayRefundDetailActivity extends HeadBaseActivity {

    private TextView state;
    private ImageView icon_goods;
    private TextView goodsName,attrs;
    private TextView reason,payNumber,createTime,makefor,trench;

    private Subscription subscription;
    public static final String KEY_TRADINGID = "tradingId";
    public static final String KEY_TRADINGCOMMODITYID = "tradingCommodityId";
    private String tradingId = "",userId = "",tradingCommodityId = "";
    @Override
    protected int layoutResID() {
        return R.layout.activity_orderpay_refund;
    }

    @Override
    protected void initView() {
        state = findView(R.id.state);
        icon_goods = findView(R.id.icon_goods);
        goodsName = findView(R.id.goodsName);
        attrs = findView(R.id.attrs);
        reason = findView(R.id.reason);
        payNumber = findView(R.id.payNumber);
        createTime = findView(R.id.createTime);
        makefor = findView(R.id.makefor);
        trench = findView(R.id.trench);
        showImMsg(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getParmas(Intent intent) {
        super.getParmas(intent);
        if (null == intent)
            return;
        tradingId = intent.getStringExtra(KEY_TRADINGID);
        tradingCommodityId = intent.getStringExtra(KEY_TRADINGCOMMODITYID);
    }

    @Override
    protected void initData() {
        userId = SPUtils.getUser().id;
        reqApi();
    }

    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(tradingId))
            return;
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("tradingId",tradingId+"");
        map.put("tradingCommodityId",tradingCommodityId+"");
        subscription = RxRequestApi.getInstance().getApiService().getRefundDetail(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderPayBody<RefundDetailData>>() {
                    @Override
                    public void call(OrderPayBody<RefundDetailData> refundDetailDataOrderPayBody) {
                        if (refundDetailDataOrderPayBody.isSuccess()){
                            updateView(refundDetailDataOrderPayBody.globalData);
                        }else {
                            Tst.showToast(refundDetailDataOrderPayBody.message);
                        }
                    }
                });
    }

    private void updateView(RefundDetailData data){
        ImageLoadHelper.getInstance().displayImage(data.orderTradingCommodityBo.specificationImg, icon_goods, LocalDisplay.dp2px(160), LocalDisplay.dp2px(160));
        goodsName.setText(data.orderTradingCommodityBo.showName);
        attrs.setText(data.orderTradingCommodityBo.parameterBriefing);
        if (null == data.orderRecedeBo)
            return;
        RefundDetailData.OrderRecedeBos goodsData = data.orderRecedeBo;
        if (null == goodsData)
            return;
        if ("1".equals(goodsData.recedeType)){
            state.setText("退款中");
        }else /*if("3".equals(goodsData.recedeType)){
//            state.setText("退款取消");
        }else if("4".equals(goodsData.recedeType))*/{
            state.setText("退款成功");
        }
        reason.setText("退款原因: "+goodsData.recedeReason);
        payNumber.setText("退款金额: "+ConstantObj.MONEY + goodsData.recedeMoney);
        createTime.setText("申请时间: "+goodsData.createTime);
        if (TextUtils.isEmpty(goodsData.recedeTo))
            makefor.setVisibility(View.GONE);
        else
            makefor.setVisibility(View.VISIBLE);
        makefor.setText("退款去向: "+goodsData.recedeTo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != subscription)
            subscription.unsubscribe();
    }

    @Override
    protected String umTitle() {
        return "退款详情";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText(umTitle());
    }
}
