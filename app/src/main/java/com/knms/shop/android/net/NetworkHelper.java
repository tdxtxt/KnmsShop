package com.knms.shop.android.net;

import com.knms.shop.android.util.SystemInfo;

/**
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017/12/1 14:01
 * 传参：
 * 返回:
 */
public class NetworkHelper {
    public static boolean isNetwork(){
        return SystemInfo.getNetworkState() != SystemInfo.NETWORN_NONE;
    }
}
