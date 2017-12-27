package com.knms.shop.android.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.order.OrderDetailActivity;
import com.knms.shop.android.adapter.order.OrderListAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.OrderListData;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.CommonRecyclerFragment;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.ui.StickyDecoration;
import com.knms.shop.android.util.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 订单列表(待回复除外)
 * Created by 654654 on 2017/4/30.
 */

public class OrderListFragment extends CommonRecyclerFragment {

    private int state = 0;
    private Subscription subscription;
    private OrderListAdapter adapter;
    private boolean isStcky = true;
    private String shopId;

    public static OrderListFragment newInstance(int state){
        OrderListFragment fragment =  new OrderListFragment();
        fragment.setState(state);
        return fragment;
    }

    public void setState(int iState){
        state = iState;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopId = SPUtils.getUser().shopid;
        pageNum = 1;
    }

    @Override
    public String getTitle() {
        String title;
        switch (state){
            case 0:
                title = "全部";
                break;
            case 1:
                title = "待送货";
                break;
            case 2:
                title = "待收货";
                break;
            case 3:
                title = "待评价";
                break;
            default:
                title = "全部";
        }
        return title;
    }

    private void initData() {
        pullToRefresh.isStick(isStcky,getResources().getDimensionPixelSize(R.dimen.sticky_height));
        recyclerView.addItemDecoration(new StickyDecoration(getContext(), new StickyDecoration.DecorationCallback() {
            @Override
            public String getTagName(int position) {
                if (null == adapter) return "";
                List<OrderListData> data = adapter.getData();
                if (null == data || data.size() == 0) return "";
                if (position >= data.size()) position = data.size() - 1;
                OrderListData item = data.get(position);
                if (null == item) return "";
                String time = 1 == state ?/* 待送货以送货时间排序 */ TextUtils.isEmpty(item.delayTime) ? item.ordeliverytime:item.delayTime:/* 其它状态以订单创建时间排序 */item.orcreated;
                if (!TextUtils.isEmpty(time)) {
                    time = time.substring(0, time.lastIndexOf("-"));
                    time = time.replace("-", "年")+"月";
                }
                return time;
            }

            @Override
            public boolean isSticky() {
                return isStcky;
            }
        }));
        adapter = new OrderListAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String event;
                switch (state){
                    case 0:
                        event = "orderItemAll";
                        break;
                    case 1:
                        event = "orderItemDelivery";
                        break;
                    case 2:
                        event = "orderItemHarvest";
                        break;
                    case 3:
                        event = "orderItemReviews";
                        break;
                    default:
                        event = "orderItemAll";
                }
                MobclickAgent.onEvent(getActivity(),event);
                OrderListData item = (OrderListData) adapter.getItem(position);
                if (null == item)
                    return;
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put("orderId",item.orid);
//                params.put("orderTime",TextUtils.isEmpty(item.delayTime)? item.ordeliverytime : item.delayTime);
                startActivityAnimGeneral(OrderDetailActivity.class,params);
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                reqApi();
            }
        },recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh.setRefreshing();
            }
        },500);
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void commentChang(String orderId){//回复评论
        if (null == adapter) return;
        if (0 == state){
            changData(orderId,5);
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_DELIVERY_TIME)})
    public void timeChang(String alterTime){//延迟送货成功
        if (0 == state || 1 == state){
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing();
                }
            },500);
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_COMPLETE)})
    public void completeChang(String orderId){//送货完成
        if (null == adapter) return;
        if (0 == state ){
            changData(orderId,2);
        }else if( 1 == state) {
            removeData(orderId);
        }else if (2 == state){//新增需要刷新 (订单详情接口没有延迟送货时间字段)
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing();
                }
            },500);
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_RETURNS)})
    public void returnsChang(String orderId){//客户退货成功
        if (null == adapter) return;
        if(0 == state){
            changData(orderId,0);
        }else if (1 == state){
            removeData(orderId);
        }
    }

    /** 改变订单某项的状态 */
    private void changData(String orderId,int state){
        int index = adapter.findOrderIndex(orderId);
        if (-1 == index)
            return;
        OrderListData item = adapter.getItem(index);
        item.deliverystate = state;
        adapter.changItemData(index,item);
    }
    /** 列表中删除数据 */
    private void removeData(String orderId){
        adapter.removeData(orderId);
        if (adapter.getData() == null || adapter.getData().size() < 1){
            isStcky = false;
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initData();
    }
    @Override
    public void reqApi() {
        subscription = RxRequestApi.getInstance().getApiService().getOrderList(shopId,state,pageNum).compose(this.<ResponseBody<List<OrderListData>>>applySchedulers())
                .subscribe(new Action1<ResponseBody<List<OrderListData>>>() {
                    @Override
                    public void call(ResponseBody<List<OrderListData>> body) {
                        if (body.isSuccess()) {
                            isStcky = true;
                            updateView(body.data);
                            pageNum++;
                        } else {
                            Tst.showToast(body.desc);
                            isStcky = false;
                            if (1 != pageNum) adapter.loadMoreEnd();
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (1 == pageNum) {
                            isStcky = false;
                            adapter.setEmptyView(R.layout.layout_order_empty);
                        }else {
                            adapter.loadMoreEnd();
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                });
    }

    private void updateView(List<OrderListData> data) {
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
