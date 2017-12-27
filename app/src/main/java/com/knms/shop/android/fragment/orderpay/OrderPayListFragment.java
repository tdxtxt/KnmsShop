package com.knms.shop.android.fragment.orderpay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.orderpay.OderPayCommentListActivity;
import com.knms.shop.android.activity.orderpay.OrderPayDetailActivity;
import com.knms.shop.android.adapter.orderpay.OrderPayListAdapter;
import com.knms.shop.android.bean.body.orderpay.GlobalData;
import com.knms.shop.android.bean.body.orderpay.OrderPayBody;
import com.knms.shop.android.bean.body.orderpay.OrderPayDetailData;
import com.knms.shop.android.bean.body.orderpay.OrderTradingBos;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.CommonRecyclerFragment;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 654654 on 2017/8/22.
 */

public class OrderPayListFragment extends CommonRecyclerFragment {
    private int state = 0;
    private Subscription subscription;
    private OrderPayListAdapter adapter;
    private String userId = "";

    public static OrderPayListFragment newInstance(int state){
        OrderPayListFragment fragment =  new OrderPayListFragment();
        fragment.setState(state);
        return fragment;
    }

    public void setState(int state) {
        this.state = state;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = SPUtils.getUser().id;
        pageNum = 1;
    }
    @Override
    protected void initView(View view) {
        super.initView(view);
        initData();
    }
    @Override
    public String getTitle() {
        String title;
        switch (state){
            case 0:
                title = "全部";
                break;
            case 1:
                title = "待付款";
                break;
            case 2:
                title = "待送货";
                break;
            case 3:
                title = "待收货";
                break;
            case 4:
                title = "待评价";
                break;
            case 5:
                title = "待回复";
                break;
            default:
                title = "全部";
        }
        return title;
    }
    private void initData() {
        adapter = new OrderPayListAdapter();
        adapter.setOnRightCLick(new OrderPayListAdapter.OnRightCLick() {
            @Override
            public void onCompleteDelivery(String stradingId ,String tradingLocking) {
                if (!TextUtils.isEmpty(tradingLocking) && "1".equals(tradingLocking)) {
                    Tst.showToast(OrderPayDetailData.TOASTLOACKMSG);
                    return;
                }
                MobclickAgent.onEvent(getActivity(),"orderPayCompleteDelivery");
                onOver(stradingId);
            }
            @Override
            public void onComment(String stradingId,int flag) {
                String event;
                if (1 == flag){
                    event = "orderPayReply";
                }else {
                    event = "orderPaycomment";
                }
                MobclickAgent.onEvent(getActivity(),event);
                goComment(stradingId);
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                reqApi();
            }
        },recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getActivity(),"orderPayItemAll");
                OrderTradingBos itemdata = (OrderTradingBos) adapter.getItem(position);
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put(OrderPayDetailActivity.KEY_TRADINGID,itemdata.tradingId);
                params.put(OrderPayDetailActivity.KEY_TRADINGSTATUS,itemdata.tradingStatus);
                startActivityAnimGeneral(OrderPayDetailActivity.class,params);
            }
        });
        recyclerView.setAdapter(adapter);
        refreshData();
    }

    @Override
    public void reqApi() {
        if (TextUtils.isEmpty(userId))
            return;
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("orderTypeStatus",state+"");
        map.put("pageIndex",pageNum);
        subscription = RxRequestApi.getInstance().getApiService().getOrderPayList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderPayBody<GlobalData>>() {
                    @Override
                    public void call(OrderPayBody<GlobalData> body) {
                        if (body.isSuccess()) {
                            updateView(body.globalData.orderTradingBos);
                            pageNum++;
                        } else {
                            Tst.showToast(body.message);
                            if (1 != pageNum) adapter.loadMoreEnd();
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (1 == pageNum) {
                            adapter.setEmptyView(R.layout.layout_order_empty);
                        }else {
                            adapter.loadMoreEnd();
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                });
    }
    /** 跳转评价 */
    private void goComment(String tradingId){
        MobclickAgent.onEvent(getActivity(),"orderPaycomment");
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put(OderPayCommentListActivity.KEY_ORDERID,tradingId);
        startActivityAnimGeneral(OderPayCommentListActivity.class,params);
    }
    private void updateView(List<OrderTradingBos> data) {
        if (pageNum == 1) {//刷新的数据
            if (data != null && data.size() > 0) {
                adapter.setNewData(data);
            } else {
                adapter.setEmptyView(R.layout.layout_order_empty);
            }
        } else {//加载更多的数据
            if (data != null && data.size() > 0) {
                adapter.addData(data);
                adapter.loadMoreComplete();
            }else {
                adapter.loadMoreEnd();
            }
        }
    }

    /** 送货完成 */
    private void onOver(final String tradingId){
        if (TextUtils.isEmpty(tradingId))
            return;
        DialogHelper.showPromptDialog(getActivity(), null, "确认送货完成", "取消", null, "确定", new DialogHelper.OnMenuClick() {
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
        Map<String,Object> map=new HashMap<>();
        map.put("userId", SPUtils.getUser().id);
        map.put("tradingId",tradingId+"");
        RxRequestApi.getInstance().getApiService().postOrderPayShip(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderPayBody<OrderPayDetailData>>() {
                    @Override
                    public void call(OrderPayBody<OrderPayDetailData> body) {
                        if (body.isSuccess()) {
                            if (null != body.globalData && null != body.globalData.orderTradingBo && "1".equals(body.globalData.orderTradingBo.tradingLocking)){
                                Tst.showToast(OrderPayDetailData.TOASTLOACKMSG);
                            }else {
                                if (null != body.globalData && null != body.globalData.orderTradingBo && "600".equals(body.globalData.orderTradingBo.tradingStatus))
                                    Tst.showToast("该订单已退款");
                                else
                                    Tst.showToast("送货完成");
                            }
//                            adapter.alterOrderLock(body.globalData.orderTradingBo.shopId,"1",
//                                    body.globalData.orderTradingBo.orderTradingCommoditys.get(body.globalData.orderTradingBo.orderTradingCommoditys.size() -1).showId
//                                    ,"2","退款中");
                            RxBus.get().post(BusAction.ORDERPAY_COMPLETEDELIVERY, tradingId);
                        }else {
                            Tst.showToast(body.message);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("result",""+throwable.getMessage());
                        Tst.showToast("error: "+throwable.getMessage());
                    }
                });
    }
    @Subscribe(tags = {@Tag(BusAction.ORDERPAY_COMPLETEDELIVERY)})
    public void refreshCompleteDelivery(String tradingId){//客户送货成功
        if (null == adapter) return;
        if(0 == state || 2 == state){
            refreshData();
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDERPAY_CLOSE)})
    public void refreshClose(String tradingId){//客户送货成功
        if (null == adapter) return;
        if(0 == state || 1 == state){
            refreshData();
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void onReply(String tradingId){//回复评价
        if (null == adapter) return;
        if(0 == state || 5 == state){
            refreshData();
        }
    }

    public void refreshData(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh.setRefreshing();
            }
        },500);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) subscription.unsubscribe();
    }
    @Override
    protected String umTitle() {
        return getTitle();
    }
}
