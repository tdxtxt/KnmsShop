package com.knms.shop.android.activity.details.style;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseFragmentActivity;
import com.knms.shop.android.adapter.details.DecorationStyleFragmentPagerAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.product.StyleDetails;
import com.knms.shop.android.bean.body.product.StyleId;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.view.scrollview.BdScrollView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by tdx on 2016/10/25.
 * 家装风格详情
 * 若果需要无限查看下一个详情，则需要传入参数shopId&type(风格类型id)&ids(风格id数组)
 * 若只需产看单个详情，只需传入id即可
 * 其中position用于定位显示位置
 */

public class DecorationStyleDetailsActivity extends HeadBaseFragmentActivity {
    ViewPager viewPager;
    public Map<String,StyleDetails> details = new HashMap<>();
    private List<StyleId> styleIds;
    private DecorationStyleFragmentPagerAdapter adapter;
    private int position;
    private String type, shopId;
    private boolean isLoadMore = false;//true正在加载更多
    private boolean isReqNext = true;//是否请求下一组数据，false不请求
    public static int currentPage, maxPage;
    private RelativeLayout mTopLayout;
    /*******/
    private TextView mContent;//介绍内容
    private View scrollView;

    @Override
    protected void getParams(Intent intent) {
        List<String> ids = (ArrayList<String>) intent.getSerializableExtra("ids");
        if (ids != null) {
            if (styleIds == null) styleIds = new ArrayList<StyleId>();
            for (String id : ids) {
                StyleId styleid = new StyleId();
                styleid.inid = id;
                styleIds.add(styleid);
            }
        } else {
            String id = intent.getStringExtra("id");
            StyleId styleId = new StyleId();
            styleId.inid = id;
            styleIds = Arrays.asList(styleId);
            isReqNext = false;
        }
        position = intent.getIntExtra("position", 0);
        type = intent.getStringExtra("type");
        shopId = intent.getStringExtra("shopId");
        if(TextUtils.isEmpty(type) && TextUtils.isEmpty(shopId)) isLoadMore = true;
        if (styleIds != null) maxPage = styleIds.size();
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_decoration_style_details_f;
    }

    @Override
    protected void initView() {
        mTopLayout = findView(R.id.top_details_layout);
        viewPager = findView(R.id.viewpager);
        mContent = findView(R.id.content);
        scrollView = findView(R.id.scrollview);
        ((BdScrollView)scrollView).setMaxHeight(LocalDisplay.dp2px(120));
        findView(R.id.view_bg).setAlpha(0.3f);
        findView(R.id.top_details_layout).setBackgroundColor(R.color.black_24221E);
        ((ImageView) findViewById(R.id.iv_back)).setImageResource(R.drawable.icon_back_white);


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){// 横屏
        } else if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            hidden("portrait");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){// 横屏
        } else if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            hidden("portrait");
        }
        super.onConfigurationChanged(newConfig);
    }

    int collectNum = 0;
    public void setDetail(final StyleDetails data){
        if(data == null) return;
        collectNum = 0;
        //设置页码+内容
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewPager auto_vp = ((DecorationStyleDetailsFragment) adapter.getFragment(viewPager.getCurrentItem())).auto_vp;
                if(auto_vp == null) return;
                mContent.setText(auto_vp.getCurrentItem() + 1 + "/" + data.imglist.size() + data.coremark);
                auto_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        if (data.imglist.size() != 0) {
                            mContent.setText(position + 1 + "/" + data.imglist.size() + " " + data.coremark);
                        }
                    }
                    @Override
                    public void onPageSelected(int position) {
                        if (DecorationStyleDetailsActivity.currentPage == 0 && position == 0) {
                            Tst.showToast("已是第一张图");
                        } else if (DecorationStyleDetailsActivity.maxPage == DecorationStyleDetailsActivity.currentPage + 1 && position == data.imglist.size() - 1) {
                            Tst.showToast("已是最后一张图");
                        }
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        },500);
    }
    @Override
    protected void initData() {
        adapter = new DecorationStyleFragmentPagerAdapter(getSupportFragmentManager(), styleIds);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);
        if (position >= styleIds.size()) {
            position = 0;
        }
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //当页面在滑动的时候会调用此方法，在滑动被停止之前一直调用,position:当前页面，及你点击滑动的页面;positionOffset:当前页面偏移的百分比;positionOffsetPixels:当前页面偏移的像素位置
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }
            @Override//代表哪个页面被选中
            public void onPageSelected(final int position) {
                setDetail(details.get(styleIds.get(position).inid));
                if (!isReqNext) return;
                if (position == adapter.getCount() - 1) {//已经滑动到末尾了,加载下一页的ids
                    if (isLoadMore) return;
                    isLoadMore = true;
                    RxRequestApi.getInstance().getApiService().getStyleIds(type, styleIds.get(position).inid, shopId)
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ResponseBody<List<StyleId>>>() {
                                @Override
                                public void call(ResponseBody<List<StyleId>> body) {
                                    isLoadMore = false;
                                    if (body.isSuccess() && body.data != null && body.data.size() > 0) {
                                        styleIds.addAll(body.data);
                                        adapter.notifyDataSetChanged();
                                        maxPage = styleIds.size();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    isLoadMore = false;
                                }
                            });
                }
            }

            @Override//当用手指滑动翻页时，手指按下去的时候会触发这个方法,0（什么都没做）,1(正在滑动) , 2(动完毕了) 。
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    public int getCurrentPage(){
        return viewPager.getCurrentItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Subscribe(tags = {@Tag(BusAction.HIDDED_TOP_LAYOUT)})
    public void hidden(String obj) {
        if("onclick".equals(obj)){
            boolean b = View.VISIBLE != scrollView.getVisibility();//false没有隐藏
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//切换到横屏
            }else{
            }
            mTopLayout.setVisibility(b ? View.VISIBLE : View.GONE);
            scrollView.setVisibility(b ? View.VISIBLE : View.GONE);
        }else {
            boolean b = View.VISIBLE != scrollView.getVisibility();//false没有隐藏
        }
    }
    @Override
    public void setCenterTitleView(TextView tv_center) {}
}
