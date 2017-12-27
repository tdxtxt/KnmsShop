package com.knms.shop.android.adapter.mineshop;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.knms.shop.android.activity.base.BaseActivity;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
import com.knms.shop.android.activity.browser.ImgBrowerPagerActivity;
import com.knms.shop.android.bean.body.media.Pic;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/25.
 */
public class AutoBrowseAdapter extends PagerAdapter {
    private List<Pic> imgs;
    private List<View> mRecycledViews = new ArrayList<View>();
    private OnclickListener onclickListener;
    private OnLongclickListener onLongclickListener;
    private boolean isOriginal = false;
    private boolean isBlackBg=false;


    public void setOnclickListener(OnclickListener onclickListener) {
        this.onclickListener = onclickListener;
    }
    public void setOnLongclickListener(OnLongclickListener onLongclickListener) {
        this.onLongclickListener = onLongclickListener;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public void setBlackBg(boolean blackBg){
        isBlackBg=blackBg;
    }

    public interface OnclickListener{
        public void onClick(int position);
    }
    public interface OnLongclickListener{
        public void onLoingClick(View view);
    }
    public AutoBrowseAdapter(List<Pic> imgs){
        this.imgs = imgs;
        if(this.imgs == null) this.imgs = new ArrayList<Pic>();
    }
    public List<String> toImgs(){
        List<String> list = null;
        if(imgs != null && imgs.size() > 0){
            list = new ArrayList<String>();
            for (Pic item: imgs) {
                list.add(item.url);
            }
        }
        return list;
    }
    @Override
    public int getCount() {
        return imgs == null ? 0 : imgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mRecycledViews.add((View) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView view;
        if (mRecycledViews.isEmpty()) {
            view = new ImageView(container.getContext());
            if(!isOriginal)view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            view = (ImageView) mRecycledViews.get(0);
            mRecycledViews.remove(0);
        }
        if(imgs.size() <= position){
            return view;
        }
        container.addView(view);
        if(isBlackBg) ImageLoadHelper.getInstance().displayImageOrigin(imgs.get(position).url,view);
        else ImageLoadHelper.getInstance().displayImage(imgs.get(position).url,view, ScreenUtil.getScreenWidth(), LocalDisplay.dp2px(360));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onclickListener != null){
                    onclickListener.onClick(position);
                }else{
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("data",toImgs());
                    params.put("position",position);
                    if(v.getContext() instanceof BaseActivity){
                        ((BaseActivity)v.getContext()).startActivityAnimGeneral(ImgBrowerPagerActivity.class,params);
                    }else if(v.getContext() instanceof BaseFragmentActivity){
                        ((BaseFragmentActivity)v.getContext()).startActivityAnimGeneral(ImgBrowerPagerActivity.class,params);
                    }
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onLongclickListener!=null)
//                    view.getDrawable();
                    onLongclickListener.onLoingClick(v);
                return false;
            }
        });
        return view;
    }
}
