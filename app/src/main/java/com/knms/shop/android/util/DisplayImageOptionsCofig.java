package com.knms.shop.android.util;

import android.graphics.Bitmap;

import com.knms.shop.android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Administrator on 2016/8/25.
 */
public class DisplayImageOptionsCofig {
    public static DisplayImageOptions opt_default = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.bg_rectangle_whilte)// 在显示真正的图片前，会加载这个资源
            .showImageForEmptyUri(R.drawable.ic_default_image) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.ic_default_image) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .build();
    public static DisplayImageOptions opt_avatar = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.icon_avatar)// 在显示真正的图片前，会加载这个资源
            .showImageForEmptyUri(R.drawable.icon_avatar) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.icon_avatar) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .build();
    public static DisplayImageOptions opt_black_bg = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.bg_rectangle_black)// 在显示真正的图片前，会加载这个资源
            .showImageForEmptyUri(R.drawable.bg_rectangle_black) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.bg_rectangle_whilte) // 设置图片加载或解码过程中发生错误显示的图片
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .delayBeforeLoading(100)//载入图片前稍做延时可以提高整体滑动的流畅度
            .build();
    public static DisplayImageOptions opt_nocache = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.bg_rectangle_whilte)// 在显示真正的图片前，会加载这个资源
            .showImageForEmptyUri(R.drawable.ic_default_image) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.ic_default_image) // 设置图片加载或解码过程中发生错误显示的图片
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .delayBeforeLoading(100)//载入图片前稍做延时可以提高整体滑动的流畅度
            .build();

}
