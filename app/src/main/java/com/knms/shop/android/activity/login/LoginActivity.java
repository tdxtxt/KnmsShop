package com.knms.shop.android.activity.login;

import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseActivity;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.DeviceIDFactory;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.view.CleanableEditText;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/12.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, CleanableEditText.TextWatcherCallBack {
    private CleanableEditText mPhone;
    private CleanableEditText mPwd;
    private TextView mDial, mLogin;
    private CheckBox checkbox;
    private static final int MSG_SET_ALIAS = 1001;

    @Override
    protected int layoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mPhone = (CleanableEditText) findViewById(R.id.tv_phone_number);
        mPwd = (CleanableEditText) findViewById(R.id.tv_pwd);
        mDial = (TextView) findViewById(R.id.tv_dial);
        mDial.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mLogin = (TextView) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
        mDial.setOnClickListener(this);
        mPhone.addTextChangedListener(mTextWatcher);
        mPwd.addTextChangedListener(mTextWatcher);
        checkbox = findView(R.id.pwd_visible);
    }

    @Override
    protected void initData() {
//        mPhone.setText("DRKAZ353698540125");
//        mPwd.setText("kn123456");
        mPhone.setText(SPUtils.getCurrentUserName());
        mPhone.setSelection(SPUtils.getCurrentUserName().length());
        mPwd.setText(SPUtils.getCurrentUserPwd());
        mPwd.setSelection(SPUtils.getCurrentUserPwd().length());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//可见
                    mPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {//不可间
                    mPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPwd.setSelection(mPwd.getText().length());
            }
        });
    }

    @Override
    protected String umTitle() {
        return "登录";
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(mPhone.getText().toString()) && !TextUtils.isEmpty(mPwd.getText().toString())) {
                mLogin.setBackgroundResource(R.drawable.bg_rectangle_btn);
                mLogin.setTextColor(0xff333333);
            } else {
                mLogin.setTextColor(0xff999999);
                mLogin.setBackgroundResource(R.drawable.bg_rectangle_gray);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_dial:
                CommonHelper.onCallPhone(this,CommonHelper.CSphone);
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    Subscription subscription;

    private void login() {
        final String username = mPhone.getText().toString();
        final String password = mPwd.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return;
        }
        showProgress();
        subscription = RxRequestApi.getInstance().getApiService().login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<User>>() {
                    @Override
                    public void call(ResponseBody<User> body) {
                        hideProgress();
                        if (body.isSuccess()) {
                            if (body.data != null){
                                IMHelper.getInstance().login(body.data.id, body.data.token);
                                mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, body.data.pushtoken));
                            }
                            User user = body.data;
                            user.accountumber = username;
                            SPUtils.saveUser(body.data);
                            SPUtils.saveLoginState(true);
                            SPUtils.saveCurrentUserName(username);
                            SPUtils.saveCurrentUserPwd(password);
                            RxRequestApi.getInstance().getApiService().uploadOpenRecord(DeviceIDFactory.getInstance().getUniqueID())
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<ResponseBody>() {
                                        @Override
                                        public void call(ResponseBody body) {
                                            startActivityAnimGeneral(MainActivity.class, null);
                                            finshActivity();}
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            startActivityAnimGeneral(MainActivity.class, null);
                                            finshActivity();}
                                    });
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

    @Override
    public void handleMoreTextChanged() {
        Log.e("tag", mPhone.getText() + "");
    }

    @Override
    protected void onDestroy() {
        if (subscription != null) subscription.unsubscribe();
        super.onDestroy();
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.e("jpus", "Set alias in handler."+msg.obj);
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                default:
                    Log.i("jpus", "Unhandled msg - " + msg.what);
            }
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.e("jpush", logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e("jpush", logs);
                    ConnectivityManager conn = (ConnectivityManager) LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = conn.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.e("jpush", "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("jpush", logs);
            }
        }

    };
}
