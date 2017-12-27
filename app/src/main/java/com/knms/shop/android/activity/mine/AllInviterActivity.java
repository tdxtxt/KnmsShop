package com.knms.shop.android.activity.mine;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.InviterFriends;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/31.
 */

public class AllInviterActivity extends HeadBaseActivity{
    private PullToRefreshRecyclerView pullToRefreshRecyclerView;
    private RecyclerView recyclerView;
    private RelativeLayout noDataLayout;
    private TextView tvInviteNumber;
    private BaseQuickAdapter<InviterFriends.FriendsInfo,BaseViewHolder> mAdapter;
    private int pageindex=1;
    @Override
    public void setCenterTitleView(TextView tv_center) {
            tv_center.setText("邀请好友列表");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_all_invite_friends_layout;
    }

    @Override
    protected void initView() {
        pullToRefreshRecyclerView=findView(R.id.rv_allfriends);
        recyclerView=pullToRefreshRecyclerView.getRefreshableView();
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noDataLayout=findView(R.id.rl_status);
//        SpannableStringBuilder style=new SpannableStringBuilder("已成功邀请0人");
//        style.setSpan(new ForegroundColorSpan(Color.RED),5,6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvInviteNumber=findView(R.id.tv_invite_number);
    }

    @Override
    protected void initData() {
        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageindex=1;
                reqApi();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageindex++;
                reqApi();
            }
        });
        mAdapter=new BaseQuickAdapter<InviterFriends.FriendsInfo,BaseViewHolder>(R.layout.item_inviter_layout,null) {

            @Override
            protected void convert(BaseViewHolder helper, InviterFriends.FriendsInfo item) {
                helper.setText(R.id.invite_friends_phonenumber,item.phoneNumber);
                helper.setText(R.id.invite_time,item.inviteTime.split(" ")[0]);
            }
        };
        recyclerView.setAdapter(mAdapter);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshRecyclerView.setRefreshing();
            }
        },500);
    }


    @Override
    protected void reqApi() {
        RxRequestApi.getInstance().getApiService().getInviteList(pageindex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<InviterFriends>>() {
                    @Override
                    public void call(ResponseBody<InviterFriends> listResponseBody) {
                        pullToRefreshRecyclerView.onRefreshComplete();
                        if (listResponseBody.isSuccess()) {
                            updateView(listResponseBody.data);
                            tvInviteNumber.setText(listResponseBody.data.total+"");
                        } else Tst.showToast(listResponseBody.desc);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pullToRefreshRecyclerView.onRefreshComplete();
                        Tst.showToast(throwable.getMessage());
                    }
                });
    }

    private void updateView(InviterFriends data){
        if(pageindex==1&&data.list.size()==0){
            KnmsShopApp.getInstance().showDataEmpty(noDataLayout,"您尚未成功邀请好友",R.drawable.no_data_on_offer);
        }
        if(pageindex==1)
            mAdapter.setNewData(data.list);
        else{
            if(data.list.size()==0){
                Tst.showToast("没有更多了");
                return;
            }
            mAdapter.addData(data.list);
        }
    }


    @Override
    protected String umTitle() {
        return "邀请好友列表";
    }
}
