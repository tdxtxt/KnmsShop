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
import com.knms.shop.android.activity.details.ProductDetailsActivity;
import com.knms.shop.android.activity.orderpay.OrderPayGoodsDetailActivity;
import com.knms.shop.android.adapter.mine.ShopProductAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.ShopCommodity;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;


/**
 * Created by Administrator on 2017/3/20.
 */

public class ShopProductFragment extends BaseFragment {
    private PullToRefreshRecyclerView pullToRefresh;
    RecyclerView recyclerView;
    ShopProductAdapter adapter;
    String shopId;
    int pageNum = 1;
    FullyGridLayoutManager gridLayoutManager;
    ImageButton btnTop;

    public static ShopProductFragment newInstance(String shopId) {
        ShopProductFragment fragment = new ShopProductFragment();
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
        return "所有商品";
    }

    @Override
    public String getTitle() {
        return "商品";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_recyclerview, null);
        pullToRefresh = findView(view,R.id.pullList);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = pullToRefresh.getRefreshableView();
        btnTop=findView(view,R.id.btn_top);
        recyclerView.setFocusable(false);
        gridLayoutManager=new FullyGridLayoutManager(getmActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);//类似gridview
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageNum = 1;
                reqApi();
            }

        });
//        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ShopProductAdapter();
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
                ShopCommodity item = (ShopCommodity) adapter.getItem(position);
                if (null == item)
                    return;
                Map<String,Object> map=new HashMap<String, Object>();
                Class cl ;
                if ("6".equals(item.gotype)){
                    cl = OrderPayGoodsDetailActivity.class;
                    map.put(OrderPayGoodsDetailActivity.KEY_SHOWID, item.goid);
                }else {
                    cl = ProductDetailsActivity.class;
                    map.put("goid", item.goid);
                }
                startActivityAnimGeneral(cl, map);
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
                int firstVisibleItem =gridLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem < 4)
                    btnTop.setVisibility(View.GONE);
                else
                    btnTop.setVisibility(View.VISIBLE);
            }
        });
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutManager.scrollToPosition(0);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void reqApi() {
        RxRequestApi.getInstance().getApiService().getShopProduct(shopId, pageNum).compose(this.<ResponseBody<List<ShopCommodity>>>applySchedulers())
                .subscribe(new Action1<ResponseBody<List<ShopCommodity>>>() {
                    @Override
                    public void call(ResponseBody<List<ShopCommodity>> body) {
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
    private void updateView(List<ShopCommodity> data) {
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
