package com.knms.shop.android.activity.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.knms.shop.android.R;

/**
 * Created by tdx on 2016/8/29.
 * 特此警告:继承该类请务必在布局文件中include【top_title_layout】布局中的控件
 */
public abstract class HeadBaseFragmentActivity extends BaseFragmentActivity{
    private RightCallBack callBack;
    protected FrameLayout fl_right;
    protected ImageView iv_icon_right;
    protected TextView tv_title_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initHeadView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHeadView() throws Exception{
        findView(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finshActivity();
            }
        });
        TextView tv_title_center = findView(R.id.tv_title_center);
        tv_title_right = findView(R.id.tv_title_right);
        iv_icon_right = findView(R.id.iv_icon_right);
        fl_right = findView(R.id.fl_right);
        setCenterTitleView(tv_title_center);
        if(callBack != null){
            fl_right.setVisibility(View.VISIBLE);
            callBack.setRightContent(tv_title_right,iv_icon_right);
            fl_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onclick();
                }
            });
        }else{
            fl_right.setVisibility(View.GONE);
        }
    }

    protected void setRightVisibility(int visibility){
        fl_right.setVisibility(visibility);
    }
    protected void setRightIcon(@DrawableRes int res){
        iv_icon_right.setImageResource(res);
    }
    public abstract void setCenterTitleView(TextView tv_center);
    public void onRightMenuCallBack(RightCallBack callBack) {
        this.callBack = callBack;
    }

    public interface RightCallBack{
        public void setRightContent(TextView tv, ImageView icon);
        public void onclick();
    }
}
