package com.knms.shop.android.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.details.BBpriceDetailsActivity;
import com.knms.shop.android.activity.details.CustomFurnitureDetailsActivity;
import com.knms.shop.android.activity.details.ProductDetailsActivity;
import com.knms.shop.android.activity.details.style.DecorationStyleDetailsActivity;
import com.knms.shop.android.activity.im.ChatActivity;
import com.knms.shop.android.activity.im.KnmsChatActivity;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.activity.mine.CommWebViewActivity;
import com.knms.shop.android.activity.order.OrderDetailActivity;
import com.knms.shop.android.activity.order.OrderListActivity;
import com.knms.shop.android.activity.orderpay.OrderPayDetailActivity;
import com.knms.shop.android.activity.orderpay.OrderPayGoodsDetailActivity;
import com.knms.shop.android.activity.orderpay.OrderPayListActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.core.im.msg.Product;
import com.knms.shop.android.net.HttpConstant;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.ToolsHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/1.
 */
public class CommonHelper {
    /**
     * push推送跳转规则
     */
    public static void startActivity(Context activity, String json) {
        Intent intent = setClass(activity,null,json);
        if (null != intent) activity.startActivity(intent);
    }
    public static Intent setClass(Context context,Intent intent, String json){
        if (TextUtils.isEmpty(json)) return intent;
        Map<String, Object> map = jsonToMap(json);
        if (map.get("module") == null) return intent;
        String module = map.get("module").toString();
        Map<String, Object> parameter;
        if (null == intent )intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!TextUtils.isEmpty(map.get("parameter").toString())){
            String content = map.get("parameter").toString();
            parameter = jsonToMap(content);
        } else {
            parameter = new HashMap<>();
        }
        /*if ("home".equals(module)) {//首页
            if (!(context instanceof MainActivity))
                intent.setClass(context,MainActivity.class);
        } else */if("newsknmsone".equals(module)){//凯恩买手详情
            intent.setClass(context, KnmsChatActivity.class);
        } else if("html".equals(module)){//跳转webview
            String url = parameter.get("url").toString();
            intent.putExtra("url",url);
            intent.setClass(context, CommWebViewActivity.class);
        } else if("mer_parityone".equals(module)){//比比货详情
            String id = parameter.get("goid").toString();
            if(context instanceof MainActivity){
                ((MainActivity) context).putBBpriceDetails(id);
            }else{
                intent.setClass(context, BBpriceDetailsActivity.class);
                intent.putExtra("id",id);
            }
        }else if("merchantorderlist".equals(module)){//跳入进商家端的订单管理
            int state;
            String sstate = parameter.get("state").toString();
            if (TextUtils.isEmpty(sstate)){
                state = 0;
            }else {
                state = Integer.parseInt(sstate);
            }
            intent.putExtra("state",state);
            intent.setClass(context, OrderListActivity.class);
        }else if("merorderstate".equals(module)){//跳入进商家端的订单状态
            String orderId = parameter.get("orderId").toString();
            intent.putExtra("orderId",orderId);
            intent.setClass(context, OrderDetailActivity.class);
        }else if ("mallorderslist".equals(module)){//支付订单列表
            int orderTypeStatus;
            String sStat = parameter.get("orderTypeStatus").toString();
            if (TextUtils.isEmpty(sStat)){
                orderTypeStatus = 0;
            }else {
                orderTypeStatus = Integer.parseInt(sStat);
            }
            MobclickAgent.onEvent(context,"orderPayPush");
            intent.putExtra("state",orderTypeStatus);
            intent.setClass(context, OrderPayListActivity.class);
        }else if ("mallordersone".equals(module)){//支付订单详情
            MobclickAgent.onEvent(context,"orderPayPush");
            intent.putExtra(OrderPayDetailActivity.KEY_TRADINGID,parameter.get("tradingid").toString());
            intent.setClass(context, OrderPayDetailActivity.class);
        }else{
            if (!(context instanceof MainActivity))
                intent.setClass(context,MainActivity.class);
        }
        return intent;
    }
    /**
     * 聊天详情链接跳转定义
     */
    public static void startActivity(Context context, Product product){
        if(product == null) return;
        String productType = product.productType;
        Intent intent = new Intent();
        if("1".equals(productType)){//家装风格详情
            String decorateId = product.productId;
//            Map<String,Object> map = jsonToMap(product.attachJson);
//            String shopId = (String) map.get("shopId");
//            String type = (String) map.get("id");
            intent.setClass(context, DecorationStyleDetailsActivity.class);
            intent.putExtra("ids", (Serializable) Arrays.asList(decorateId));
            intent.putExtra("position", 0);
//            intent.putExtra("type", type);
            context.startActivity(intent);
        }else if("2".equals(productType)){//闲置详情
//            String goId = product.productId;
//            intent.putExtra("id", goId);
//            if(IMHelper.getInstance().getAccount().equals(product.userId)){//自己的闲置
//                intent.setClass(context, UndercarriageDetailsActivity.class);
//            }else {//别人的闲置
//                intent.setClass(context, IdleDetailsActivity.class);
//            }
//            context.startActivity(intent);
        }else if("3".equals(productType)){//定制家具详情
            String inId = product.productId;
            intent.setClass(context, CustomFurnitureDetailsActivity.class);
            intent.putExtra("goid", inId);
            context.startActivity(intent);
        }else if("4".equals(productType)){//爆款活动详情
            String goid = product.productId;
            intent.putExtra("goid",goid);
            intent.setClass(context, ProductDetailsActivity.class);
            context.startActivity(intent);
        }else if("5".equals(productType)){//分类商品详情
            String goId = product.productId;
            intent.putExtra("goid",goId);
            intent.setClass(context, ProductDetailsActivity.class);
            context.startActivity(intent);
        }else if("6".equals(productType)){//我的维修详情
//            String goId = product.productId;
//            intent.setClass(context, MineRepairDetailActivity.class);
//            intent.putExtra("id", goId);
//            context.startActivity(intent);
        }else if("7".equals(productType)){//我的比比货详情(个性比比货)
            String goId = product.productId;
            intent.setClass(context, BBpriceDetailsActivity.class);
            intent.putExtra("id", goId);
            context.startActivity(intent);
        }else if("8".equals(productType)){//商城商品
            intent.setClass(context, OrderPayGoodsDetailActivity.class);
            intent.putExtra(OrderPayGoodsDetailActivity.KEY_SHOWID, product.productId);
            context.startActivity(intent);
        }
    }
    /** 跳转聊天界面 */
    public static void goChat(Activity activity, String sid){
        if (TextUtils.isEmpty(sid))
            return;
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("sid", sid);
        activity.startActivity(intent);
    }
    /** 打电话 */
    public static void callPhone(Context context,String phoneNumber){
        if (TextUtils.isEmpty(phoneNumber))
            return;
        Intent it = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber.startsWith("tel:")? phoneNumber : "tel:"+phoneNumber));
        context.startActivity(it);
    }
    /** 申请权限后打电话 */
    public static void callPhone(Context context){
        callPhone(context,phoneNum);
    }
    /** 电话权限申请回调 */
    public static final int STORAGE_REQUEST_CODE = 122;
    /** 客服电话 */
    public static final String CSphone = "023-63317666";
    /** 记录申请权限前的电话 */
    public static  String phoneNum = "";
    /** 带弹出框的打电话 */
    public static void onCallPhone(final Activity activity, final String phoneNumber){
        phoneNum = "";
        phoneNum = phoneNumber;
        DialogHelper.showPromptDialog(activity, null, "是否拨打" + phoneNumber, "取消", null, "确定", new DialogHelper.OnMenuClick() {
            @Override
            public void onLeftMenuClick() {}
            @Override
            public void onCenterMenuClick() {}
            @Override
            public void onRightMenuClick() {//确定
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},
                            STORAGE_REQUEST_CODE);
                } else {
                    callPhone(activity, phoneNumber);
                }
            }
        });
    }
    public static Observable<User> getUser(){
        if(!SPUtils.isLogin()){
            return Observable.just(new User());
        }
        User user = SPUtils.getUser();
        if(user != null) return Observable.just(user).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        return RxRequestApi.getInstance().getApiService().getUser().map(new Func1<ResponseBody<User>, User>() {
            @Override
            public User call(ResponseBody<User> body) {
                if(body.isSuccess()){
                    SPUtils.saveUser(body.data);
                    return body.data;
                }
                return body.data;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
    public static View getEmptyView(Context context){
        View empty = LayoutInflater.from(context).inflate(R.layout.layout_view_no_data, null);
        ((ImageView)empty.findViewById(R.id.img_no_data)).setImageResource(R.drawable.no_data_on_offer);
        ((TextView)empty.findViewById(R.id.tv_no_data)).setText("暂时还没有数据哦~");
        return empty;
    }
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookie(String url) {
//        Uri uri = new Uri.Builder().build().parse(url);
        if(TextUtils.isEmpty(url)) return false;
        if(!(url.contains("kebuyer.com") || url.contains(HttpConstant.HOST))) return false;

        String knmsid = (String) SPUtils.getFromApp(SPUtils.KeyConstant.knmsid, "");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(KnmsShopApp.getInstance());
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, knmsid);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
        String newCookie = cookieManager.getCookie(url);
        return !TextUtils.isEmpty(newCookie);
    }
    /**
     * JsonObject转Map
     */
    public static Map<String, Object> jsonToMap(String jsonString){
        if(TextUtils.isEmpty(jsonString)) return new HashMap<>();
        Map<String, Object> jsonMap = new TreeMap<String, Object>();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return jsonMap;
        }
        Iterator<?> jsonKeys = jsonObj.keys();
        while (jsonKeys.hasNext()) {
            String jsonKey = (String) jsonKeys.next();
            Object jsonValObj = null;
            try {
                jsonValObj = jsonObj.get(jsonKey);
            } catch (JSONException e) {
                e.printStackTrace();
                return jsonMap;
            }
            if (jsonValObj instanceof JSONArray) {
                try {
                    jsonMap.put(jsonKey, jsonToList((JSONArray) jsonValObj));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return jsonMap;
                }
            } else if (jsonValObj instanceof JSONObject) {
                jsonMap.put(jsonKey, jsonToMap(jsonValObj.toString()));
            } else {
                jsonMap.put(jsonKey, jsonValObj);
            }
        }
        return jsonMap;
    }
    /**
     * JsonArray转List
     */
    private static List<Object> jsonToList(JSONArray jsonArr)
            throws JSONException {
        List<Object> jsonToMapList = new ArrayList<Object>();
        for (int i = 0; i < jsonArr.length(); i++) {
            Object object = jsonArr.get(i);
            if (object instanceof JSONArray) {
                jsonToMapList.add(jsonToList((JSONArray) object));
            } else if (object instanceof JSONObject) {
                jsonToMapList.add(jsonToMap(object.toString()));
            } else {
                jsonToMapList.add(object);
            }
        }
        return jsonToMapList;
    }
    public static boolean isNumeric(String str){
        if(TextUtils.isEmpty(str)) return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    public static void copyText(final TextView textView) {
        Context context = textView.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popView = layoutInflater.inflate(R.layout.popup_view, null);
        final PopupWindow popupWindow = new PopupWindow(popView);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popView.getMeasuredWidth();
        int popupHeight = popView.getMeasuredHeight();
        int viewWidth = textView.getWidth();
        int[] location = new int[2];
        textView.getLocationOnScreen(location);
        // 这个是为了点击"返回Back"也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 使其聚集
        popupWindow.setFocusable(true);
        // // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(textView, Gravity.NO_GRAVITY, location[0] - (popupWidth - viewWidth)/2,
                location[1] - popupHeight);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                systemCopy(textView.getText().toString());
            }
        });
    }
    public static void  systemCopy(String content){
        //获取剪贴板管理服务
        ClipboardManager cm =(ClipboardManager) KnmsShopApp.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        //将文本数据复制到剪贴板
        cm.setText(content);
    }
}
