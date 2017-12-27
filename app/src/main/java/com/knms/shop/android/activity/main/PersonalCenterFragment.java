package com.knms.shop.android.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.mine.CommWebViewActivity;
import com.knms.shop.android.activity.mine.FeedBackActivity;
import com.knms.shop.android.activity.mine.InviteFriendsActivity;
import com.knms.shop.android.activity.mine.SettingActivity;
import com.knms.shop.android.activity.mine.ShopActivity;
import com.knms.shop.android.activity.mine.UserInfoActivity;
import com.knms.shop.android.activity.order.OrderListActivity;
import com.knms.shop.android.activity.orderpay.OrderPayListActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.other.TipNum;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/10/5.
 */
public class PersonalCenterFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView circleImageView;
    private TextView tv_name, tvCall;

    private RelativeLayout mSettingLayout, mFeedbackLayout, mUserInfoLayout,mInstructions,mMineShopLayout,mInviteFriends;

    private TextView order_all,app_order_all;//查看所有订单
    private LinearLayout order_state_1,order_state_2,order_state_3,order_state_4;
    private LinearLayout app_order_obligation,app_order_state_1,app_order_state_2,app_order_state_3,app_order_state_4;

    private ImageView icon_order_state_1,icon_order_state_2,icon_order_state_3,icon_order_state_4;
    private ImageView icon_app_order_obligation,icon_app_order_state_1,icon_app_order_state_2,icon_app_order_state_3,icon_app_order_state_4;//支付订单

    private Observer unreadObserver;
    private TextView tv_new_count_wait_give,tv_new_count_wait_receipt,tv_new_count_wait_comment,tv_new_count_wait_reply;//小红点
    private TextView tv_app_new_count_obligation,tv_app_new_count_wait_give,tv_app_new_count_wait_receipt,tv_app_new_count_wait_comment,tv_app_new_count_wait_reply;
    private LinearLayout showOrder;//隐藏订单信息
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unreadObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(arg instanceof TipNum){
                    updateViewTip((TipNum)arg);
                }
            }
        };
    }

    private void updateViewTip(TipNum tipNum) {
        if(tv_new_count_wait_comment != null) tv_new_count_wait_comment.setText(tipNum.waitCommentCount + "");
        if(tv_new_count_wait_give != null) tv_new_count_wait_give.setText(tipNum.waitGiveCount + "");
        if(tv_new_count_wait_receipt != null) tv_new_count_wait_receipt.setText(tipNum.waitReceiptCount + "");
        if(tv_new_count_wait_reply != null) tv_new_count_wait_reply.setText(tipNum.waitReplyCount + "");
        if (null != tv_app_new_count_obligation ) tv_app_new_count_obligation.setText(tipNum.e+"");
        if (null != tv_app_new_count_wait_give ) tv_app_new_count_wait_give.setText(tipNum.f+"");
        if (null != tv_app_new_count_wait_receipt ) tv_app_new_count_wait_receipt.setText(tipNum.g+"");
        if (null != tv_app_new_count_wait_comment ) tv_app_new_count_wait_comment.setText(tipNum.h+"");
        if (null != tv_app_new_count_wait_reply ) tv_app_new_count_wait_reply.setText(tipNum.i+"");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_center_layout, null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KnmsShopApp.getInstance().getUnreadObservable().deleteObserver(unreadObserver);
        KnmsShopApp.getInstance().getUnreadObservable().addObserver(unreadObserver);
        KnmsShopApp.getInstance().getUnreadObservable().refreshTips();//更新数据
        CommonHelper.getUser().subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                updateView(user);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {}
        });
    }

    private void updateView(User user) {
        if (user != null) {
            ImageLoadHelper.getInstance().displayImageHead(user.shoppic, circleImageView);
            tv_name.setText(user.shopname);
            if("2".equals(user.type)){
                showOrder.setVisibility(View.GONE);
                mMineShopLayout.setVisibility(View.GONE);
//                mInviteFriends.setVisibility(View.GONE);
            }else if("1".equals(user.type)){
                showOrder.setVisibility(View.VISIBLE);
                mMineShopLayout.setVisibility(View.VISIBLE);
//                mInviteFriends.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initView(View view) {
        mInviteFriends= (RelativeLayout) view.findViewById(R.id.rl_invite_friends);
        mSettingLayout = (RelativeLayout) view.findViewById(R.id.setting);
        mFeedbackLayout = (RelativeLayout) view.findViewById(R.id.feedback);
        mUserInfoLayout = (RelativeLayout) view.findViewById(R.id.go_user_info);
        circleImageView = (CircleImageView) view.findViewById(R.id.iv_avatar);
        mMineShopLayout= (RelativeLayout) view.findViewById(R.id.mine_shop);

        order_all = (TextView) view.findViewById(R.id.order_all);
        app_order_all = (TextView) view.findViewById(R.id.app_order_all);
        order_state_1 = (LinearLayout) view.findViewById(R.id.order_state_1);
        order_state_2 = (LinearLayout) view.findViewById(R.id.order_state_2);
        order_state_3 = (LinearLayout) view.findViewById(R.id.order_state_3);
        order_state_4 = (LinearLayout) view.findViewById(R.id.order_state_4);
        icon_order_state_1 = (ImageView) view.findViewById(R.id.icon_order_state_1);
        icon_order_state_2 = (ImageView) view.findViewById(R.id.icon_order_state_2);
        icon_order_state_3 = (ImageView) view.findViewById(R.id.icon_order_state_3);
        icon_order_state_4 = (ImageView) view.findViewById(R.id.icon_order_state_4);

        tv_new_count_wait_give = (TextView) view.findViewById(R.id.tv_new_count_wait_give);
        tv_new_count_wait_receipt = (TextView) view.findViewById(R.id.tv_new_count_wait_receipt);
        tv_new_count_wait_comment = (TextView) view.findViewById(R.id.tv_new_count_wait_comment);
        tv_new_count_wait_reply = (TextView) view.findViewById(R.id.tv_new_count_wait_reply);
        tv_app_new_count_obligation = (TextView) view.findViewById(R.id.tv_app_new_count_obligation);
        tv_app_new_count_wait_give = (TextView) view.findViewById(R.id.tv_app_new_count_wait_give);
        tv_app_new_count_wait_receipt = (TextView) view.findViewById(R.id.tv_app_new_count_wait_receipt);
        tv_app_new_count_wait_comment = (TextView) view.findViewById(R.id.tv_app_new_count_wait_comment);
        tv_app_new_count_wait_reply = (TextView) view.findViewById(R.id.tv_app_new_count_wait_reply);

        app_order_obligation = (LinearLayout) view.findViewById(R.id.app_order_obligation);
        app_order_state_1 = (LinearLayout) view.findViewById(R.id.app_order_state_1);
        app_order_state_2 = (LinearLayout) view.findViewById(R.id.app_order_state_2);
        app_order_state_3 = (LinearLayout) view.findViewById(R.id.app_order_state_3);
        app_order_state_4 = (LinearLayout) view.findViewById(R.id.app_order_state_4);
        showOrder = (LinearLayout) view.findViewById(R.id.showOrder);

        ScrollView scrollView = findView(view,R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tvCall = (TextView) view.findViewById(R.id.call);
        mInstructions= (RelativeLayout) view.findViewById(R.id.rl_instructions);
        mInstructions.setOnClickListener(this);
        mFeedbackLayout.setOnClickListener(this);
        mSettingLayout.setOnClickListener(this);
        mUserInfoLayout.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        mMineShopLayout.setOnClickListener(this);
        mInviteFriends.setOnClickListener(this);
        order_all.setOnClickListener(this);
        app_order_all.setOnClickListener(this);
        order_state_1.setOnClickListener(this);
        order_state_2.setOnClickListener(this);
        order_state_3.setOnClickListener(this);
        order_state_4.setOnClickListener(this);
        app_order_obligation.setOnClickListener(this);
        app_order_state_1.setOnClickListener(this);
        app_order_state_2.setOnClickListener(this);
        app_order_state_3.setOnClickListener(this);
        app_order_state_4.setOnClickListener(this);


        User user = SPUtils.getUser();
        if (null != user && !user.isRepairMan()){
            showOrder.setVisibility(View.VISIBLE);
        }else {
            showOrder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting:
                getmActivity().startActivityAnimGeneral(SettingActivity.class, null);
                break;
            case R.id.feedback:
                getmActivity().startActivityAnimGeneral(FeedBackActivity.class, null);
                break;
            case R.id.go_user_info:
                getmActivity().startActivityAnimGeneral(UserInfoActivity.class, null);
                break;
            case R.id.call:
                CommonHelper.onCallPhone(getActivity(),CommonHelper.CSphone);
                break;
            case R.id.rl_instructions:
                Map<String,Object> map=new HashMap<>();
                map.put("url","http://h5.kebuyer.com/document/business_help.html");
                startActivityAnimGeneral(CommWebViewActivity.class,map);
                break;
            case R.id.mine_shop:
                MobclickAgent.onEvent(getActivity(),"enterMyStore");
                User user = SPUtils.getUser();
                if(user != null && !TextUtils.isEmpty(user.shopid)){
                    Map<String,Object> params = new HashMap<>();
                    params.put("shopId",user.shopid);
                    startActivityAnimGeneral(ShopActivity.class,params);
                }
                break;
            case R.id.rl_invite_friends:
                MobclickAgent.onEvent(getActivity(),"inviteFriendsBtnOnClick");
                startActivityAnimGeneral(InviteFriendsActivity.class,null);
                break;
            case R.id.order_all://查看所有的界面
                goOrderList(0);
                break;
            case R.id.order_state_1://
                goOrderList(1);
                break;
            case R.id.order_state_2://待收货
                goOrderList(2);
                break;
            case R.id.order_state_3://待评价
                goOrderList(3);
                break;
            case R.id.order_state_4://待回复
                goOrderList(4);
                break;
            case R.id.app_order_all://查看所有的界面
                goAppOrderList(0);
                break;
            case R.id.app_order_obligation://待付款
                goAppOrderList(1);
                break;
            case R.id.app_order_state_1://待送货
                goAppOrderList(2);
                break;
            case R.id.app_order_state_2://待收货
                goAppOrderList(3);
                break;
            case R.id.app_order_state_3://待评价
                goAppOrderList(4);
                break;
            case R.id.app_order_state_4://待回复
                goAppOrderList(5);
                break;
        }
    }

    /** 跳转订单列表 */
    private void goOrderList(int state){
        String event;
        switch (state){
            case 0:
                event = "orderMainStateAll";
                break;
            case 1:
                event = "orderMainStateDelivery";
                break;
            case 2:
                event = "orderMainStateHarvest";
                break;
            case 3:
                event = "orderMainStateReviews";
                break;
            case 4:
                event = "orderMainStateReply";
                break;
            default:
                event = "orderMainStateAll";
        }
        MobclickAgent.onEvent(getActivity(),event);
        Map<String,Object> params = new HashMap<>();
        params.put("state",state);
        goNeedLoginActivity(OrderListActivity.class,params);
    }
    /** 跳转订单列表 */
    private void goAppOrderList(int state){
        Map<String,Object> params = new HashMap<>();
        params.put("state",state);
        goNeedLoginActivity(OrderPayListActivity.class,params);
    }
    /** 需要登录的界面 */
    private void goNeedLoginActivity(Class<?> c,Map<String,Object> params){
        User user = SPUtils.getUser();
        if(user != null && !TextUtils.isEmpty(user.shopid)){
            startActivityAnimGeneral(c,params);
        }
    }

    @Override
    protected String umTitle() {
        return "我的";
    }

    @Override
    public void onDestroy() {
        KnmsShopApp.getInstance().getUnreadObservable().deleteObserver(unreadObserver);
        super.onDestroy();
    }
}
