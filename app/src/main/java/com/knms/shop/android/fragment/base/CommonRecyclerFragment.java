package com.knms.shop.android.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.view.clash.FzLinearLayoutManager;

/**
 * Created by 654654 on 2017/4/30.
 */

public class CommonRecyclerFragment extends LazyLoadFragment {
    protected PullToRefreshRecyclerView pullToRefresh;
    protected RecyclerView recyclerView;
    protected ImageButton btnTop;
    protected FzLinearLayoutManager linearLayoutManager;
    /** 页数 */
    protected int pageNum = 1;
    @Override
    protected String umTitle() {
        return "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

 /*   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_recyclerview, null);
        pullToRefresh = findView(view,R.id.pullList);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = pullToRefresh.getRefreshableView();
        btnTop = findView(view,R.id.btn_top);
        linearLayoutManager=new FzLinearLayoutManager(getmActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
//        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        recyclerView.setFocusable(false);
        //去掉动画防止notifyItemchanged闪屏
//        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getItemAnimator().setChangeDuration(0);
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutManager.scrollToPosition(0);
            }
        });
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageNum = 1;
                reqApi();
            }

        });
        return view;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        pullToRefresh.onRefreshComplete();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        pullToRefresh = findView(view,R.id.pullList);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = pullToRefresh.getRefreshableView();
        btnTop = findView(view,R.id.btn_top);
        linearLayoutManager=new FzLinearLayoutManager(getmActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
//        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        recyclerView.setFocusable(false);
        //去掉动画防止notifyItemchanged闪屏
//        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getItemAnimator().setChangeDuration(0);
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutManager.scrollToPosition(0);
            }
        });
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageNum = 1;
                reqApi();
            }

        });
    }

    protected void setShowTop(){
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
    }

    @Override
    protected int setContentView() {
        return R.layout.common_recyclerview;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

}
