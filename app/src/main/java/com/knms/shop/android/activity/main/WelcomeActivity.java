package com.knms.shop.android.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.login.LoginActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.core.upgrade.UpdateHelper;
import com.knms.shop.android.core.upgrade.listener.OnUpdateListener;
import com.knms.shop.android.util.SPUtils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tdx on 2016/10/19.
 */

public class WelcomeActivity extends Activity implements OnUpdateListener{
    private long calc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        calc = System.currentTimeMillis();
        UpdateHelper updateHelper = new UpdateHelper.Builder(this)
                .isAutoInstall(false)
                .isThinkTime(false)
                .build();
        updateHelper.check(this);
    }

    // 处理收到的Intent
    private void onIntent() {
        if(SPUtils.isLogin()){
            startActivity(new Intent(this,MainActivity.class));
        }else{
            JPushInterface.setAliasAndTags(KnmsShopApp.getInstance(), "", null,null);//防止意外情况
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }


    @Override
    public void noUpdata() {
        long diff = System.currentTimeMillis() - calc;
        if(diff > 2000){
            onIntent();
        }else {
            Observable.just("").delay(2000 - diff, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String aLong) {
                            onIntent();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            onIntent();
                        }
                    });
        }
    }
    @Override
    public void nextUpdata() {
        noUpdata();
    }
    @Override
    public void onfail() {
        noUpdata();
    }
}
