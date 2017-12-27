package com.knms.shop.android.helper;

import android.os.Environment;

import com.knms.shop.android.app.KnmsShopApp;

import java.io.File;

/**
 * Created by Administrator on 2016/9/30.
 */

public class SDCardHelper {
    public final static String CACHE = File.separator + "Android"+File.separator+"data"+File.separator+"knmsShop" + File.separator;
    public final static String CACHE_IMG = "imageloader" + File.separator;
    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * 获取Sdcard卡路径
     *
     * @return SDPath
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = KnmsShopApp.getInstance().getCacheDir();
        }
        return sdDir.toString();
    }
    /**
     * 获取缓存路径
     * @return SDPath SDCard--/Android/data/knmsShop
     */
    public static String getCacheDir() {
        String cacheDir = getSDPath() + CACHE;
        File filecacheDir = new File(cacheDir);
        if (!filecacheDir.exists()) {
            filecacheDir.mkdirs();
        }
        return filecacheDir.getAbsolutePath();
    }

    /**
     *
     * @return SDCard--/DCIM/Camera
     */
    public static File getDCIMDir() {
        String imgDir = getSDPath() + "/DCIM/Camera";
        File fileImgDir = new File(imgDir);
        if (!fileImgDir.exists()) {
            fileImgDir.mkdirs();
        }
        return fileImgDir;
    }
    /**
     * 获取缓存img路径
     * @return SDPath SDCard--/Android/data/knmsApp/imageloader
     */
    public static String getCacheImgDir() {
        String cacheImgDir = getSDPath() + CACHE + CACHE_IMG;
        File filecacheImgDir = new File(cacheImgDir);
        if (!filecacheImgDir.exists()) {
            filecacheImgDir.mkdirs();
        }
        return filecacheImgDir.getAbsolutePath();
    }
    public static File getCacheImgDirFile() {
        String path = getCacheImgDir();
        File fileDir = new File(path);
        return fileDir;
    }
    public static File getCacheDirFile() {
        String path = getCacheDir();
        File fileDir = new File(path);
        return fileDir;
    }
}
