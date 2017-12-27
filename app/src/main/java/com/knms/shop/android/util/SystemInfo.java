package com.knms.shop.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.knms.shop.android.BuildConfig;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.helper.DeviceIDFactory;

import java.util.Locale;

/**
 * Created by Administrator on 2016/10/5.
 */

public class SystemInfo {
    //没有网络连接
    public static final int NETWORN_NONE = 0;
    //wifi连接
    public static final int NETWORN_WIFI = 1;
    //手机网络数据连接类型
    public static final int NETWORN_2G = 2;
    public static final int NETWORN_3G = 3;
    public static final int NETWORN_4G = 4;
    public static final int NETWORN_MOBILE = 5;

    /**
     * 获取当前网络连接类型
     * @return
     */
    public static int getNetworkState() {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) KnmsShopApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return NETWORN_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || (activeNetInfo != null && !activeNetInfo.isAvailable())) {
            return NETWORN_NONE;
        }
        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }

    public static String getSDK() {
        return android.os.Build.VERSION.SDK; // SDK号

    }

    public static String getModel(){ // 手机型号
        return android.os.Build.MODEL;
    }

    public static String getRelease(){ // android系统版本号
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getImei(){ // 获取手机身份证imei

        TelephonyManager telephonyManager = (TelephonyManager) KnmsShopApp.getInstance()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getVerSerName() { // 获服务端版本号
        return BuildConfig.VERSION_NAME;
    }
    public static String getVerSerCode() { // 获服务端版本号
        return BuildConfig.SER_VERSION_CODE;
    }
    public static String getVerName() { // 获取版本名字
        try {
            String pkName = KnmsShopApp.getInstance().getPackageName();
            String versionName = KnmsShopApp.getInstance().getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }
    public static int getVerCode() { // 获取版本号
        String pkName = KnmsShopApp.getInstance().getPackageName();
        try {
            int versionCode = KnmsShopApp.getInstance().getPackageManager().getPackageInfo(
                    pkName, 0).versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static String getMac() { // 获取时机mac地址
        final WifiManager wifi = (WifiManager) KnmsShopApp.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            if (info.getMacAddress() != null) {
                return info.getMacAddress().toLowerCase(Locale.ENGLISH)
                        .replace(":", "");
            }
            return "";
        }
        return "";
    }
    public static ApplicationInfo getAppInfo(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = KnmsShopApp.getInstance().getPackageManager()
                    .getApplicationInfo(KnmsShopApp.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            return appInfo;
        } catch (PackageManager.NameNotFoundException e) {}
        return null;
    }
    public static String getMeta(String key){
        if (TextUtils.isEmpty(key))
            return null;
        ApplicationInfo appInfo = getAppInfo();
        if (null == appInfo) return null;
        return appInfo.metaData.getString(key);
    }
    public static Boolean getMetaBool(String key){
        if (TextUtils.isEmpty(key))
            return null;
        ApplicationInfo appInfo = getAppInfo();
        if (null == appInfo) return null;
        return appInfo.metaData.getBoolean(key,true);
    }
    public static String getDeviceID(){
        return DeviceIDFactory.getInstance().getUniqueID();
    }
}
