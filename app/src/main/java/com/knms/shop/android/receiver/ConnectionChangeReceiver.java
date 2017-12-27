package com.knms.shop.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.util.SystemInfo;
import static com.knms.shop.android.util.SystemInfo.NETWORN_NONE;

/**
 * Created by tdx on 2016/12/8.
 * 开机&网络状态改变触发广播
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(SystemInfo.getNetworkState() != NETWORN_NONE){
            IMHelper.getInstance().relogin();
        }
    }
}
