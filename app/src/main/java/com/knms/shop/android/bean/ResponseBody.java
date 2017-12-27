package com.knms.shop.android.bean;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import com.knms.shop.android.activity.login.LoginActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.helper.ConstantObj;
import com.knms.shop.android.util.ToolsHelper;

import java.io.Serializable;

/**
 * Created by tdx on 2016/8/23.
 */
public class ResponseBody<T> implements Serializable {
    private static final long serialVersionUID = -8694746325578097513L;
    public String code;
    public String desc;
    public String message;
    public T data;
    public boolean isSuccess() {
        if(ConstantObj.LOGIN_OUT.equals(this.code)){
            ToolsHelper.getInstance().logout();
            Activity activity = KnmsShopApp.getInstance().currentActivity();
            if(activity != null && !activity.isFinishing()){
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                KnmsShopApp.getInstance().finishAllActivity();
            }
        }
        return ConstantObj.OK.equals(code);
    }
}
