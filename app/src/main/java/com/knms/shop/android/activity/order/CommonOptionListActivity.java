package com.knms.shop.android.activity.order;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseFragmentActivity;
import com.knms.shop.android.adapter.CommFragmentPagerAdapter;
import com.knms.shop.android.fragment.base.BaseFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

/**
 * Activityz
 * Created by 654654 on 2017/4/30.
 */

public abstract class CommonOptionListActivity extends HeadBaseFragmentActivity {
    protected MagicIndicator magicIndicator;
    protected ViewPager viewPager;
    protected CommonNavigator commonNavigator;
    private List<BaseFragment> fragments;
    private int width = 0;
    @Override
    protected int layoutResID() {
        return R.layout.activity_mine_shop;
    }

    @Override
    protected void initView() {
        fragments = getFragments();
        if (null == fragments)
            return;
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        viewPager = (ViewPager) findViewById(R.id.shop_viewpager);
        commonNavigator = new CommonNavigator(this);
        if (fragments.size() < 6) {
            commonNavigator.setAdjustMode(true);
//            commonNavigator.setSmoothScroll(false);
        }else{//项数过多时用滑动
            commonNavigator.setAdjustMode(false);
//            commonNavigator.setSmoothScroll(true);
            width = (getResources().getDisplayMetrics().widthPixels - UIUtil.dip2px(this, 10) * 4)  / 5;
        }
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                if (width > 0 )//项数过多时统一宽度看起来协调
                    colorTransitionPagerTitleView.setWidth(width);
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
        CommFragmentPagerAdapter adapter = new CommFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    protected abstract List<BaseFragment> getFragments();

    @Override
    protected void initData() {}
}
