package com.knms.shop.android.activity.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.login.LoginActivity;
import com.knms.shop.android.adapter.CommFragmentPagerAdapter;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.fragment.ClientsFragment;
import com.knms.shop.android.fragment.MsgCenterFragment;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.util.ToolsHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/5.
 * 消息
 */
public class MsgFragment extends BaseFragment {
    MagicIndicator magicIndicator;
    ViewPager viewPager;
    CommonNavigator commonNavigator;
    List<BaseFragment> fragments = new ArrayList<>();

    TextView tv_tips;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments.clear();
        fragments.add(MsgCenterFragment.newInstance());
        fragments.add(ClientsFragment.newInstance());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_center_layout,null);
        initView(view);
        return view;
    }
    private CommonNavigatorAdapter commonNavigatorAdapter;
    private void initView(View view){
        tv_tips = (TextView) view.findViewById(R.id.tv_tips);
        tv_tips.setVisibility(View.GONE);
        magicIndicator = (MagicIndicator) view.findViewById(R.id.magic_indicator);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(commonNavigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.GRAY);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(fragments.get(index).getTitle());
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerObservers(true);
        tv_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getString(R.string.nim_status_unlogin).equals(tv_tips.getText().toString())){
                    IMHelper.getInstance().relogin();
                }
            }
        });

    }

    @Override
    protected String umTitle() {
        return "消息中心";
    }

    @Override
    public void onResume() {
        super.onResume();
//        IMHelper.getInstance().enableMsgNotification(false);
    }

    @Override
    public void onPause() {
        super.onPause();
//        IMHelper.getInstance().enableMsgNotification(true);
    }
    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatus, register);//在线状态变更
    }
    Observer<StatusCode> onlineStatus = new Observer<StatusCode> () {
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                if (code == StatusCode.KICKOUT) {
                    Tst.showToast("你的账号在其他设备有登录");
                    ToolsHelper.getInstance().logout();
                    startActivityAnimGeneral(LoginActivity.class,null);
                    getmActivity().finshActivity();
                }else if(code == StatusCode.PWD_ERROR){
                    // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                    Tst.showToast("登录密码错误,请重新登录");
                }

            } else {
                if (code == StatusCode.NET_BROKEN) {
                    tv_tips.setVisibility(View.VISIBLE);
                    tv_tips.setText(R.string.net_broken);
                } else if (code == StatusCode.UNLOGIN) {
                    tv_tips.setVisibility(View.VISIBLE);
                    tv_tips.setText(R.string.nim_status_unlogin);
                    IMHelper.getInstance().relogin();
                } else if (code == StatusCode.CONNECTING) {
                    tv_tips.setVisibility(View.VISIBLE);
                    tv_tips.setText(R.string.nim_status_connecting);
                } else if (code == StatusCode.LOGINING) {
                    tv_tips.setVisibility(View.VISIBLE);
                    tv_tips.setText(R.string.nim_status_logining);
                } else {
                    tv_tips.setText("");
                    tv_tips.setVisibility(View.GONE);
                }
            }
        }
    };
    public void setTitle(final String title, final int position){
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                ColorTransitionPagerTitleView tv = (ColorTransitionPagerTitleView) commonNavigator.getPagerTitleView(position);
                tv.setText(title);
                commonNavigator.requestLayout();
            }
        },200);
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        super.onDestroy();
    }
}
