package com.knms.shop.android.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.net.HttpConstant;
import com.knms.shop.android.util.DisplayImageOptionsCofig;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ScreenUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by tdx on 2016/9/6.
 */
public class ImageLoadHelper {
    static ImageLoadHelper instance;
    public ImageLoadHelper(){
    }
    public static ImageLoadHelper getInstance(){
        if (instance == null) {
            synchronized (ImageLoadHelper.class) {
                if (instance == null) {
                    instance = new ImageLoadHelper();
                }
            }
        }
        return instance;
    }
    public void displayImage(String url, ImageView view){
        if(view == null) return;
        int[] size = LocalDisplay.getViewSize(view);
        displayImage(url,view,size[0],size[1]);
    }
    public void displayImage(String url, ImageView view,int width,int height){
        if(view == null) return;
        url = convertFillJPG(url,width,height);
        ImageLoader.getInstance().displayImage(url,view,DisplayImageOptionsCofig.opt_default);
    }
    public void displayImageOrigin(String url, ImageView view){
        if(view == null) return;
        url = convertLfitWidthJPG(url, ScreenUtil.getScreenWidth());
        ImageLoader.getInstance().displayImage(url,view,DisplayImageOptionsCofig.opt_black_bg);
    }
    public void displayImageLocal(String url, ImageView view){
        if(view == null) return;
        url = convertLfitWidthJPG(url,ScreenUtil.getScreenWidth());
        ImageLoader.getInstance().displayImage(url,view,DisplayImageOptionsCofig.opt_nocache);
    }
    public void displayImageHead(String url, ImageView view){
        if(view == null) return;
        int[] size = LocalDisplay.getViewSize(view);
        url = convertFillJPG(url,size[0],size[1]);
        ImageLoader.getInstance().displayImage(url,view,DisplayImageOptionsCofig.opt_avatar);
    }

    /**
     * 等比例填充
     */
    public String convertFillJPG(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            if (width > 0 && height > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_lfit,w_" + width + ",h_" + height + ",limit_0/auto-orient,1/sharpen,100/quality,q_61/format,jpg/interlace,1";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }
    /**
     * 单边自适应，按宽度
     */
    private String convertLfitWidthJPG(String url, int width) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            width = (width >= 1080) ? 1080 : 720;
            if (width > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_lfit,w_" + (int) (width * 1.5) + ",limit_0/auto-orient,1/quality,q_61/format,jpg/interlace,1";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }
    /**
     * 单边自适应，按宽度
     */
    private String convertLfitHightJPG(String url, int height) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            if (height > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_lfit,h_" + height + ",limit_0/auto-orient,1/quality,q_61/format,jpg/interlace,1";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }
    /**
     * 等比例拆剪
     */
    private String convertPadJPG(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            if (width > 0 && height > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_pad,w_" + width + ",h_" + height + ",limit_0/auto-orient,1/sharpen,100/quality,q_61/format,jpg";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }
    private String convertFillPNG(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            if (width > 0 && height > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_fill,w_" + width + ",h_" + height + ",limit_0/auto-orient,1/format,png";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }
    private String convertPadPNG(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) return "";
        if (url.contains(SDCardHelper.getSDPath())) {
            return "file://" + url;
        } else if (url.startsWith("drawable://")) {
        } else if (url.startsWith("https://")) {
        } else if (!url.contains("http://")) {
            if (width > 0 && height > 0) {
                if (url.contains("?")) {
                    return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
                }
                return (url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url) + "?x-oss-process=image/resize,m_pad,w_" + width + ",h_" + height + ",limit_0/auto-orient,1/format,png";
            } else {
                return url.startsWith(HttpConstant.SRC) ? url : HttpConstant.SRC + url;
            }
        }
        return url;
    }

    public Bitmap getBitmapFromCache(String url) {
        File file = ImageLoader.getInstance().getDiskCache().get(url);
        Bitmap bitmap = null;
        if(file != null && file.exists()) bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }
}
