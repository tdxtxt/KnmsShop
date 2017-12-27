package com.knms.shop.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.knms.shop.android.activity.back.HooliganActivity;

/**
 * Created by Administrator on 2017/7/11.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            HooliganActivity. startHooligan();
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            HooliganActivity. killHooligan();
        }
    }
}
