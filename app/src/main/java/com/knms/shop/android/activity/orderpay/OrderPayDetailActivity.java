package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.orderpay.OrderPayListGoodsAdapter;
import com.knms.shop.android.bean.body.orderpay.OrderPayBody;
import com.knms.shop.android.bean.body.orderpay.OrderPayDetailData;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 订单详情
 * Created by 654654 on 2017/7/24.
 */

public class OrderPayDetailActivity extends HeadBaseActivity {

    private ScrollView scroll_view;
    private TextView state,overdue;
    private LinearLayout showAddr;
    private TextView consignee,addrPhone,addr;
    private TextView storeName;
    private RecyclerView goods;
    private TextView distributionPrice,payPrice;
    private LinearLayout showRemark;
    private TextView remark;
    private TextView payPriceTitle;
    private LinearLayout showContact;
    private FrameLayout online,calling;
    private TextView orderNumber,payNumber,payWay,createTime;
    private LinearLayout showBottom;
    private TextView seeComplaint,completeDelivery;

    private OrderPayListGoodsAdapter adapter;
    private Subscription subscription;
    private String tradingId = "",userId = "",sTradingStatus = "";
    public static final String KEY_TRADINGID = "tradingId";
    public static final String KEY_TRADINGSTATUS = "tradingStatus";
    @Override
    protected int layoutResID() {
        return R.layout.activity_orderpay_detail;
    }

