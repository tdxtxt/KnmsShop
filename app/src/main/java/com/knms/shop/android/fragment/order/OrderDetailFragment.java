package com.knms.shop.android.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.adapter.order.CouponListAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.OrderDetail;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.LazyLoadFragment;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.CalendarUtils;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * 订单详情
 * Created by 654654 on 2017/5/2.
 */

public class OrderDetailFragment extends LazyLoadFragment {

    private TextView id,orderTime,ordeliverytime,ordeliverypersion,phone,addr,orcontractamount,oramountpaid,preferamount,refund;
    private LinearLayout showRefund;
    private String orderId;
    private ScrollView scrollView;
    private LinearLayout showCoupon;
    private RecyclerView couponList;
    private CouponListAdapter couponListAdapter;
    public static OrderDetailFragment newInstance(String orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
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
    protected String umTitle() {
        return "订单详情";
    }

    @Override
    public String getTitle() {
        return "订单详情";
    }

    @Override
    protected void initView(View view) {
        RxBus.get().register(this);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        id = (TextView) view.findViewById(R.id.id);
        orderTime = (TextView) view.findViewById(R.id.orderTime);
        ordeliverytime = (TextView) view.findViewById(R.id.ordeliverytime);
        ordeliverypersion = (TextView) view.findViewById(R.id.ordeliverypersion);
        phone = (TextView) view.findViewById(R.id.phone);
        addr = (TextView) view.findViewById(R.id.addr);
        orcontractamount = (TextView) view.findViewById(R.id.orcontractamount);
        oramountpaid = (TextView) view.findViewById(R.id.oramountpaid);
        preferamount = (TextView) view.findViewById(R.id.preferamount);
        refund = (TextView) view.findViewById(R.id.refund);
        showRefund = (LinearLayout) view.findViewById(R.id.showRefund);

        showCoupon = (LinearLayout) view.findViewById(R.id.showCoupon);
        couponList = (RecyclerView) view.findViewById(R.id.couponList);
        couponList.setLayoutManager(new FullyLinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        couponListAdapter = new CouponListAdapter();
        couponList.setAdapter(couponListAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_order_detail;
    }

    @Override
    protected void lazyLoad() {
        if (TextUtils.isEmpty(orderId))
            return;
//        orderId = "06c9a1d44a14480ba4adc758e7128641";
//        Log.e("result","id == "+ SPUtils.getUser().id);
        RxRequestApi.getInstance().getApiService().getOrderDetail(orderId).compose(this.<ResponseBody<OrderDetail>>applySchedulers())
                .subscribe(new Action1<ResponseBody<OrderDetail>>() {
                    @Override
                    public void call(ResponseBody<OrderDetail> orderDetailResponseBody) {
                        updateView(orderDetailResponseBody.data);
                    }
                });
    }

    private void updateView(OrderDetail data) {
        if (null == data) return;
        id.setText(data.orcontractno);
        orderTime.setText(data.orcreated);
        ordeliverytime.setText(CalendarUtils.getDataFormat(data.ordeliverytime));
        ordeliverypersion.setText(data.orreceivercontacts);
        phone.setText(data.orreceiverphone);
        addr.setText(data.ordeliverylocation);
        orcontractamount.setText("¥"+data.orcontractamount);
        oramountpaid.setText("¥"+data.oramountpaid);
        if (TextUtils.isEmpty(data.preferamount) || "0".equals(data.preferamount) || "0.0".equals(data.preferamount)|| "0.00".equals(data.preferamount)){
            preferamount.setVisibility(View.GONE);
        }else {
            preferamount.setVisibility(View.VISIBLE);
            preferamount.setText("(含使用买手优惠券"+data.preferamount+"元)");
        }
        if (TextUtils.isEmpty(data.orrefundamount) || "0".equals(data.orrefundamount)){
            showRefund.setVisibility(View.GONE);
        }else {
            showRefund.setVisibility(View.VISIBLE);
            refund.setText("¥"+data.orrefundamount);
        }
        if (null != data.couponList && data.couponList.size() > 0) {
            couponListAdapter.setNewData(data.couponList);
            showCoupon.setVisibility(View.VISIBLE);
        }else {
            showCoupon.setVisibility(View.GONE);
        }
    }
    @Subscribe(tags = {@Tag(BusAction.ORDER_DELIVERY_TIME)})
    public void timeChang(String alterTime){
        ordeliverytime.setText(CalendarUtils.getDataFormat(alterTime));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
    }
}
