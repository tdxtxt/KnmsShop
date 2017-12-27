package com.knms.shop.android.activity.orderpay;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.NestedScrollViewOverScrollDecorAdapter;
import com.knms.shop.android.adapter.mineshop.AttrAdapter;
import com.knms.shop.android.adapter.mineshop.AutoBrowseAdapter;
import com.knms.shop.android.bean.body.orderpay.OrderPayBody;
import com.knms.shop.android.bean.body.orderpay.OrderPayGoodsDetailData;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ToolsHelper;
import com.knms.shop.android.view.scrollview.StickyScrollView;

import java.util.HashMap;
import java.util.Map;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 新的商品详情
 * Created by 654654 on 2017/9/7.
 */

public class OrderPayGoodsDetailActivity extends HeadBaseActivity {

    private ViewPager vp_detail_img;
    private AutoBrowseAdapter adapter_auto;
    private TextView mCurrentImgPage;
    private TextView mTitle, mBrowseAmount, mContent;
    private TextView realityPrice,showPrice,showFreightTitle,showSalesTitle;
    private AttrAdapter attrAdapter;
    private RecyclerView mRecyclerViewAttr;
    private View viewTopLayout;
    private StickyScrollView scrollView;
    private String showId;
    public static final String KEY_SHOWID = "showId";
    @Override
    public void setCenterTitleView(TextView tv_center) {

    }
    @Override
    protected void getParmas(Intent intent) {
        super.getParmas(intent);
        showId=intent.getStringExtra(KEY_SHOWID);
    }
    @Override
    protected int layoutResID() {
        return R.layout.activity_orderpay_goods_detail;
    }

    @Override
    protected void initView() {
        viewTopLayout = findView(R.id.rl_border);
        viewTopLayout.setAlpha(0);
        scrollView = findView(R.id.scrollView);
        mRecyclerViewAttr=findView(R.id.recyclerView_params);
        mTitle = findView(R.id.title);
        mBrowseAmount = findView(R.id.browse_amount);
        realityPrice = findView(R.id.realityPrice);
        showPrice = findView(R.id.showPrice);
        showFreightTitle = findView(R.id.showFreightTitle);
        showSalesTitle = findView(R.id.showSalesTitle);
        mContent = findView(R.id.content);
        mCurrentImgPage = findView(R.id.tv_num);
        vp_detail_img = findView(R.id.goods_img_viewpager);

        showPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        vp_detail_img.setAdapter(adapter_auto);
        vp_detail_img.setOffscreenPageLimit(3);
        mRecyclerViewAttr.setLayoutManager(new LinearLayoutManager(this));
        vp_detail_img.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentImgPage.setText(position + 1 + "/" + adapter_auto.getCount());
            }
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        findView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finshActivity();
            }
        });
        scrollView.setOnScrollListener(new StickyScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                int height = LocalDisplay.dp2px(360 - 48);
                if(t > height){
                    viewTopLayout.setAlpha(1);
                }else{
                    viewTopLayout.setAlpha((float) t / (float)height);
                }
            }
        });
        new VerticalOverScrollBounceEffectDecorator(new NestedScrollViewOverScrollDecorAdapter(scrollView));
    }

    @Override
    protected void initData() {
        reqApi();
    }

    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(showId) )
            return;
        Map<String,Object> map=new HashMap<>();
        map.put("showId",showId);
        RxRequestApi.getInstance().getApiService().getOrderPayGoodsDetail(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderPayBody<OrderPayGoodsDetailData>>() {
                    @Override
                    public void call(OrderPayBody<OrderPayGoodsDetailData> body) {
                        if (body.isSuccess())
                            updataView(body.globalData.commodityShowBo);
                        else
                            Tst.showToast(body.message);
                    }
                });
    }

    private void updataView(OrderPayGoodsDetailData.CommodityShowBo data) {
        if (null == data)
            return;
        mTitle.setText(data.showName);
        mBrowseAmount.setText(data.browseNumber + "次浏览");
        realityPrice.setText(data.realityPrice);
        showPrice.setText(data.showPrice);
        showFreightTitle.setText(data.showFreightTitle);
        showSalesTitle.setText(data.showSalesTitle);
        mContent.setText(data.showDescription);
        adapter_auto = new AutoBrowseAdapter(data.showImages);
        vp_detail_img.setAdapter(adapter_auto);
        if (null != data.showImages)
            mCurrentImgPage.setText("1/" + data.showImages.size());


        //设置商品参数
        if (data.showParameter != null && data.showParameter.size() > 0) {
            ToolsHelper.getInstance().sort(data.showParameter, "sorting");
            attrAdapter = new AttrAdapter(data.showParameter);
            mRecyclerViewAttr.setAdapter(attrAdapter);
        }
    }

    @Override
    protected String umTitle() {
        return null;
    }
}