    @Override
    protected void initView() {
        scroll_view = findView(R.id.scroll_view);
        state = findView(R.id.state);
        overdue = findView(R.id.overdue);
        showAddr = findView(R.id.showAddr);
        consignee = findView(R.id.consignee);
        addrPhone = findView(R.id.addrPhone);
        addr = findView(R.id.addr);
        storeName = findView(R.id.storeName);
        goods = findView(R.id.goods);
        distributionPrice = findView(R.id.distributionPrice);
        payPrice = findView(R.id.payPrice);
        showRemark = findView(R.id.showRemark);
        remark = findView(R.id.remark);
        payPriceTitle = findView(R.id.payPriceTitle);
        showContact = findView(R.id.showContact);
        online = findView(R.id.online);
        calling = findView(R.id.calling);
        orderNumber = findView(R.id.orderNumber);
        payNumber = findView(R.id.payNumber);
        payWay = findView(R.id.payWay);
        createTime = findView(R.id.createTime);
        showBottom = findView(R.id.showBottom);
        seeComplaint = findView(R.id.seeComplaint);
        completeDelivery = findView(R.id.completeDelivery);

        showView(overdue,"100".equals(sTradingStatus));
        showView(showContact,true/*"100".equals(sTradingStatus)*/);

        if ("100".equals(sTradingStatus)){
            payPriceTitle.setText("需付款");
        }else {
            payPriceTitle.setText("实付款");
        }
        adapter = new OrderPayListGoodsAdapter();
        adapter.setFromDetail(true);
        adapter.setOnRefundClick(new OrderPayListGoodsAdapter.OnRefund() {
            @Override
            public void onRefundClick(String tradingCommodityId) {
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put(OrderPayRefundDetailActivity.KEY_TRADINGID,tradingId);
                params.put(OrderPayRefundDetailActivity.KEY_TRADINGCOMMODITYID,tradingCommodityId);
                startActivityAnimGeneral(OrderPayRefundDetailActivity.class,params);
            }
        });
//        goods.addItemDecoration(new ItemDecoration());
        goods.setLayoutManager(new FullyLinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        goods.setAdapter(adapter);

        seeComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goComplain();
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(scroll_view);
    }

    @Override
    protected void initData() {
        userId = SPUtils.getUser().id;
        reqApi();
    }

    @Override
    protected void getParmas(Intent intent) {
        super.getParmas(intent);
        if (null == intent)
            return;
        tradingId = intent.getStringExtra(KEY_TRADINGID);
        sTradingStatus = intent.getStringExtra(KEY_TRADINGSTATUS);
    }

    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(tradingId))
            return;
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("tradingId",tradingId+"");
        subscription = RxRequestApi.getInstance().getApiService().getOrderPayDetail(map).compose(this.<OrderPayBody<OrderPayDetailData>>applySchedulers())
                .subscribe(new Action1<OrderPayBody<OrderPayDetailData>>() {
                    @Override
                    public void call(OrderPayBody<OrderPayDetailData> body) {
                        if (!body.isSuccess()){
                            Tst.showToast(body.message);
                            return;
                        }
                        updataView(body.globalData.orderTradingBo);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("result","error === "+throwable.getMessage());
                    }
                });
    }
    private CountDownTimer countDownTimer;
    /** 倒计时 */
    private void startTick(int time){
        if (time < 1)
            return;
        if (null != countDownTimer) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(time * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                updateTickUi(seconds);
            }
            @Override
            public void onFinish() {
//                updateTickUi(0);
                state.setText("已关闭");
                overdue.setVisibility(View.GONE);
                RxBus.get().post(BusAction.ORDERPAY_CLOSE, tradingId);
            }
        }.start();
    }

    private void updateTickUi(long seconds) {
        if (View.GONE == overdue.getVisibility() || View.INVISIBLE == overdue.getVisibility())
            overdue.setVisibility(View.VISIBLE);
        overdue.setText(getTime(seconds));
    }

    private String getTime(long sec){
        if (1 > sec) return "订单已经关闭";
        long h=sec/3600;
        long m=(sec%3600)/60;
        long s=(sec%3600)%60;
        return formatTime(h)+ ":" + formatTime(m) + ":" + formatTime(s) +" 后订单将自动关闭";
    }
    private String formatTime(long t){
        return (t <10 ? "0"+t : t+"");
    }
    private void updataView(OrderPayDetailData.OrderTradingBo orderTradingBo) {
        if (null == orderTradingBo )
            return;
        state.setText(orderTradingBo.tradingStatusTitle);
        if ("100".equals(orderTradingBo.tradingStatus)) {
            updateTickUi(orderTradingBo.orderCountdown);
            startTick(orderTradingBo.orderCountdown);
            payPriceTitle.setText("需付款");
        }else {
            payPriceTitle.setText("实付款");
        }
        showView(showAddr,"1".equals(orderTradingBo.orderType));
        consignee.setText("收货人: "+orderTradingBo.mailingName);
        addrPhone.setText(orderTradingBo.mailingPhone);
        addr.setText("收货地址: "+orderTradingBo.mailingAddress);
        storeName.setText(orderTradingBo.shopName);
        adapter.setNewData(orderTradingBo.orderTradingCommoditys);
        if (TextUtils.isEmpty(orderTradingBo.tradingTransportMoneyTitle))
            orderTradingBo.tradingTransportMoneyTitle = "免配送费";
        distributionPrice.setText(orderTradingBo.tradingTransportMoneyTitle.contains("费")|| orderTradingBo.tradingTransportMoneyTitle.contains(ConstantObj.MONEY)? orderTradingBo.tradingTransportMoneyTitle : ConstantObj.MONEY + orderTradingBo.tradingTransportMoneyTitle);
        payPrice.setText(ConstantObj.MONEY +orderTradingBo.actualMoney);
        showView(showRemark,!TextUtils.isEmpty(orderTradingBo.buyerRemarks));
        showView(orderNumber,!TextUtils.isEmpty(orderTradingBo.tradingSerialid));
        showView(payNumber,!TextUtils.isEmpty(orderTradingBo.payOrderId));
        showView(payWay,!TextUtils.isEmpty(orderTradingBo.payTypeTitle));
        showView(createTime,!TextUtils.isEmpty(orderTradingBo.createTime));
        remark.setText(""+orderTradingBo.buyerRemarks);
        orderNumber.setText("订单编号: "+orderTradingBo.tradingSerialid);
        payNumber.setText("支付订单号: "+orderTradingBo.payOrderId);
        payWay.setText("支付方式: "+orderTradingBo.payTypeTitle);
        createTime.setText("创建时间: "+orderTradingBo.createTime);
        showView(seeComplaint,orderTradingBo.complaintCount > 0 || (0 == orderTradingBo.complaintCount && 2 == orderTradingBo.isComplaint));

        showView(completeDelivery,"300".equals(orderTradingBo.tradingStatus) || ("500".equals(orderTradingBo.tradingStatus) &&"1".equals(orderTradingBo.userAppraise) )||
                ("500".equals(orderTradingBo.tradingStatus) &&"1".equals(orderTradingBo.businessmenAppraise)));//完成送货 评价 按钮显现条件
        showView(showBottom,"300".equals(orderTradingBo.tradingStatus) ||"1".equals(orderTradingBo.userAppraise) ||
                "1".equals(orderTradingBo.businessmenAppraise) || 2 == orderTradingBo.isComplaint);
        completeDelivery.setTag(orderTradingBo.tradingId);
        if ("300".equals(orderTradingBo.tradingStatus)){
            completeDelivery.setText("送货完成");
            completeDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOver((String) v.getTag());
                }
            });
        }else if("500".equals(orderTradingBo.tradingStatus) && "0".equals(orderTradingBo.businessmenAppraise) && "1".equals(orderTradingBo.userAppraise)){
            completeDelivery.setText("回复评价");
            completeDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(OrderPayDetailActivity.this,"orderPayReply");
                    goComment();
                }
            });
        }else if("500".equals(orderTradingBo.tradingStatus) &&"1".equals(orderTradingBo.businessmenAppraise)){
            completeDelivery.setText("查看评价");
            completeDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(OrderPayDetailActivity.this,"orderPaycomment");
                    goComment();
                }
            });
        }
        tradingLocking = orderTradingBo.tradingLocking;
