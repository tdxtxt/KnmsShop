package com.knms.shop.android.activity.mine;

import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.util.SystemInfo;

/**
 * Created by Administrator on 2016/10/21.
 */
public class AboutActivity extends HeadBaseActivity {
    private TextView tvCall, tvVersions;

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("关于我们");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_about;
    }


    @Override
    protected void initView() {
        tvCall = (TextView) findViewById(R.id.tv_call);
        tvVersions = (TextView) findViewById(R.id.tv_ver_name);
        tvCall.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void initData() {
        tvVersions.setText("铠恩买手 . 卖家中心"+SystemInfo.getVerName());
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.onCallPhone(AboutActivity.this,CommonHelper.CSphone);
            }
        });
    }

    @Override
    protected String umTitle() {
        return "关于我们";
    }

}
