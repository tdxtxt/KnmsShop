package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.activity.order.ReplyCommentActivity;
import com.knms.shop.android.adapter.orderpay.OrderPayCommentListAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.orderpay.OrderPayCommentListData;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.net.RxRequestApi;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 评论列表
 * Created by 654654 on 2017/8/31.
 */

public class OderPayCommentListActivity extends HeadBaseActivity {

    private PullToRefreshRecyclerView pullToRefresh;
    private TextView replay;
    private OrderPayCommentListAdapter adapter;
    protected RecyclerView recyclerView;
    private Subscription subscription;
    private String orderId= "";
    private String sCommentId="";
    public static final String KEY_ORDERID = "orderId";
    @Override
    protected int layoutResID() {
        return R.layout.activity_orderpay_recycler_list;
    }

    @Override
    protected void initView() {
        replay = findView(R.id.replay);
        pullToRefresh = findView(R.id.pullToRefreshRecyclerView);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        adapter = new OrderPayCommentListAdapter(new OrderPayCommentListAdapter.OnReplyClick() {
            @Override
            public void onClick(String commentId) {
                if (TextUtils.isEmpty(commentId))
                    return;
//                MobclickAgent.onEvent(OderPayCommentListActivity.this,"orderPayReply");
                HashMap<String,Object> params = new HashMap<>();
                params.put("orderId",orderId);
                params.put("commentId",commentId);
                startActivityAnimGeneral(ReplyCommentActivity.class,params);
            }
        });
        recyclerView = pullToRefresh.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setFocusable(false);
        adapter.bindToRecyclerView(recyclerView);
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                reqApi();
            }

        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sCommentId))
                    return;
//                MobclickAgent.onEvent(OderPayCommentListActivity.this,"orderPayReply");
                HashMap<String,Object> params = new HashMap<>();
                params.put("orderId",orderId);
                params.put("commentId",sCommentId);
                startActivityAnimGeneral(ReplyCommentActivity.class,params);
            }
        });
        showImMsg(true);
    }

    @Override
    protected void initData() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh.setRefreshing();
            }
        },500);
    }

    @Override
    protected void getParmas(Intent intent) {
        super.getParmas(intent);
        if (null == intent)
            return;
        orderId = intent.getStringExtra(KEY_ORDERID);
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void onReply(String tradingId){
        initData();
    }
    @Override
    protected void reqApi() {
        sCommentId = "";
        if (TextUtils.isEmpty(orderId))
            return;
//        Map<String,Object> map=new HashMap<>();
//        map.put("orderId",orderId);
        subscription = RxRequestApi.getInstance().getApiService().getOrderPayCommentList(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<List<OrderPayCommentListData>>>() {
                    @Override
                    public void call(ResponseBody<List<OrderPayCommentListData>> listResponseBody) {
                        if (listResponseBody.isSuccess()){
                            adapter.setNewData(listResponseBody.data);
                            if (null != listResponseBody.data && listResponseBody.data.size() == 1 &&
                                    (null == listResponseBody.data.get(0).shopReply || TextUtils.isEmpty(listResponseBody.data.get(0).shopReply.content))){
                                sCommentId = listResponseBody.data.get(0).id;
                                replay.setVisibility(View.VISIBLE);
                            }else {
                                replay.setVisibility(View.GONE);
                            }
                        }else {
                            adapter.setEmptyView(R.layout.layout_order_empty);
                            replay.setVisibility(View.GONE);
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pullToRefresh.onRefreshComplete();
                        adapter.setEmptyView(R.layout.layout_order_empty);
                        replay.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != subscription)
            subscription.unsubscribe();
    }

    @Override
    protected String umTitle() {
        return "用户评价";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText(umTitle());
    }
}
