package com.knms.shop.android.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseActivity;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
import com.knms.shop.android.activity.base.HeadBaseFragmentActivity;
import com.knms.shop.android.adapter.CommFragmentPagerAdapter;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.fragment.shop.ShopCustomFurnitureFragment;
import com.knms.shop.android.fragment.shop.ShopDecorationStyleFragment;
import com.knms.shop.android.fragment.shop.ShopProductFragment;

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
 * Created by Administrator on 2017/3/20.
 */

public class ShopActivity extends HeadBaseFragmentActivity {

    private MagicIndicator magicIndicator;
    private ViewPager viewPager;
    private CommonNavigator commonNavigator;
    private List<BaseFragment> fragments = new ArrayList<>();
    private String shopId;
    @Override
    protected int layoutResID() {
        return R.layout.activity_mine_shop;
    }

    @Override
    protected void getParams(Intent intent) {
        shopId = intent.getStringExtra("shopId");
    }

    @Override
    protected void initView() {
        fragments.add(ShopProductFragment.newInstance(shopId));//店铺商品
        fragments.add(ShopDecorationStyleFragment.newInstance(shopId));//家具风格
        fragments.add(ShopCustomFurnitureFragment.newInstance(shopId));//定制家具

        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        viewPager = (ViewPager) findViewById(R.id.shop_viewpager);
        commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
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
        CommFragmentPagerAdapter adapter = new CommFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("我的店铺");
    }
}
