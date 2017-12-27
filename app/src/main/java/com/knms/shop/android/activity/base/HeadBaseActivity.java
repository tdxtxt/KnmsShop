package com.knms.shop.android.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.helper.ConstantObj;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by tdx on 2016/9/1.
 * 特此警告:继承该类请务必在布局文件中include【top_title_layout】布局中的控件
 * initView 中添加super.initView();
 */
public abstract class HeadBaseActivity extends BaseActivity{
    private RightCallBack callBack;
    protected TextView tv_title_center;
    protected TextView tv_title_right;
    protected ImageView iv_icon_right;
    public Subscription subscriptionMsgCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initHeadView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showImMsg(boolean show){
        if(subscriptionMsgCount != null) subscriptionMsgCount.unsubscribe();
        if (!show){
            findView(R.id.fl_right).setVisibility(View.INVISIBLE);
            return;
        }
        findView(R.id.fl_right).setVisibility(View.VISIBLE);
        if(tv_title_right == null) tv_title_right = findView(R.id.tv_title_right);
        if(iv_icon_right == null) iv_icon_right = findView(R.id.iv_icon_right);
        iv_icon_right.setImageResource(R.drawable.chat_b);
        findView(R.id.fl_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> params =  new HashMap<>();
                params.put(ConstantObj.IM_SELECT,2);
                startActivityAnimGeneral(MainActivity.class,params);
                finish();
            }
        });
        subscriptionMsgCount = IMHelper.getInstance().isUnreadMsg().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                iv_icon_right.setImageResource(aBoolean ? R.drawable.chat_red_b : R.drawable.chat_b);
            }
        });
    }


    private void initHeadView() throws Exception {
        findView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finshActivity();
            }
        });
        tv_title_center = findView(R.id.tv_title_center);
        tv_title_right = findView(R.id.tv_title_right);
        iv_icon_right = findView(R.id.iv_icon_right);
        FrameLayout fl_right = findView(R.id.fl_right);
        setCenterTitleView(tv_title_center);
        if(callBack != null){
            fl_right.setVisibility(View.VISIBLE);
//            callBack.setRightContent(tv_title_right,iv_icon_right);
//            fl_right.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    callBack.onclick();
//                }
//            });
        }else{
            fl_right.setVisibility(View.INVISIBLE);
        }
    }

    public abstract void setCenterTitleView(TextView tv_center);

    public void setRightMenuCallBack(RightCallBack callBack) {
        this.callBack = callBack;
        if(callBack != null){
            findView(R.id.fl_right).setVisibility(View.VISIBLE);
            if(tv_title_right == null) tv_title_right = findView(R.id.tv_title_right);
            if(iv_icon_right == null) iv_icon_right = findView(R.id.iv_icon_right);
            callBack.setRightContent(tv_title_right,iv_icon_right);
            findView(R.id.fl_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HeadBaseActivity.this.callBack.onclick();
                }
            });
        }else{
            findView(R.id.fl_right).setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onDestroy() {
        if(subscriptionMsgCount != null) subscriptionMsgCount.unsubscribe();
        super.onDestroy();
    }
    public interface RightCallBack{
            void setRightContent(TextView tv, ImageView icon);
            void onclick();
    }
}
