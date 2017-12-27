package com.knms.shop.android.util;

import android.util.TypedValue;
import android.view.View;

import com.knms.shop.android.app.KnmsShopApp;

/**
 * Created by Administrator on 2016/8/24.
 */
public class LocalDisplay {
    public static int dip2px(float dpValue) {
        final float scale = KnmsShopApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                KnmsShopApp.getInstance().getResources().getDisplayMetrics());
    }
    public static int px2dip(float pxValue) {
        final float scale = KnmsShopApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = KnmsShopApp.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = KnmsShopApp.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int[] getViewSize(final View view){
        if (null == view) return new int[]{0,0};
        int height = view.getHeight();
        int width = view.getWidth();
        if (0 != width) return new int[]{width,height};
        view.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
        height = view.getMeasuredHeight();
        width = view.getMeasuredWidth();
        return new int[]{width,height};
    }
}
