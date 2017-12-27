package com.knms.shop.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.body.account.User;
import com.orhanobut.hawk.Hawk;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2016/8/23.
 */
public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static final String APP_COMMON_FILE_NAME = "Knms";
    private static final String ACCOUNT_FILE_NAME = "AccountCommon";

    public static boolean saveToApp(String key, Object object) {
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(APP_COMMON_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        return editor.commit();
    }
    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getFromApp(String key, Object defaultObject) {
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(APP_COMMON_FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    private static boolean saveToAccount(String key, Object object) {
        String currentAccount = getCurrentMobile();
        if(!TextUtils.isEmpty(key) && !key.endsWith(currentAccount)) key = key +  "_" + currentAccount;
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        // SharedPreferencesCompat.apply(editor);
        return editor.commit();
    }

    public static String getCurrentMobile(){
        return (String) SPUtils.getFromApp(KeyConstant.currentMobile, "");
    }
    public static void saveCurrentMoblie(String moblie){
        SPUtils.saveToApp(KeyConstant.currentMobile,moblie);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key
     * @param defaultObject
     * @return
     */
    private static Object getFromAccount(String key, Object defaultObject) {
        String currentAccount = (String) SPUtils.getFromApp(KeyConstant.currentMobile, "youke");
        if(!TextUtils.isEmpty(key) && !key.endsWith(currentAccount)) key = key +  "_" + currentAccount;
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 序列化对象
     *
     * @return
     * @throws IOException
     */
    private static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }
    /**
     * 反序列化对象
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object deSerialization(String str) throws IOException, ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }
    public static boolean saveSerializable(String key, Object obj){
        String str = "";
        try {
            str = serialize(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SPUtils.saveToApp("key_"  + key + obj.getClass().getSimpleName(), str);
    }
    public static boolean saveRingStatus(boolean status){
        return SPUtils.saveToApp("RingStatus",status);
    }
    public static boolean getRingStatus(){
        return (boolean)SPUtils.getFromApp("RingStatus",true);
    }
    public static boolean saveNotificationStatus(boolean status){
        return SPUtils.saveToApp("notificationStatus",status);
    }
    public static boolean getNotificationStatus(){
        return (boolean)SPUtils.getFromApp("notificationStatus",true);
    }
    public static boolean clearSerializable(String key, Class clazz){
        return clearKey("key_" + key + clazz.getSimpleName());
    }

    public static <T>T getSerializable(String key, Class<T> clazz) {
        T obj = null;
        String str = (String) SPUtils.getFromApp("key_" + key + clazz.getSimpleName() , "");
        try {
            obj = (T) deSerialization(str);
        } catch (Exception e) {
            e.printStackTrace();
            return obj;
        }
        return obj;
    }
    public static boolean saveUser(User user){
        if(user != null){
            MobclickAgent.onProfileSignIn(user.accountumber);
            return Hawk.put("user",user);
        }
        return false;
    }

    public static void saveCurrentUserName(String userName){
        SPUtils.saveToAccount(KeyConstant.account,userName);
    }
    public static void saveCurrentUserPwd(String pwd){
        SPUtils.saveToAccount(KeyConstant.pwd,pwd);
    }

    public static String getCurrentUserName(){
        return SPUtils.getFromAccount(KeyConstant.account,"").toString();
    }
    public static String getCurrentUserPwd(){
        return SPUtils.getFromAccount(KeyConstant.pwd,"").toString();
    }

    public static User getUser(){
        return Hawk.get("user");
    }
    public static void saveLoginState(boolean loginState){
        SPUtils.saveToApp(SPUtils.KeyConstant.loginState,loginState);
    }
    public static boolean isLogin(){
        return (Boolean) SPUtils.getFromApp(SPUtils.KeyConstant.loginState, false);
    }
    /**
     * 清除所有数据
     */
    public static void clear() {
        String mobllie = getCurrentMobile();
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(APP_COMMON_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        saveCurrentMoblie(mobllie);
    }
    /**
     * 清除key数据
     */
    public static boolean clearKey(String key) {
        SharedPreferences sp = KnmsShopApp.getInstance().getSharedPreferences(APP_COMMON_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }
    public static class KeyConstant{
        public static String account = "account";
        public static String pwd = "pwd";
        public static String knmsid = "knmsid";
        public static String loginState = "loginState";
        public static String currentMobile = "currentMobile";
        public static String currentUser = "user";
        public static String searchHistory = "searchHistory";
        public static String imAccount = "imAccount";
        public static String time="time";
    }


}
