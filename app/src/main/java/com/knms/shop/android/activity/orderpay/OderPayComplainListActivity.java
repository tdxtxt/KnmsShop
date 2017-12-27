package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.activity.order.CompainDetailActivity;
import com.knms.shop.android.adapter.order.ComplaintsAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.Complaints;
import com.knms.shop.android.net.RxRequestApi;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 投诉列表
 * Created by 654654 on 2017/8/31.
 */

public class OderPayComplainListActivity extends HeadBaseActivity {
    private PullToRefreshRecyclerView pullToRefresh;
    private ComplaintsAdapter adapter;
    protected RecyclerView recyclerView;
    private Subscription subscription;
    private String orderId= "";
    public static final String KEY_ORDERID = "orderId";
    @Override
    protected int layoutResID() {
        return R.layout.activity_orderpay_recycler_list;
    }

    @Override
    protected void initView() {
        pullToRefresh = findView(R.id.pullToRefreshRecyclerView);
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) pullToRefresh.getLayoutParams();
        llp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        pullToRefresh.setLayoutParams(llp);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        adapter = new ComplaintsAdapter(null);
        adapter.setOrderPay(true);
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
        recyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Complaints item = (Complaints) adapter.getItem(position);
                if (null == item )
                    return;
                goDetail(item.ocid);
            }
        });

        showImMsg(true);
    }

    /** 跳转详情 */
    private void goDetail(String ocid){
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put(CompainDetailActivity.KEY_COMPLAINTSID,ocid);
        startActivityAnimGeneral(CompainDetailActivity.class,params);
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
    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(orderId))
            return;
//        Map<String,Object> map=new HashMap<>();
//        map.put("orderId",orderId);
        subscription = RxRequestApi.getInstance().getApiService().getComplainList(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<List<Complaints>>>() {
                    @Override
                    public void call(ResponseBody<List<Complaints>> body) {
                        if (body.isSuccess()){
                            adapter.setNewData(body.data);
                        }else {
                            adapter.setEmptyView(R.layout.layout_order_empty);
                        }
                        pullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        adapter.setEmptyView(R.layout.layout_order_empty);
                        pullToRefresh.onRefreshComplete();
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
        return "投诉记录";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText(umTitle());
    }
}
