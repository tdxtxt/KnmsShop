package com.knms.shop.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.main.HomeShopFragment;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.adapter.IndexAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.fragment.base.LazyLoadFragment;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/21.
 * 商家首页子界面店内相似&个性求购
 */

public class HomePageFragment extends LazyLoadFragment {

    PullToRefreshRecyclerView pullToRefreshScrollView;
    RecyclerView recyclerView;
    RelativeLayout rl_status;
    IndexAdapter adapter;
    int pageNum = 1;
    Subscription subscription;
    int type = 5;//2：个性比比价，5：内部求购比比货
    boolean isFirst = true;//是否第一次加载

    public static HomePageFragment newInstance(int type) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putInt("type",type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected String umTitle() {
        return "";
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        type = getArguments().getInt("type");
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void lazyLoad() {//个性求购需要懒加载
        if(type == 2){
            if(isFirst)
            pullToRefreshScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshScrollView.setRefreshing();
                    isFirst = false;
                }
            }, 500);
        }
    }
    @Override
    protected void initView(View view) {
        pullToRefreshScrollView = (PullToRefreshRecyclerView) view.findViewById(R.id.refresh_scrollView);
        recyclerView = pullToRefreshScrollView.getRefreshableView();
        recyclerView.setFocusable(false);
        rl_status = (RelativeLayout) view.findViewById(R.id.rl_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(getmActivity()));
        adapter = new IndexAdapter("shop");
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
        if(type == 5){//店内相似直接刷新,个性求购需要懒加载
            pullToRefreshScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshScrollView.setRefreshing();
                }
            }, 500);
        }
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.chad.library.adapter.base.BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getActivity(),type==2?"enterPersonalityBuyDetail":"enterShopSimilarityDetail");
                BBprice data = (BBprice) adapter.getItem(position);
                ((MainActivity)getActivity()).putBBpriceDetails(data);
            }
        });
    }

    @Override
    public void reqApi() {
        //仅在用户为商家时传 2：个性比比价，5：内部求购比比货
        subscription = RxRequestApi.getInstance().getApiService().getIndexData(pageNum,type)
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

    private void updateView(List<BBprice> data) {
        if(pageNum == 1) refresh();
//        if (data == null) return;
        if (pageNum == 1) {
            if(null!= data && data.size() > 0){
                setLastTime(data.get(0).time);
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
    private void refresh(){
        if(getFragmentManager().getFragments() != null){
            for (Fragment fragment: getFragmentManager().getFragments()) {
                if(fragment instanceof HomeShopFragment) {
                    HomeShopFragment parent = (HomeShopFragment) fragment;
                    parent.setRedState(type,0);
                    break;
                }
            }
        }
    }
    private void setLastTime(String time){
        if(getFragmentManager().getFragments() != null){
            for (Fragment fragment: getFragmentManager().getFragments()) {
                if(fragment instanceof HomeShopFragment){
                    HomeShopFragment parent = (HomeShopFragment) fragment;
                    if(type == 5){
                        parent.knmsLastTime = time;
                    }else if(type == 2){
                        parent.myLastTime = time;
                    }
                    break;
                }
            }
        }
    }
    public void refreshData(int type) {
        if(this.type == type){
            recyclerView.scrollToPosition(0);
            pullToRefreshScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshScrollView.setRefreshing();
                }
            }, 500);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        if (subscription != null) subscription.unsubscribe();
    }
}
