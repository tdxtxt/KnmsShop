package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.order.ShowImagesAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.CompainDetail;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ScreenUtil;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * 投诉详情
 * Created by 654654 on 2017/5/2.
 */

public class CompainDetailActivity extends HeadBaseActivity {

    private TextView time,phone,contacts,type,content;
    private RecyclerView imgs;
    private ScrollView scrollView;
    public static final String KEY_COMPLAINTSID = "complaintsId";
    @Override
    protected int layoutResID() {
        return R.layout.activity_compain_detail;
    }

    @Override
    protected void initView() {
        scrollView = findView(R.id.scrollView);
        time = findView(R.id.time);
        phone = findView(R.id.phone);
        contacts = findView(R.id.contacts);
        type = findView(R.id.type);
        content = findView(R.id.content);
        imgs = findView(R.id.imgs);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    private String complaintsId;//投诉id
    @Override
    protected void getParmas(Intent intent) {
        if (null != intent)
            complaintsId = intent.getStringExtra(KEY_COMPLAINTSID);
    }

    @Override
    protected void initData() {
        reqApi();
    }

    @Override
    protected void reqApi() {
        RxRequestApi.getInstance().getApiService().getCompainDetail(complaintsId).compose(this.<ResponseBody<CompainDetail>>applySchedulers())
                .subscribe(new Action1<ResponseBody<CompainDetail>>() {
                    @Override
                    public void call(ResponseBody<CompainDetail> compainDetailResponseBody) {
                        if (compainDetailResponseBody.isSuccess()){
                            updateView(compainDetailResponseBody.data);
                        }
                    }
                });
    }

    private void updateView(CompainDetail data) {
        if (null == data)
            return;
        time.setText(data.occreated);
        phone.setText(TextUtils.isEmpty(data.ocrelationmobile)?"":data.ocrelationmobile.substring(0,3)+"****"+data.ocrelationmobile.substring(data.ocrelationmobile.length()-4));
        contacts.setText(data.ocrelationname);
        type.setText(data.occomplaintstype);
        content.setText(data.occontent);
        if (null != data.imgList){
            ArrayList<String> imgData = new ArrayList<String>();
            for (CompainDetail.ImgList img : data.imgList)
                imgData.add(img.imageUrl);
            ShowImagesAdapter adapter = new ShowImagesAdapter(imgData,true);
            adapter.setOnItemClickBefore(new ShowImagesAdapter.OnItemClickBefore() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MobclickAgent.onEvent(CompainDetailActivity.this,"orderStateCompainImage");
                }
            });
            adapter.setWidth((ScreenUtil.getScreenWidth()- LocalDisplay.dip2px(32)) / 4);
            imgs.setLayoutManager(new FullyGridLayoutManager(this,4));
            imgs.setAdapter(adapter);
        }
    }

    @Override
    protected String umTitle() {
        return "投诉详情";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("投诉详情");
    }
}
