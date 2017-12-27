package com.knms.shop.android.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.adapter.IndexAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/5.
 * 维修师傅首页
 */
public class HomeRepairFragment extends BaseFragment {
    PullToRefreshRecyclerView pullToRefreshScrollView;
    RecyclerView recyclerView;
    RelativeLayout rl_status;
    IndexAdapter adapter;
    int pageNum = 1;
    Subscription subscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_repair, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        pullToRefreshScrollView = (PullToRefreshRecyclerView) view.findViewById(R.id.refresh_scrollView);
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        rl_status = (RelativeLayout) view.findViewById(R.id.rl_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(getmActivity()));
        adapter = new IndexAdapter();
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                reqApi();
            }
        });
        recyclerView.setAdapter(adapter);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageNum = 1;
                reqApi();
            }
        });
        pullToRefreshScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshScrollView.setRefreshing();
            }
        }, 1000);
    }

    @Override
    public void reqApi() {
        subscription = RxRequestApi.getInstance().getApiService().getIndexData(pageNum,5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<ResponseBody<List<BBprice>>>() {
                    @Override
                    public void call(ResponseBody<List<BBprice>> body) {
                        pullToRefreshScrollView.onRefreshComplete();
                        if (body.isSuccess()) {
                            updateView(body.data);
                            pageNum++;
                        } else {
                            Tst.showToast(body.desc);
                            if (1 != pageNum) adapter.loadMoreEnd();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pullToRefreshScrollView.onRefreshComplete();
                        Tst.showToast(throwable.toString());
                        if (1 != pageNum) adapter.loadMoreEnd();
                    }
                });
    }

    @Override
    protected String umTitle() {
        return "首页";
    }

    private void updateView(List<BBprice> data) {
        if (pageNum == 1) {
            if(null!= data && data.size() > 0){
                adapter.setNewData(data);
            }else {
                adapter.setEmptyView(CommonHelper.getEmptyView(getmActivity()));
            }
        } else {
            if (null != data && data.size() > 0) {
                adapter.addData(data);
                adapter.loadMoreComplete();
            }else {
                adapter.loadMoreEnd();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null) subscription.unsubscribe();
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    public static BaseFragment newInstance() {
        HomeRepairFragment homeFragment = new HomeRepairFragment();
        return homeFragment;
    }

    @Subscribe(tags = {@Tag(BusAction.ACTION_REFRESH_HOME)})
    public void refreshData(String str) {
        recyclerView.scrollToPosition(0);
        pullToRefreshScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshScrollView.setRefreshing();
            }
        }, 500);
    }
}
