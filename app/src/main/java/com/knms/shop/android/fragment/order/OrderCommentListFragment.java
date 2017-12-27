package com.knms.shop.android.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.order.ReplyCommentActivity;
import com.knms.shop.android.activity.order.UserCommentActivity;
import com.knms.shop.android.adapter.order.OrderCommentAdapter;
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

import rx.functions.Action1;

/**
 * 待回复
 * Created by 654654 on 2017/4/30.
 */

public class OrderCommentListFragment extends CommonRecyclerFragment {
    private OrderCommentAdapter adapter;
    private boolean isStcky = true;
    private String shopId;
    public static OrderCommentListFragment newInstance(){
        return new OrderCommentListFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopId = SPUtils.getUser().shopid;
        pageNum = 1;
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
                String time = data.get(position).orderComment.created;
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
        adapter = new OrderCommentAdapter(new OrderCommentAdapter.OnReplyClick() {
            @Override
            public void onClick(String orderId,String commentId) {
                MobclickAgent.onEvent(getActivity(),"orderItemReply");
                HashMap<String,Object> params = new HashMap<>();
                params.put("orderId",orderId);
//                startActivityAnimGeneral(UserCommentActivity.class,params);
                params.put("commentId",commentId);
                params.put("fromList",true);
                startActivityAnimGeneral(ReplyCommentActivity.class,params);
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
                OrderListData item = (OrderListData) adapter.getItem(position);
                if (null == item || TextUtils.isEmpty(item.orid))
                    return;
                HashMap<String,Object> params = new HashMap<String,Object>();
                params.put("orderId",item.orid);
                startActivityAnimGeneral(UserCommentActivity.class,params);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh.setRefreshing();
            }
        },500);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initData();
    }

    @Override
    public void reqApi() {
        RxRequestApi.getInstance().getApiService().getOrderList(shopId,4,pageNum).compose(this.<ResponseBody<List<OrderListData>>>applySchedulers())
                .subscribe(new Action1<ResponseBody<List<OrderListData>>>() {
                    @Override
                    public void call(ResponseBody<List<OrderListData>> body) {
                        if (body.isSuccess()) {
                            isStcky = true;
                            updateView(body.data);
                            pageNum++;
                        } else {
                            isStcky = false;
                            Tst.showToast(body.desc);
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
                        }
                        if (1 != pageNum) adapter.loadMoreEnd();
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

    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void commentChang(String orderId){
        if (null != adapter) {
            adapter.removeData(orderId);
            if (adapter.getData().size() < 1){
                isStcky = false;
            }
        }
    }
    @Override
    public String getTitle() {
        return "待回复";
    }

    @Override
    protected String umTitle() {
        return "待回复";
    }
}
