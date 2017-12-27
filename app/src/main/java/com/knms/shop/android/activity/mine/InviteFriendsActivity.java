package com.knms.shop.android.activity.mine;

import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.CleanableEditText;
import com.umeng.analytics.MobclickAgent;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/31.
 */

public class InviteFriendsActivity extends HeadBaseActivity {
    private CleanableEditText mPhotoNumber;
    private TextView mSubmit,mLookAllInviterFriends;

    @Override
    protected int layoutResID() {
        return R.layout.activity_invite_friends_layout;
    }

    @Override
    protected void initView() {
        mPhotoNumber = findView(R.id.ed_invitee_tel);
        mSubmit = findView(R.id.btn_submit);
        mLookAllInviterFriends=findView(R.id.tv_lookall_inviter);
        mLookAllInviterFriends.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override
    protected void initData() {
        mPhotoNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSubmit.setClickable(mPhotoNumber.getText().toString().length() >= 11);
                if (StrHelper.isMobileNO(mPhotoNumber.getText()+"")) {
                    mSubmit.setBackgroundResource(R.drawable.bg_rectangle_btn);
                    mSubmit.setTextColor(0xff333333);
                } else {
                    mSubmit.setTextColor(0xff999999);
                    mSubmit.setBackgroundResource(R.drawable.bg_rectangle_gray);
                }
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(InviteFriendsActivity.this,"inviteFriendsSubmitBtnOnclick");
                hideKeyboard();
                if (!StrHelper.isMobileNO(mPhotoNumber.getText()+""))
                    Tst.showToast("请输入正确的手机号");
                else reqApi();
            }
        });
        mLookAllInviterFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(InviteFriendsActivity.this,"lookAllInviterFriends");
                startActivityAnimGeneral(AllInviterActivity.class,null);
            }
        });
        findView(R.id.ll_rootview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }

    @Override
    protected void reqApi() {
        RxRequestApi.getInstance().getApiService().saveInviter(mPhotoNumber.getText()+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<String>>() {
                    @Override
                    public void call(ResponseBody<String> stringResponseBody) {
                        showDialog(stringResponseBody.desc);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Tst.showToast(throwable.getMessage());
                    }
                });

    }

    private void showDialog(String content){
        DialogHelper.showPromptDialog(this, "", content, "", "确定", "", new DialogHelper.OnMenuClick() {
            @Override
            public void onLeftMenuClick() {

            }

            @Override
            public void onCenterMenuClick() {

            }

            @Override
            public void onRightMenuClick() {

            }
        });
    }

    @Override
    protected String umTitle() {
        return "邀请好友";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("邀请好友");
    }
}
