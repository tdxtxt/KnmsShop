package com.knms.shop.android.activity.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.activity.login.LoginActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.ToolsHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/14.
 */
public class UserInfoActivity extends HeadBaseActivity implements View.OnClickListener {

    private RelativeLayout mUpdatePwdLayout;
    private TextView mLogOut,mNickname,mType;

    @Override
    protected int layoutResID() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView() {
        mUpdatePwdLayout = (RelativeLayout) findViewById(R.id.update_pwd);
        mLogOut = (TextView) findViewById(R.id.log_out);
        mNickname= (TextView) findViewById(R.id.nickname);
        mType= (TextView) findViewById(R.id.type);
    }

    @Override
    protected void initData() {
        User user = SPUtils.getUser();
        if(user != null){
            ImageLoadHelper.getInstance().displayImageHead(user.shoppic, (ImageView) findViewById(R.id.head_img));
            mNickname.setText(user.accountumber);
            mType.setText(user.shopname);
        }
        mUpdatePwdLayout.setOnClickListener(this);
        mLogOut.setOnClickListener(this);
    }

    @Override
    protected String umTitle() {
        return "个人信息";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_pwd:
                startActivityAnimGeneral(UpdatePwdActivity.class, null);
                break;
            case R.id.log_out:
                DialogHelper.showPromptDialog(this, "确定退出登录", "", "取消", "", "确定", new DialogHelper.OnMenuClick() {
                    @Override
                    public void onLeftMenuClick() {}
                    @Override
                    public void onCenterMenuClick() {}
                    @Override
                    public void onRightMenuClick() {
//                        logout();
                        ToolsHelper.getInstance().logout();
                        startActivityAnimGeneral(LoginActivity.class, null);
                        KnmsShopApp.getInstance().finishAllActivity();
                    }
                });
                break;
        }
    }

    private void logout() {
        RxRequestApi.getInstance().getApiService().logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        if (!responseBody.isSuccess()) {
                            Tst.showToast(responseBody.desc);
                        } else {
                            IMHelper.getInstance().logout();
                            SPUtils.clear();
                            startActivityAnimGeneral(LoginActivity.class, null);
                            KnmsShopApp.getInstance().finishAllActivity();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Tst.showToast(throwable.getMessage());
                    }
                });
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("个人信息");
    }
}
