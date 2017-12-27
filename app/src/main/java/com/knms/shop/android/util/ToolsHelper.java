package com.knms.shop.android.util;
import android.text.TextUtils;

import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.helper.ConstantObj;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/11/29.
 */

public class ToolsHelper {
    /**
     * 单例
     */
    public static ToolsHelper getInstance() {
        return ToolsHelper.InstanceHolder.instance;
    }
    static class InstanceHolder {
        final static ToolsHelper instance = new ToolsHelper();
    }
    public void logout(){
        String userName=SPUtils.getCurrentUserName();
        MobclickAgent.onProfileSignOff();
        IMHelper.getInstance().logout();
        SPUtils.clear();
        SPUtils.saveCurrentUserName(userName);
        RxBus.get().post(BusAction.ACTION_LOGOUT,"logout");
        JPushInterface.setAliasAndTags(KnmsShopApp.getInstance(), "", null,null);//退出账号之后不能接受别名推送了
    }
    /**
     * 在价格前面添加￥符号
     * @param content
     * @return
     */
    public String addMoneySymbol(String content) {
        if(TextUtils.isEmpty(content)) return "";
        Pattern pattern = Pattern.compile("[￥|¥]?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String oldReplace = matcher.group(0);
            String newReplace = ConstantObj.MONEY + oldReplace.replace("￥", "").replace("¥", "");
			/*
			 * 我们在这里使用Matcher对象的appendReplacement()方法来进行替换操作，而
			 * 不是使用String对象的replaceAll()或replaceFirst()方法来进行替换操作，因为
			 * 它们都能只能进行一次性简单的替换操作，而且只能替换成一样的内容，而这里则是要求每
			 * 一个匹配式的替换值都不同，所以就只能在循环里使用appendReplacement方式来进行逐个替换了。
			 */
            matcher.appendReplacement(sb, newReplace);
        }
        //最后还得要把尾串接到已替换的内容后面去
        matcher.appendTail(sb);
        return sb.toString();
    }
    /**
     * 排序
     */
    public <T> void sort(List<T> data, final String sortField){
        if(data == null || (data != null && data.size() < 2)) return;
        Collections.sort(data, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                try {
                    Class clazz = o1.getClass();
                    Field field = clazz.getDeclaredField(sortField); //获取成员变量
                    field.setAccessible(true); //设置成可访问状态
                    String typeName = field.getType().getName().toLowerCase(); //转换成小写
                    Object v1 = field.get(o1); //获取field的值
                    Object v2 = field.get(o2); //获取field的值
                    //判断字段数据类型，并比较大小
                    if(typeName.endsWith("string")) {
                        String value1 = v1.toString();
                        String value2 = v2.toString();
                        int order1 = 0;
                        int order2 = 0;
                        try {
                            order1 = Integer.parseInt(value1);
                            order2 = Integer.parseInt(value2);
                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
                        }
                        return order1 < order2 ? 1 : -1;
                    }else if(typeName.endsWith("int") || typeName.endsWith("integer")) {
                        Integer value1 = Integer.parseInt(v1.toString());
                        Integer value2 = Integer.parseInt(v2.toString());
                        return  value2.compareTo(value1);
                    }else if(typeName.endsWith("long")) {
                        Long value1 = Long.parseLong(v1.toString());
                        Long value2 = Long.parseLong(v2.toString());
                        return value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("float")) {
                        Float value1 = Float.parseFloat(v1.toString());
                        Float value2 = Float.parseFloat(v2.toString());
                        return value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("double")) {
                        Double value1 = Double.parseDouble(v1.toString());
                        Double value2 = Double.parseDouble(v2.toString());
                        return value2.compareTo(value1);
                    }else if(typeName.endsWith("date")) {
                        Date value1 = (Date)(v1);
                        Date value2 = (Date)(v2);
                        return value2.compareTo(value1);
                    }else {
                        //调用对象的compareTo()方法比较大小
                        Method method = field.getType().getDeclaredMethod("compareTo", new Class[]{field.getType()});
                        method.setAccessible(true); //设置可访问权限
                        int result  = (Integer)method.invoke(v1, new Object[]{v2});
                        return result;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return 0; //未知类型，无法比较大小
            }
        });
    }
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches("[1][34578]\\d{9}");
    }
    /** 是手机号或者座机号 */
    public boolean isPhoneOrTel(String number){
        if (TextUtils.isEmpty(number))
            return false;
        return number.matches("(^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0678])\\d{8}$)|(^(0\\d{2}-\\d{8}(-\\d{1,4})?)$|^(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$)");
    }
}