//        if ("1".equals(orderTradingBo.tradingLocking)){//订单锁定的时候直接隐藏
//            showBottom.setVisibility(View.GONE);
//        }
        if (!TextUtils.isEmpty(orderTradingBo.usid)) {
            online.setTag(orderTradingBo.usid);
            online.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String usid = (String) v.getTag();
                    CommonHelper.goChat(OrderPayDetailActivity.this,usid);
                }
            });
        }
        if (!TextUtils.isEmpty(orderTradingBo.mailingPhone)) {
            calling.setTag(orderTradingBo.mailingPhone);
            calling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = (String) v.getTag();
                    CommonHelper.onCallPhone(OrderPayDetailActivity.this,phone);
                }
            });
        }
    }

    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void onReply(String tradingId) {//回复评价
        reqApi();
    }
    /** 跳转投诉列表 */
    private void goComplain(){
        if (isRefund()) {
            Tst.showToast(OrderPayDetailData.TOASTLOACKMSG);
            return;
        }
        MobclickAgent.onEvent(OrderPayDetailActivity.this,"orderPayRefund");
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put(OderPayComplainListActivity.KEY_ORDERID,tradingId);
        startActivityAnimGeneral(OderPayComplainListActivity.class,params);
    }

    /** 跳转评价 */
    private void goComment(){
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put(OderPayCommentListActivity.KEY_ORDERID,tradingId);
        startActivityAnimGeneral(OderPayCommentListActivity.class,params);
    }

    private String tradingLocking;
    // 退款中
    private boolean isRefund(){
        return !TextUtils.isEmpty(tradingLocking) && "1".equals(tradingLocking);
    }
    /** 送货完成 */
    private void onOver(final String tradingId){
        if (TextUtils.isEmpty(tradingId))
            return;
        if (isRefund()) {
            Tst.showToast("该订单有商品正在退款，请退款完成后再进行操作");
            return;
        }
        DialogHelper.showPromptDialog(this, null, "确认送货完成", "取消", null, "确定", new DialogHelper.OnMenuClick() {
            @Override
            public void onLeftMenuClick() {}
            @Override
            public void onCenterMenuClick() {}
            @Override
            public void onRightMenuClick() {//确定
                postOver(tradingId);
            }
        });

    }
    private void postOver(final String tradingId){
        MobclickAgent.onEvent(OrderPayDetailActivity.this,"orderPayCompleteDelivery");
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("tradingId",tradingId+"");
        subscription = RxRequestApi.getInstance().getApiService().postOrderPayShip(map).compose(this.<OrderPayBody<OrderPayDetailData>>applySchedulers())
                .subscribe(new Action1<OrderPayBody<OrderPayDetailData>>() {
                    @Override
                    public void call(OrderPayBody<OrderPayDetailData> body) {
                        if (body.isSuccess()) {
                            if (null != body.globalData && null != body.globalData.orderTradingBo && "1".equals(body.globalData.orderTradingBo.tradingLocking))
                                Tst.showToast(OrderPayDetailData.TOASTLOACKMSG);
                            else {
                                if (null != body.globalData && null != body.globalData.orderTradingBo && "600".equals(body.globalData.orderTradingBo.tradingStatus))
                                    Tst.showToast("该订单已退款");
                                else
                                    Tst.showToast("送货完成");
                            }
                            /*if (null != body.globalData && null != body.globalData.orderTradingBo)
                                updataView(body.globalData.orderTradingBo);
                            else*/
                                RxBus.get().post(BusAction.ORDERPAY_COMPLETEDELIVERY, tradingId);
                        }else {
                            Tst.showToast(body.message);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Tst.showToast("error: "+throwable.getMessage());
                    }
                });
    }

    @Override
    protected String umTitle() {
        return "订单详情";
    }

    /** 显示隐藏控件 */
    private void showView(View view,boolean isShow){
        view.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText(umTitle());
    }
    @Subscribe(tags = {@Tag(BusAction.ORDERPAY_COMPLETEDELIVERY)})
    public void refreshDetail(String tradingId){//客户送货成功
        reqApi();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != subscription)
            subscription.unsubscribe();
        if (null != countDownTimer)
            countDownTimer.cancel();
    }
}
