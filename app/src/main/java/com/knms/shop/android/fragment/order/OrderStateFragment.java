package com.knms.shop.android.fragment.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.order.AlterDeliveryTimeActivity;
import com.knms.shop.android.activity.order.CompainDetailActivity;
import com.knms.shop.android.activity.order.UserCommentActivity;
import com.knms.shop.android.adapter.order.ComplaintsAdapter;
import com.knms.shop.android.adapter.order.OrderStateAdapter;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.Complaints;
import com.knms.shop.android.bean.body.order.OrderState;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.LazyLoadFragment;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.CircleImageView;
import com.knms.shop.android.view.TimeLineView;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * 订单状态
 * Created by 654654 on 2017/5/2.
 */

public class OrderStateFragment extends LazyLoadFragment {
    private String orderId,orderTime;
    public static OrderStateFragment newInstance(String orderId) {
        OrderStateFragment fragment = new OrderStateFragment();
        Bundle args = new Bundle();
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected String umTitle() {
        return "订单状态";
    }

    @Override
    public String getTitle() {
        return "订单状态";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("orderId",orderId);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState)
            orderId = savedInstanceState.getString("orderId");
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_order_state;
    }

    private RecyclerView complaintRecyclerView, stateRecyclerView;
    private LinearLayout showContent;
    private OrderStateAdapter stateAdapter;
    private ComplaintsAdapter complaintsAdapter;
    private CircleImageView ivShopLogo;
    private TextView tvShopName, tvTelephoneConnection, tvOnlineConnection,  tvStateDescribe;
    private TextView hint_bind;
    private TimeLineView timeLineView;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private RelativeLayout rlShopInfoLayout;
    private LinearLayout showBottom;
    private TextView returns,alterTime,lookComment;//退货,修改送货时间,查看评价
    @Override
    protected void initView(View view) {
        RxBus.get().register(this);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_to_refresh_scrollview);
        showContent = (LinearLayout) view.findViewById(R.id.showContent);
        complaintRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_order_complaint);
        stateRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_order_schedule);
        ivShopLogo = (CircleImageView) view.findViewById(R.id.iv_shop_logo);
        tvShopName = (TextView) view.findViewById(R.id.tv_shop_name);
        timeLineView = (TimeLineView) view.findViewById(R.id.timeLineView);
        tvTelephoneConnection = (TextView) view.findViewById(R.id.tv_telephone_connection);
        tvOnlineConnection = (TextView) view.findViewById(R.id.tv_online_connection);
        hint_bind = (TextView) view.findViewById(R.id.hint_bind);
        tvStateDescribe = (TextView) view.findViewById(R.id.tv_state_describe);
        rlShopInfoLayout = (RelativeLayout) view.findViewById(R.id.rl_shopinfo_layout);
        showBottom = (LinearLayout) view.findViewById(R.id.showBottom);
        returns = (TextView) view.findViewById(R.id.returns);
        alterTime = (TextView) view.findViewById(R.id.alterTime);
        lookComment = (TextView) view.findViewById(R.id.lookComment);

        showBottom.setVisibility(View.GONE);
        timeLineView.setVisibility(View.INVISIBLE);
        complaintRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        stateRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        complaintsAdapter = new ComplaintsAdapter(null);
        stateAdapter = new OrderStateAdapter(null);
        stateRecyclerView.setAdapter(stateAdapter);
        complaintRecyclerView.setAdapter(complaintsAdapter);
        tvTelephoneConnection.setOnClickListener(listener);
        tvOnlineConnection.setOnClickListener(listener);
        returns.setOnClickListener(listener);
        alterTime.setOnClickListener(listener);
        lookComment.setOnClickListener(listener);
        pullToRefreshScrollView.getRefreshableView().setVerticalScrollBarEnabled(false);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                lazyLoad();
            }
        });

        complaintRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Complaints item =  complaintsAdapter.getItem(position);
                if (null == item )
                    return;
                MobclickAgent.onEvent(getActivity(),"orderStateCompainDetail");
                Intent intent = new Intent(getActivity(), CompainDetailActivity.class);
                intent.putExtra("complaintsId", item.ocid);
                startActivity(intent);
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(pullToRefreshScrollView.getRefreshableView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    private String sid,phoneNum;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_telephone_connection://打电话
                    MobclickAgent.onEvent(getActivity(),"orderStateCallPhone");
                    onCallPhone();
                    break;
                case R.id.tv_online_connection://在线联系
                    MobclickAgent.onEvent(getActivity(),"orderStateContact");
                    onCallClient();
                    break;
                case R.id.returns://退货
                    MobclickAgent.onEvent(getActivity(),"orderStateReturns");
                    onReturns();
                    break;
                case R.id.alterTime://延迟送货
                    int delayable = (int) view.getTag();
                    MobclickAgent.onEvent(getActivity(),"orderStateAlterTime");
                    if (1 == delayable)
                        onAlterTime();
                    else
                        Toast.makeText( getContext(),"每天只能修改一次送货时间",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.lookComment://用户评价|送货完成
                    int state = (int) view.getTag();
                    String event;
                    if (1 == state){
                        event = "orderStateComplete";
                        onCompleteDelivery();
                    }else {
                        event = "orderStateComment";
                        onSeeComment();
                    }
                    MobclickAgent.onEvent(getActivity(),event);
                    break;
            }
        }
    };
    @Override
    protected void lazyLoad() {
        if (TextUtils.isEmpty(orderId)) {
            pullToRefreshScrollView.onRefreshComplete();
            return;
        }
        showProgress();
        RxRequestApi.getInstance().getApiService().getOrderState(orderId).compose(this.<ResponseBody<OrderState>>applySchedulers())
                .subscribe(new Action1<ResponseBody<OrderState>>() {
                    @Override
                    public void call(ResponseBody<OrderState> orderStateResponseBody) {
                        if (orderStateResponseBody.isSuccess()) {
                            showContent.setVisibility(View.VISIBLE);
                            updateView(orderStateResponseBody.data);
                        } else {
                            showContent.setVisibility(View.GONE);
                            Toast.makeText(getContext(), orderStateResponseBody.desc, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showContent.setVisibility(View.GONE);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        hideProgress();
                        pullToRefreshScrollView.onRefreshComplete();
                    }
                });
    }

    /** 初始化订单状态 */
    private void initOrderState(int orderState){
        String describeStr;
        switch (orderState) {
            case 0:
                describeStr = "客户已退货";
                showBottom.setVisibility(View.GONE);
                break;
            case 1:
                describeStr = "生成订单，待商家送货";
                showBottom.setVisibility(View.VISIBLE);
                returns.setVisibility(View.VISIBLE);
                alterTime.setVisibility(View.VISIBLE);
                lookComment.setVisibility(View.VISIBLE);
                lookComment.setTag(orderState);
                lookComment.setText("送货完成");
                break;
            case 2:
                describeStr = "商家送货完成，待客户确认收货";
                showBottom.setVisibility(View.GONE);
                break;
            case 3:
                describeStr = "客户确认收货，待客户评价";
                showBottom.setVisibility(View.GONE);
                break;
            case 4:
                describeStr = "客户已评价，待商家回复";
                showBottom.setVisibility(View.VISIBLE);
                returns.setVisibility(View.GONE);
                alterTime.setVisibility(View.GONE);
                lookComment.setVisibility(View.VISIBLE);
                lookComment.setTag(orderState);
                lookComment.setText("查看评价");
                break;
            case 5:
                describeStr = "商家已回复评价";
                showBottom.setVisibility(View.VISIBLE);
                returns.setVisibility(View.GONE);
                alterTime.setVisibility(View.GONE);
                lookComment.setVisibility(View.VISIBLE);
                lookComment.setTag(orderState);
                lookComment.setText("查看评价");
                break;
            case 6:
                describeStr = "超过10天未收货，系统默认收货";
                showBottom.setVisibility(View.GONE);
                break;
            default:
                describeStr = "生成订单，待商家送货";
                showBottom.setVisibility(View.GONE);
        }

        tvStateDescribe.setText(describeStr);

        String[] strs;
        int step;
        if (orderState == 0) {
            strs = new String[]{"待送货", "客户退货"};
            step = 2;
        } else {
            strs = new String[]{"待送货", "送货完成", "确认收货", "客户评价", "商家回复"};
            step = orderState == 6 ? 3 : orderState;
        }
        timeLineView.builder().pointStrings(strs, step).load();
        timeLineView.setVisibility(View.VISIBLE);
    }

    private void updateView(OrderState data) {
        if (data.userinfo == null) {
            hint_bind.setVisibility(View.VISIBLE);
            tvShopName.setVisibility(View.GONE);
            ivShopLogo.setVisibility(View.INVISIBLE);
            tvTelephoneConnection.setVisibility(View.GONE);
            tvOnlineConnection.setVisibility(View.GONE);
        }else {
            hint_bind.setVisibility(View.GONE);
            tvShopName.setVisibility(View.VISIBLE);
            ivShopLogo.setVisibility(View.VISIBLE);
            tvTelephoneConnection.setVisibility(View.VISIBLE);
            tvOnlineConnection.setVisibility(View.VISIBLE);
            ImageLoadHelper.getInstance().displayImageHead(data.userinfo.usPhoto,ivShopLogo);
            phoneNum = data.userinfo.usPhone;
            tvShopName.setText(data.userinfo.usNickname);
            sid = data.userinfo.usId;
        }
        orderTime = data.deliveryTime;
        alterTime.setTag(data.delayable);
        if (1 == data.delayable){
            alterTime.setTextColor(getResources().getColor(R.color.black_333333));
        }else {
            alterTime.setTextColor(getResources().getColor(R.color.gray_999999));
        }
        if (null == data.complaintList || data.complaintList.size() < 1){
            complaintRecyclerView.setVisibility(View.GONE);
        }else {
            complaintRecyclerView.setVisibility(View.VISIBLE);
        }
        initOrderState(data.state);

        stateAdapter.setNewData(data.deliveryList);
        complaintsAdapter.setNewData(data.complaintList);
    }

    /** 打电话 */
    private void onCallPhone(){
        CommonHelper.onCallPhone(getActivity(),phoneNum);
    }

    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void commentChang(String orderId){
        lazyLoad();
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_DELIVERY_TIME)})
    public void timeChang(String alterTime){
        lazyLoad();
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_COMPLETE)})
    public void completeChang(String orderId){
        lazyLoad();
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_RETURNS)})
    public void returnsChang(String orderId){
        lazyLoad();
    }

    /** 在线联系 */
    private void onCallClient(){
        CommonHelper.goChat(getActivity(),sid);
    }

    /** 用户退货 */
    private void onReturns(){
        if (TextUtils.isEmpty(orderId))
            return;
        DialogHelper.showPromptDialog(getActivity(), null, "确定该合同已全部退货?", "取消", null, "确定", new DialogHelper.OnMenuClick() {
            @Override
            public void onLeftMenuClick() {}
            @Override
            public void onCenterMenuClick() {}
            @Override
            public void onRightMenuClick() {//确定
                RxRequestApi.getInstance().getApiService().postReturnDelivery(orderId).compose(OrderStateFragment.this.<ResponseBody<String>>applySchedulers())
                        .subscribe(new Action1<ResponseBody<String>>() {
                            @Override
                            public void call(ResponseBody<String> stringResponseBody) {
                                if (stringResponseBody.isSuccess()){
                                    Toast.makeText(getContext(),"客户退货成功",Toast.LENGTH_SHORT).show();
                                    RxBus.get().post(BusAction.ORDER_STATE_RETURNS,orderId);
                                }else {
                                    Toast.makeText(getContext(),stringResponseBody.desc,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    /** 延迟送货时间  */
    private void onAlterTime(){
        HashMap<String,Object> params = new HashMap<>();
        params.put("orderId",orderId);
        params.put("orderTime",orderTime);
        startActivityAnimGeneral(AlterDeliveryTimeActivity.class,params);
    }
    /** 查看评价 */
    private void onSeeComment(){
        HashMap<String,Object> params = new HashMap<>();
        params.put("orderId",orderId);
        startActivityAnimGeneral(UserCommentActivity.class,params);
    }
    /** 用户确定送货完成 */
    private void onCompleteDelivery(){
        DialogHelper.showPromptDialog(getActivity(), null, "确定已经送货完成?", "取消", null, "确定", new DialogHelper.OnMenuClick() {
            @Override
            public void onLeftMenuClick() {}
            @Override
            public void onCenterMenuClick() {}
            @Override
            public void onRightMenuClick() {//确定
                RxRequestApi.getInstance().getApiService().postCompleteDelivery(orderId).compose(OrderStateFragment.this.<ResponseBody<String>>applySchedulers())
                        .subscribe(new Action1<ResponseBody<String>>() {
                            @Override
                            public void call(ResponseBody<String> stringResponseBody) {
                                if (stringResponseBody.isSuccess()){
                                    Toast.makeText(getContext(),"送货完成",Toast.LENGTH_SHORT).show();
                                    RxBus.get().post(BusAction.ORDER_STATE_COMPLETE,orderId);
                                    KnmsShopApp.getInstance().getUnreadObservable().refreshTips();
                                }else {
                                    Toast.makeText(getContext(),stringResponseBody.desc,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}
