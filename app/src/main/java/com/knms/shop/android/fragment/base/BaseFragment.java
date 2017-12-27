package com.knms.shop.android.fragment.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
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
 * Created by tdx on 2016/8/29.
 */
public abstract class BaseFragment extends Fragment {
    public BaseFragmentActivity mActivity;
    public <T extends View> T findView(View view,int id) {
        return (T) view.findViewById(id);
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
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof BaseFragmentActivity){
            mActivity = (BaseFragmentActivity) context;
        }
    }
    CircleProgressDialog circleProgressDialog;
    public void showProgress(){
        if(mActivity == null) return;
        if(circleProgressDialog == null) circleProgressDialog = new CircleProgressDialog(this.mActivity);
        if(circleProgressDialog.isShowing()) circleProgressDialog.dismiss();

        circleProgressDialog.showDialog();
    }
    public void hideProgress(){
        if(mActivity == null) return;
        if(circleProgressDialog != null) circleProgressDialog.dismiss();
    }
    public BaseFragmentActivity getmActivity() {
        return mActivity;
    }
    public void reqApi(){}
    public String getTitle(){return "";}
    protected abstract String umTitle();

    public void startActivityAnimGeneral(Class activityClazz, Map<String, Object> param) {
        if(getmActivity() == null) return;
        Intent intent = new Intent(getmActivity(), activityClazz);
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
        if (getmActivity() != null) {
            getmActivity().overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        } else {
            getmActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        }
    }
    public void startActivityForResultAnimGeneral(Class activityClazz, Map<String, Object> param, int code) {
        if(getmActivity() == null) return;
        Intent intent = new Intent(getmActivity(), activityClazz);
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
        if (getmActivity().getParent() != null) {
            getmActivity().overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        } else {
            getmActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(umTitle()))
        MobclickAgent.onPageStart(umTitle());
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!TextUtils.isEmpty(umTitle()))
        MobclickAgent.onPageEnd(umTitle());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CommonHelper.STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    CommonHelper.callPhone(getActivity());
                } else {
                    Tst.showToast("暂未获取到拨号权限");
                }
                break;
            default:
                break;
        }
    }
}
