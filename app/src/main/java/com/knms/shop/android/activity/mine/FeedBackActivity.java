package com.knms.shop.android.activity.mine;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/13.
 */
public class FeedBackActivity extends HeadBaseActivity {
    private EditText mConten;
    private TextView mNumber, mSubmit;

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("意见反馈");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initView() {
        mConten = (EditText) findViewById(R.id.feedback_content);
        mNumber = (TextView) findViewById(R.id.tv_number);
        mSubmit = (TextView) findViewById(R.id.submit_feedback);
    }

    @Override
    protected void initData() {
        mConten.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((mConten.getText() + "").equals("")) {
                    mSubmit.setBackgroundResource(R.drawable.bg_rectangle_gray);
                    mSubmit.setTextColor(0xff333333);
                } else {
                    mSubmit.setBackgroundResource(R.drawable.bg_rectangle_btn);
                    mSubmit.setTextColor(0xff999999);
                }
                mNumber.setText(editable.length() + "/200");
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publishContent=mConten.getText().toString();
                if(TextUtils.isEmpty(publishContent)){
                    Tst.showToast("请输入发表内容");
                    return;
                }
                showProgress();
                RxRequestApi.getInstance().getApiService().feedback(mConten.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody responseBody) {
                                hideProgress();
                                Tst.showToast(responseBody.desc);
                                finshActivity();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                hideProgress();
                                Tst.showToast(throwable.getMessage());
                            }
                        });
            }
        });
    }

    @Override
    protected String umTitle() {
        return "意见反馈";
    }


}
