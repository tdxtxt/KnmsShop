package com.knms.shop.android.activity.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.hubert.library.HighLight;
import com.app.hubert.library.NewbieGuide;
import com.knms.shop.android.R;
import com.knms.shop.android.adapter.CommFragmentPagerAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.other.BBpriceNewCount;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.HomePageFragment;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.view.GuideView;
import com.umeng.analytics.MobclickAgent;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CustomTitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tdx on 2016/10/5.
 * 商家首页
 */
public class HomeShopFragment extends BaseFragment{
    final int KnmsBuy = 5;//铠恩内部商品比比货
    final int MyBuy = 2;//自己发布比比货
    MagicIndicator magicIndicator;
    ViewPager viewPager;
    CommonNavigator commonNavigator;
    List<BaseFragment> fragments = new ArrayList<>();

    public String knmsLastTime;
    public String myLastTime;

    private Subscription subscription;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        fragments.clear();
        fragments.add(HomePageFragment.newInstance(KnmsBuy));
        fragments.add(HomePageFragment.newInstance(MyBuy));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout,null);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        startIntervalTask();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        magicIndicator = (MagicIndicator) view.findViewById(R.id.home_magic_indicator);
        viewPager = (ViewPager) view.findViewById(R.id.home_view_pager);
        commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CustomTitleView colorTransitionPagerTitleView = new CustomTitleView(context){
                    @Override
                    public void onSelected(int index, int totalCount) {
                        ImageView imageView = new ImageView(getmActivity());
                        this.setId(index);
                        if (index == 0) {
                            imageView.setImageResource(R.drawable.guide_dnxs);
                        } else {
                            imageView.setImageResource(R.drawable.guide_pzqg);
                        }

                        new GuideView.Builder()
                                .setTargetView(this)    // 必须调用，设置需要Guide的View
                                .setCustomGuideView(imageView)  // 必须调用，设置GuideView，可以使任意View的实例，比如ImageView 或者TextView
                                .setDirction(GuideView.Direction.BOTTOM)   // 设置GuideView 相对于TargetView的位置，有八种，不设置则默认在屏幕左上角,其余的可以显示在右上，右下等等
                                .setShape(GuideView.MyShape.ELLIPSE)   // 设置显示形状，支持圆形，椭圆，矩形三种样式，矩形可以是圆角矩形，
                                .setOnclickExit(true)   // 设置点击消失，可以传入一个Callback，执行被点击后的操作
                                .setRadius(LocalDisplay.dp2px(32))
                                // 设置偏移，一般用于微调GuideView的位置
                                .setOffset(index % 2 == 0 ? -LocalDisplay.dp2px(85) : LocalDisplay.dp2px(85), -10)
                                .showOnce()// 设置首次显示，设置后，显示一次后，不再显示
                                .build(getmActivity())                // 必须调用，Buider模式，返回GuideView实例
                                .show();                // 必须调用，显示GuideView
                    }
                };
                colorTransitionPagerTitleView.setNormalColor(Color.GRAY);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(index == 0 ? "店内相似" : "拍照求购");
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(getActivity(), index == 0 ? "touchShopSimilarityButton" : "touchPersonalityBuyButton");
                        viewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }
            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }

        });
        magicIndicator.setNavigator(commonNavigator);
        CommFragmentPagerAdapter adapter = new CommFragmentPagerAdapter(getFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void startIntervalTask(){
        subscription = Observable.interval(1, 15, TimeUnit.SECONDS)//延时1秒 ，每间隔5秒
         .filter(new Func1<Long, Boolean>() {
             @Override
             public Boolean call(Long aLong) {
                 if(!isVisible()) return false;//不可见
                 if(TextUtils.isEmpty(knmsLastTime)) knmsLastTime = StrHelper.getCurrentTime(StrHelper.DATE_FORMAT2);
                 if(TextUtils.isEmpty(myLastTime)) myLastTime = StrHelper.getCurrentTime(StrHelper.DATE_FORMAT2);
                 return true;
             }
         })
        .flatMap(new Func1<Long, Observable<ResponseBody<BBpriceNewCount>>>() {
            @Override
            public Observable<ResponseBody<BBpriceNewCount>> call(Long aLong) {
                return RxRequestApi.getInstance().getApiService().hasNewBBprice(knmsLastTime,myLastTime);
            }
        }).compose(this.<ResponseBody<BBpriceNewCount>>applySchedulers())
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends ResponseBody<BBpriceNewCount>>>() {
            @Override
            public Observable<? extends ResponseBody<BBpriceNewCount>> call(Throwable throwable) {
                return null;
            }
        })
        .subscribe(new Action1<ResponseBody<BBpriceNewCount>>() {
            @Override
            public void call(ResponseBody<BBpriceNewCount> body) {
                if(body.isSuccess() && body.data != null){
                    setRedState(KnmsBuy,body.data.knmsCount);
                    setRedState(MyBuy,body.data.myCount);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {}
        });
    }

    public void setRedState(int type, int count){
        CustomTitleView view = null;
        if(type == KnmsBuy){
            view = (CustomTitleView) commonNavigator.getPagerTitleView(0);
        }else if(type == MyBuy){
            view = (CustomTitleView) commonNavigator.getPagerTitleView(1);
        }
        view.setRedVisibility(count);
    }
    private void stopIntervalTask(){
        if(subscription != null) subscription.unsubscribe();
    }
    @Override
    protected String umTitle() {
        return "首页";
    }
    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        stopIntervalTask();
        super.onDestroy();
    }
    @Subscribe(tags = {@Tag(BusAction.ACTION_REFRESH_HOME)})
    public void refreshData(String str) {
        int type = viewPager.getCurrentItem() == 0 ? KnmsBuy : MyBuy;
        ((HomePageFragment)fragments.get(viewPager.getCurrentItem())).refreshData(type);
    }
    public static BaseFragment newInstance() {
        HomeShopFragment homeFragment = new HomeShopFragment();
        return homeFragment;
    }
}
