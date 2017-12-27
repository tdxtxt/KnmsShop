package com.knms.shop.android.activity.details;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.NestedScrollViewOverScrollDecorAdapter;
import com.knms.shop.android.adapter.mineshop.AttrAdapter;
import com.knms.shop.android.adapter.mineshop.AutoBrowseAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.ClassifyDetail;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ToolsHelper;
import com.knms.shop.android.view.scrollview.StickyScrollView;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/27.
 */

public class ProductDetailsActivity extends HeadBaseActivity {
    private ViewPager vp_detail_img;
    private AutoBrowseAdapter adapter_auto;
    private TextView mCurrentImgPage;
    private String goodsId;
    private TextView mTitle, mBrowseAmount, mContent;
    private AttrAdapter attrAdapter;
    private RecyclerView mRecyclerViewAttr;
    private View viewTopLayout;
    private StickyScrollView scrollView;

    @Override
    protected void getParmas(Intent intent) {
        super.getParmas(intent);
        goodsId=intent.getStringExtra("goid");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_goods_details;
    }

    @Override
    protected void initView() {
        viewTopLayout = findView(R.id.rl_border);
        viewTopLayout.setAlpha(0);
        scrollView = findView(R.id.scrollView);
        mRecyclerViewAttr=findView(R.id.recyclerView_params);
        mTitle = findView(R.id.title);
        mBrowseAmount = findView(R.id.browse_amount);
        mContent = findView(R.id.content);
        mCurrentImgPage = findView(R.id.tv_num);
        vp_detail_img = findView(R.id.goods_img_viewpager);
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
    protected void reqApi() {
        RxRequestApi.getInstance().getApiService().getGoodsDetail(goodsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<ClassifyDetail>>() {
                    @Override
                    public void call(ResponseBody<ClassifyDetail> body) {
                        if (body.isSuccess()) {
                            updateView(body.data);
                        } else {
                            Tst.showToast(body.desc);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        hideProgress();
                        Tst.showToast(throwable.toString());
                    }
                });
    }

    private void updateView(ClassifyDetail data) {
        mTitle.setText(data.cotitle);
        mBrowseAmount.setText(data.browseNumber + "次浏览");
        mContent.setText(data.coremark);
        adapter_auto = new AutoBrowseAdapter(data.imglist);
        vp_detail_img.setAdapter(adapter_auto);
        if (null != data.imglist)
            mCurrentImgPage.setText("1/" + data.imglist.size());

        //设置商品参数
        if (data.attributes != null && data.attributes.size() > 0) {
            ToolsHelper.getInstance().sort(data.attributes, "weight");
            attrAdapter = new AttrAdapter(data.attributes);
            mRecyclerViewAttr.setAdapter(attrAdapter);
        }
    }

    @Override
    protected void initData() {
        reqApi();
    }



    @Override
    protected String umTitle() {
        return null;
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {}
}
