package com.knms.shop.android.fragment.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.knms.shop.android.R;

/**
 * Created by Administrator on 2017/3/27.
 * 使用须知:需要使用top_title_layout布局
 */

public abstract class BaseHeadFragment extends BaseFragment{
    private String title,rightText;
    private int resId;
    public void setTitle(String title){
        this.title = title;
    }
    public void setRightText(String rightText){
        this.rightText = rightText;
    }
    public void setRightIcon(@DrawableRes int resId){
        this.resId = resId;
    }
    @Override
    protected String umTitle() {
        return this.title;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView iv_back = findView(view,R.id.iv_back);
        TextView tv_title = findView(view, R.id.tv_title_center);
        TextView tv_rightText = findView(view,R.id.tv_title_right);
        ImageView iv_icon_right = findView(view,R.id.iv_icon_right);
        if(TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.GONE);
        }else {
            tv_title.setText(title);
        }
        if(TextUtils.isEmpty(rightText)){
            tv_rightText.setVisibility(View.GONE);
            if(resId != 0){
                iv_icon_right.setImageResource(resId);
            }else{
                iv_icon_right.setVisibility(View.GONE);
            }
        }else{
            tv_rightText.setVisibility(View.VISIBLE);
            tv_rightText.setText(rightText);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmActivity().finshActivity();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
