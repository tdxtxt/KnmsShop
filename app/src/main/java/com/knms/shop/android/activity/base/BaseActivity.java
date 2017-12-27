package com.knms.shop.android.activity.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.knms.shop.android.R;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.view.progress.CircleProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/30.
 */

public abstract class BaseActivity extends Activity {
    CircleProgressDialog circleProgressDialog;
    public <T extends View> T findView(int id) {
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
        getParmas(getIntent());
        setContentView(layoutResID());
        initView();
        initData();
        RxBus.get().register(this);
    }
    protected void getParmas(Intent intent){};
    protected void reqApi(){};
    protected abstract int layoutResID();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract String umTitle();

    public void showProgress(){
        if(circleProgressDialog == null) circleProgressDialog = new CircleProgressDialog(this);
        if(circleProgressDialog.isShowing()) circleProgressDialog.dismiss();

        circleProgressDialog.showDialog();
    }
    public void hideProgress(){
        if(circleProgressDialog != null) circleProgressDialog.dismiss();
    }
    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        if (this != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        if (this != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(getCurrentFocus()
                    .getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
            overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        }
    }
    public void startActivityForResultAnimGeneral(Class activityClazz, Map<String, Object> param,int code) {
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
        MobclickAgent.onPageStart(umTitle());
        MobclickAgent.onResume(this) ;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(umTitle());
        MobclickAgent.onPause(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CommonHelper.STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    CommonHelper.callPhone(this);
                } else {
                    Tst.showToast("暂未获取到拨号权限");
                }
                break;
            default:
                break;
        }
    }
}
