package com.knms.shop.android.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import com.knms.shop.android.R;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.view.progress.CircleProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tdx on 2016/8/29.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    CircleProgressDialog circleProgressDialog;
    protected <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }
    public <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>(){
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KnmsShopApp.getInstance().addActivity(this);
        getParams(getIntent());
        setContentView(layoutResID());
        initView();
        initData();
        RxBus.get().register(this);
    }
    protected void getParams(Intent intent){};
    protected void reqApi(){};
    protected abstract int layoutResID();
    protected abstract void initView();
    protected abstract void initData();
    public void showProgress(){
        if(circleProgressDialog == null) circleProgressDialog = new CircleProgressDialog(this);
        if(circleProgressDialog.isShowing()) circleProgressDialog.dismiss();

        circleProgressDialog.showDialog();
    }
    public void hideProgress(){
        if(circleProgressDialog != null) circleProgressDialog.dismiss();
    }
    public void startActivityAnimGeneral(Class activityClazz, Map<String, Object> param) {
        Intent intent = new Intent(this, activityClazz);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除堆栈顶部的
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                Object obj = entry.getValue();
                if (obj != null) {
                    if (obj instanceof String) {
                        intent.putExtra(entry.getKey(),
                                (String) entry.getValue());
                    } else if (obj instanceof Integer) {
                        intent.putExtra(entry.getKey(),
                                (Integer) entry.getValue());
                    } else {
                        try {
                            intent.putExtra(entry.getKey(),
                                    (Serializable) entry.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        startActivity(intent);
        if (getParent() != null) {
            getParent().overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        } else {
            overridePendingTransition(R.anim.in_from_right,
                    R.anim.out_to_left);
        }
    }
    public void startActivityForResultAnimGeneral(Class activityClazz, Map<String, Object> param, int code) {
        Intent intent = new Intent(this, activityClazz);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除堆栈顶部的
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                Object obj = entry.getValue();
                if (obj != null) {
                    if (obj instanceof String) {
                        intent.putExtra(entry.getKey(),
                                (String) entry.getValue());
                    } else if (obj instanceof Integer) {
                        intent.putExtra(entry.getKey(),
                                (Integer) entry.getValue());
                    } else {
                        try {
                            intent.putExtra(entry.getKey(),
                                    (Serializable) entry.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        startActivityForResult(intent,code);
        if (getParent() != null) {
            getParent().overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        } else {
            overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finshActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
    public void finshActivity(){
        KnmsShopApp.getInstance().finishActivity(this);
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
    @Override
    protected void onDestroy() {
        RxBus.get().unregister(this);
        if(circleProgressDialog != null && circleProgressDialog.isShowing()){
            circleProgressDialog.dismiss();
            circleProgressDialog = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this) ;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
