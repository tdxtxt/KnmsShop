package com.knms.shop.android.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
import com.knms.shop.android.activity.details.BBpriceDetailsFragment;
import com.knms.shop.android.activity.login.LoginActivity;
import com.knms.shop.android.app.ForegroundCallbacks;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.other.TipNum;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.core.badgenumber.BadgeNumberManager;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.ToolsHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/10/5.
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private RelativeLayout homeLayout;
    private RelativeLayout msgLayout;
    private RelativeLayout personalCenterLayout;
    private ImageView homeImage;
    private ImageView msgImage;
    private ImageView personalCenterImage;
    private TextView homeText;
    private TextView msgText;
    private TextView personalCenterText;
    private TextView tvUnReadCount;
    private ImageView iv_tips;

    private MainFragmentAdapter adapter;
    private static Fragment currentFragment;
    private FrameLayout mFrameLayout;
    private List<Fragment> fragmentList = new ArrayList<>();

    private int whirt = 0xFFFFFFFF;
    private int yellow = 0xffFEBF29;
    public static boolean isOpen = false;
//    private boolean isTipMsg = false;//false没有红点
    private java.util.Observer unreadObserver;
    @Override
    protected void getParams(Intent intent) {
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {//聊天推送
            ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            if (messages != null && messages.size() > 0) {
                selectMsg();
                CommonHelper.goChat(this,messages.get(0).getSessionId());
            }
            return;
        }else if(intent.hasExtra(JPushInterface.EXTRA_EXTRA)){//极光推送
            selectHome();
            String json = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            CommonHelper.startActivity(this,json);

        }else if(intent.hasExtra(ConstantObj.IM_SELECT)){//二级界面跳转消息中心
            selectMsg();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getParams(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerObservers(true);
        User user = SPUtils.getUser();
        if (null != user && !user.isRepairMan()){
            startIntervalTask();
        }
        isOpen = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyMsgTip("notify");
    }
    @Override
    protected int layoutResID() {
        return R.layout.activity_main;
    }

    protected void initView() {
        homeImage = (ImageView) findViewById(R.id.home_image);
        msgImage = (ImageView) findViewById(R.id.msg_image);
        personalCenterImage = (ImageView) findViewById(R.id.personal_center_image);
        homeText = (TextView) findViewById(R.id.home_text);
        msgText = (TextView) findViewById(R.id.msg_text);
        personalCenterText = (TextView) findViewById(R.id.personal_center_text);
        homeLayout = (RelativeLayout) findViewById(R.id.home_layout);
        msgLayout = (RelativeLayout) findViewById(R.id.msg_layout);
        personalCenterLayout = (RelativeLayout) findViewById(R.id.personal_center_layout);
        tvUnReadCount = findView(R.id.tv_new_count);
        iv_tips = findView(R.id.iv_tips);
        homeLayout.setOnClickListener(this);
        msgLayout.setOnClickListener(this);
        personalCenterLayout.setOnClickListener(this);
        mFrameLayout = (FrameLayout) findViewById(R.id.content);
    }
    private void loadFramgent() {
        fragmentList.clear();
        CommonHelper.getUser().subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                if("1".equals(user.type)){//商户
                    fragmentList.add(HomeShopFragment.newInstance());
                }else if("2".equals(user.type)) {//维修师傅
                    fragmentList.add(HomeRepairFragment.newInstance());
                }
                fragmentList.add(new MsgFragment());
                fragmentList.add(new PersonalCenterFragment());
                adapter = new MainFragmentAdapter(getSupportFragmentManager(),fragmentList);
                currentFragment = (Fragment) adapter.instantiateItem(mFrameLayout, 0);
                adapter.setPrimaryItem(mFrameLayout, 0, currentFragment);
                adapter.finishUpdate(mFrameLayout);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                ToolsHelper.getInstance().logout();
                startActivityAnimGeneral(LoginActivity.class,null);
                finshActivity();
            }
        });
    }
    @Override
    protected void initData() {
        loadFramgent();
        selectHome();
        unreadObserver = new java.util.Observer() {
            @Override
            public void update(java.util.Observable o, Object arg) {
                if(arg instanceof TipNum){
                    iv_tips.setVisibility(((TipNum) arg).isTotalZero() ? View.GONE : View.VISIBLE);
                }
            }
        };
        KnmsShopApp.getInstance().getUnreadObservable().addObserver(unreadObserver);
    }

    @Override
    public void onClick(View v) {
        clearChioce();
        switch (v.getId()) {
            case R.id.home_layout:
                selectHome();
                break;
            case R.id.msg_layout:
                selectMsg();
                break;
            case R.id.personal_center_layout:
                selectInfo();
                break;
        }
    }
    private void selectHome(){
        clearChioce();
        if(homeImage != null) homeImage.setImageResource(R.drawable.b_2);
        if(homeText != null) homeText.setTextColor(yellow);
        if(adapter != null && mFrameLayout != null){
            Fragment fragment = (Fragment) adapter.instantiateItem(mFrameLayout, 0);
            switchContent(currentFragment, fragment);
            notifyMsgTip("notify");
        }
    }
    public void toHomePage(){
        if(adapter != null){
            Fragment fragment = (Fragment) adapter
                    .instantiateItem(mFrameLayout, 0);
            switchContent(currentFragment, fragment);
        }
    }
    private void selectMsg(){
        clearChioce();
        if(msgImage != null) msgImage.setImageResource(R.drawable.c_2);
        if(msgText != null) msgText.setTextColor(yellow);
        if(adapter != null && mFrameLayout != null){
            Fragment fragment = (Fragment) adapter.instantiateItem(mFrameLayout, 1);
            switchContent(currentFragment, fragment);
            notifyMsgTip("notify");
        }
    }
    private void selectInfo(){
        clearChioce();
        if(personalCenterImage != null)personalCenterImage.setImageResource(R.drawable.d_2);
        if(personalCenterText != null)personalCenterText.setTextColor(yellow);
        if(adapter != null && mFrameLayout != null){
            Fragment fragment = (Fragment) adapter.instantiateItem(mFrameLayout, 2);
            switchContent(currentFragment, fragment);
            notifyMsgTip("notify");
        }
    }
    public static Fragment getCurrentFragement(){
        return currentFragment;
    }
    public void switchContent(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (!to.isAdded()) {
                transaction.setCustomAnimations(R.anim.in_from_right,R.anim.out_to_left);
                transaction.hide(from).add(R.id.content, to,to.getClass().getSimpleName())
                        .commit();
            } else {
                transaction.hide(from).show(to).commit();
            }
        } else if (currentFragment == to && currentFragment == adapter.getItem(0)) {
            if(isFastClick()){
                RxBus.get().post(BusAction.ACTION_REFRESH_HOME, "");
            }
        }
    }
    @Subscribe(tags = {@Tag(BusAction.PUT_BBPRICE_DETAILS)})
    public void putBBpriceDetails(final String id){
        //为了解决点击两次返回后，再从通知栏点击键入比比货详情，fragment重叠问题
        Observable.timer(500,TimeUnit.MILLISECONDS).compose(this.<Long>applySchedulers())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        switchContent(getCurrentFragement(), BBpriceDetailsFragment.newInstance(id,0));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {}
                });

    }
    @Subscribe(tags = {@Tag(BusAction.PUT_BBPRICE_DETAILS)})
    public void putBBpriceDetails(BBprice data){
        switchContent(getCurrentFragement(), BBpriceDetailsFragment.newInstance(data));
    }
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime;
    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    private void clearChioce() {
        if(homeImage != null){
            homeImage.setImageResource(R.drawable.b_1);
            homeText.setTextColor(whirt);
        }
        if(msgImage != null) msgImage.setImageResource(R.drawable.c_1);
        if(msgText != null) msgText.setTextColor(whirt);
        if(personalCenterImage != null) personalCenterImage.setImageResource(R.drawable.d_1);
        if(personalCenterText != null) personalCenterText.setTextColor(whirt);
    }

    private class MainFragmentAdapter extends FragmentStatePagerAdapter {
        List<Fragment> data;
        public MainFragmentAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatus, register);//在线状态变更
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, register);//监听最近会话变更
        NIMClient.getService(AuthServiceObserver.class).observeOtherClients(onlineclients, true);//多端登录状态观察者
    }
    Observer<List<OnlineClient>> onlineclients = new Observer<List<OnlineClient>>() {
        @Override
        public void onEvent(List<OnlineClient> onlineClients) {
            if(onlineClients != null && onlineClients.size() > 5){
//                NIMClient.getService(AuthService.class).kickOtherClient(onlineClients.get(0));//主动踢掉当前同时在线的其他端
            }
        }
    };
    Observer<StatusCode> onlineStatus = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode.wontAutoLogin()) {
                if (statusCode == StatusCode.KICKOUT) {
                    Tst.showToast("你的账号在其他设备有登录");
                    ToolsHelper.getInstance().logout();
                    startActivityAnimGeneral(LoginActivity.class, null);
                    finshActivity();
                } else if (statusCode == StatusCode.PWD_ERROR) {
                    // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                    Tst.showToast("登录密码错误,请重新登录");
                }
            }
        }
    };
    //最近会话变更
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            notifyMsgTip("notify");
        }
    };

    //接收消息该改变的通知(聊天会话变更&查阅凯恩买手和凯恩客服后的通知)
    @Subscribe(tags = {@Tag(BusAction.REFRESH_MSG_TIP)})
    public void notifyMsgTip(String notify) {
        RxBus.get().post(BusAction.REFRESH_MSG_KNMS,"refresh");
        IMHelper.getInstance().unreadMsgCount().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if(integer >= 0) tvUnReadCount.setText(integer > 99 ? "99+" : integer + "");
                BadgeNumberManager.from(KnmsShopApp.getInstance()).setBadgeNumber(integer);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {}
        });
    }
    long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime >= 1000) {
                Tst.showToast("再按一次退出程序");
                firstTime = secondTime;
                return true;
            } else {
                MobclickAgent.onKillProcess(this);
                KnmsShopApp.getInstance().AppExit(this);
            }
        }
        finshActivity();
        return super.onKeyDown(keyCode, event);
    }
    Subscription subscription;
    private void startIntervalTask(){
        subscription = Observable.interval(1, 15, TimeUnit.SECONDS)//延时1秒 ，每间隔5秒
                .map(new Func1<Long, User>() {
                    @Override
                    public User call(Long aLong) {
                        return SPUtils.getUser();
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        if(user == null) return false;
                        return TextUtils.equals("1", user.type) && ForegroundCallbacks.get().isForeground();
                    }
                }).flatMap(new Func1<User, Observable<ResponseBody<TipNum>>>() {
                    @Override
                    public Observable<ResponseBody<TipNum>> call(User user) {
                        return RxRequestApi.getInstance().getApiService().getTipNum(user.shopid);
                    }
                }).compose(this.<ResponseBody<TipNum>>applySchedulers())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ResponseBody<TipNum>>>() {
                    @Override
                    public Observable<? extends ResponseBody<TipNum>> call(Throwable throwable) {
                        return null;
                    }
                })
                .subscribe(new Action1<ResponseBody<TipNum>>() {
                    @Override
                    public void call(ResponseBody<TipNum> body) {
                        if (body.isSuccess()){
                            KnmsShopApp.getInstance().getUnreadObservable().sendData(body.data);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }
    private void stopIntervalTask(){
        if(subscription != null) subscription.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpen = false;
        registerObservers(false);
        stopIntervalTask();
        KnmsShopApp.getInstance().getUnreadObservable().deleteObserver(unreadObserver);
    }
}
