package com.knms.shop.android.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.details.style.DecorationStyleDetailsActivity;
import com.knms.shop.android.adapter.mine.ShopDecorationStyleAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.DeStyle;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/3/20.
 */

public class ShopDecorationStyleFragment extends BaseFragment {
    PullToRefreshRecyclerView pullToRefresh;
    RecyclerView recyclerView;
    ShopDecorationStyleAdapter adapter;
    String shopId;
    int pageNum = 1;
    FullyLinearLayoutManager linearLayoutManager;
    ImageButton btnTop;

    public static ShopDecorationStyleFragment newInstance(String shopId) {
        ShopDecorationStyleFragment fragment = new ShopDecorationStyleFragment();
        Bundle args = new Bundle();
        args.putString("shopId", shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getString("shopId");
        }
    }
    @Override
    protected String umTitle() {
        return "家居风格";
    }

    @Override
    public String getTitle() {
        return "家居风格";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_recyclerview, null);
        pullToRefresh = findView(view,R.id.pullList);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = pullToRefresh.getRefreshableView();
        btnTop=findView(view,R.id.btn_top);
        linearLayoutManager=new FullyLinearLayoutManager(getmActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageNum = 1;
                reqApi();
            }

        });
//        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        recyclerView.setFocusable(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ShopDecorationStyleAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                reqApi();
            }
        },recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeStyle item = (DeStyle) adapter.getItem(position);
                Map<String,Object> params = new HashMap<String, Object>();
                List<DeStyle> lists = adapter.getData();
                List<String> ids = new ArrayList<String>();
                for (DeStyle desytyle : lists){
                    ids.add(desytyle.inid);
                }
                params.put("ids",ids);
                params.put("shopId",shopId);
                params.put("position",position);
                getmActivity().startActivityAnimGeneral(DecorationStyleDetailsActivity.class,params);
            }
        });
        pageNum = 1;
        reqApi();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getChildCount() == 0)
                    return;
                int firstVisibleItem =linearLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem < 2)
                    btnTop.setVisibility(View.GONE);
                else
                    btnTop.setVisibility(View.VISIBLE);
            }
        });
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutManager.scrollToPosition(0);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void reqApi() {
        RxRequestApi.getInstance().getApiService().getShopFurnitureStyle(shopId,pageNum).compose(this.<ResponseBody<List<DeStyle>>>applySchedulers())
                .subscribe(new Action1<ResponseBody<List<DeStyle>>>() {
                    @Override
                    public void call(ResponseBody<List<DeStyle>> body) {
                        pullToRefresh.onRefreshComplete();
                        if (body.isSuccess()) {
                            updateView(body.data);
                            pageNum++;
                        } else {
                            Tst.showToast(body.desc);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pullToRefresh.onRefreshComplete();
                    }
                });
    }
    private void updateView(List<DeStyle> data) {
        if (data == null) return;
        if (pageNum == 1) {//刷新的数据
            if (data.size() > 0) {
                adapter.setNewData(data);
            } else {
                adapter.setEmptyView(R.layout.layout_view_no_data);
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
}
