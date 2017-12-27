package com.knms.shop.android.helper;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tdx on 2016/9/28.
 * 字符串时间统一格式yyyy-MM-dd HH:mm:ss
 */

public class StrHelper {
    public static final String DATE_FORMAT1 = "yyyy-MM-dd";
    public static final String DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT3 = "MM-dd HH:mm:ss";
    public static final String DATE_FORMAT4 = "HH:mm:ss";

    static SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT2);
    public static String validateStr(String data){
        if(!TextUtils.isEmpty(data) && data.contains("-")){
            return data;
        }
        return getCurrentTime(DATE_FORMAT2);
    }
    public static Date str2Date(String data){
        data = validateStr(data);
        try {
            return  df.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    public static String millis2Str(long millis){
        return  df.format(new Date(millis));
    }
    public static long str2Millis(String data){
        data = validateStr(data);
        try {
            return  df.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static String str2format(String data, String format){
        data = validateStr(data);
        try {
            Date new_data =  df.parse(data);
            SimpleDateFormat sf = new SimpleDateFormat(format);
            return sf.format(new_data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "未知";
    }
    /**
     * 显示聊天的时间格式
     * @param currentTime
     * @param lastTime
     * @return
     */
    public static String showImTime(long currentTime,long lastTime){
        long diff = Math.abs(currentTime - lastTime);
        if(diff > 15 * 1000){//15秒
            return displayTime(currentTime,true,true);
        }
        return "";
    }
    /**
     * 显示聊天的时间格式
     * @param currentTime
     * @return
     */
    public static String showImTime(long currentTime){
        return displayTime(currentTime,true,false);
    }
    public static String displayTime(long millis,boolean isOwn,boolean isDetails){
        String data = millis2Str(millis);
        return displayTime(data,isOwn,isDetails);
    }
    public static String displayTime(String data,boolean isOwn,boolean isDetails){
        if(TextUtils.isEmpty(data) || "null".equals(data)){
            return "";
        }
        long timeMillis = str2Millis(data);

        Calendar oldTimeCalendar = Calendar.getInstance();
        oldTimeCalendar.setTimeInMillis(timeMillis);
        int oldYear = oldTimeCalendar.get(Calendar.YEAR);
        int oldMonth = oldTimeCalendar.get(Calendar.MONTH) + 1;
        int oldDay = oldTimeCalendar.get(Calendar.DAY_OF_MONTH);
//        int oldHour = oldTimeCalendar.get(Calendar.HOUR_OF_DAY);
//        int oldMinute = oldTimeCalendar.get(Calendar.MINUTE);

        Calendar currentTimeCalendar = Calendar.getInstance();
        int newYear = currentTimeCalendar.get(Calendar.YEAR);
        int newMonth = currentTimeCalendar.get(Calendar.MONTH) + 1;
        int newDay = currentTimeCalendar.get(Calendar.DAY_OF_MONTH);
//        int newHour = currentTimeCalendar.get(Calendar.HOUR_OF_DAY);
//        int newMinute = currentTimeCalendar.get(Calendar.MINUTE);

        if(oldYear == newYear){
            if(oldMonth == newMonth){
                if(oldDay == newDay){
                    long diffMillis = Math.abs(currentTimeCalendar.getTimeInMillis() - timeMillis);//时间差
                    long diffHour = diffMillis / (1 * 60 * 60 * 1000);//xx小时前
                    if(diffHour == 0){
                        long diffMinute = diffMillis / (1 * 60 * 1000);//xx分钟钱
                        return isOwn ? "今天" + str2format(data,DATE_FORMAT4) : Math.max(1,diffMinute) + "分钟前";
                    }else{
                        return isOwn ? "今天" + str2format(data,DATE_FORMAT4) : diffHour + "小时前";
                    }
                }else{//同一年同月不同天
                    currentTimeCalendar.add(Calendar.DAY_OF_MONTH,-1);
                    if(currentTimeCalendar.get(Calendar.DAY_OF_MONTH) == oldDay){//昨天
                        return "昨天" + str2format(data,DATE_FORMAT4);
                    }
                }
            }
        }
        return (isDetails ? str2format(data,DATE_FORMAT2) : str2format(data,DATE_FORMAT1));
    }
    public static String getFormatTime(String format, long timeMillisecond){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(timeMillisecond));
    }

    /**
     * 根据指定格式获取当前时间
     * @author chenssy
     * @date Dec 27, 2013
     * @param format
     * @return String
     */
    public static String getCurrentTime(String format){
        SimpleDateFormat sdf = getFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }
    public static String str2formatTime(String date, String format){
        SimpleDateFormat sdf = getFormat(format);
        Date new_date = str2Date(date);
        return sdf.format(new_date);
    }
    /**
     * 获取日期显示格式，为空默认为yyyy-mm-dd HH:mm:ss
     * @author chenssy
     * @date Dec 30, 2013
     * @param format
     * @return
     * @return SimpleDateFormat
     */
    private static SimpleDateFormat getFormat(String format){
        if(format == null || "".equals(format)){
            format = DATE_FORMAT2;
        }
        return new SimpleDateFormat(format);
    }
    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }
    public static long getSecondsByMilliseconds(long milliseconds) {
        long seconds = new BigDecimal((float) ((float) milliseconds / (float) 1000)).setScale(0,
                BigDecimal.ROUND_HALF_UP).intValue();
        // if (seconds == 0) {
        // seconds = 1;
        // }
        return seconds;
    }
    /**
     * 删除字符串中的空白符
     *
     * @param content
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }
    private static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * 用于获取一个String的md5值
     * @return
     */
    public static String getMd5(String str) {
        if(TextUtils.isEmpty(str)) return UUID.randomUUID().toString();
        byte[] bs = messageDigest.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs) {
            if((x & 0xff)>>4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }

    public static boolean isMobileNO(String mobiles) {
        //^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0678])\d{8}$
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }
}
