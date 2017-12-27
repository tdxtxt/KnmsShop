package com.knms.shop.android.adapter.order;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.browser.ImgBrowerPagerActivity;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示图片
 * Created by 654654 on 2017/5/4.
 */

public class ShowImagesAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    private int width;
    private int maxCount;
    private int drawableId;
    public OnItemClickBefore onItemClickBefore;
    public ShowImagesAdapter(List<String> imgUrls){
        this(imgUrls,false);
    }
    public ShowImagesAdapter(List<String> imgUrls,boolean canClickImg){
        super(R.layout.item_img_70x70,imgUrls);
        if (width == 0) width = ScreenUtil.getScreenWidth() / 4;
        maxCount = -1;
        drawableId = R.drawable.icon_3;
        if (canClickImg)
            setOnItemClickListener(listener);
    }

    /** 设置默认点击事件之前的事情 */
    public void setOnItemClickBefore(OnItemClickBefore onItemClickBefore) {
        this.onItemClickBefore = onItemClickBefore;
    }

    public interface OnItemClickBefore{
        public void onItemClick(BaseQuickAdapter adapter, View view, int position);
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (null != onItemClickBefore)
                onItemClickBefore.onItemClick(adapter,view,position);
            ArrayList<String> data = (ArrayList<String>) adapter.getData();
            ArrayList<String> imgData = new ArrayList<>();
            if (data != null && position >= 0) {
                for (String url : data){
                    if (!TextUtils.isEmpty(url) && !url.equals("drawable://"+ drawableId))
                        imgData.add(url);
                }
                if (imgData.size() < 1)
                    return;
                Intent intent = new Intent(mContext, ImgBrowerPagerActivity.class);
                intent.putExtra("data", imgData);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        }
    };

    public void setWidth(int width) {
        this.width = width;
        if (this.width == 0) this.width = ScreenUtil.getScreenWidth() / 4;
        notifyDataSetChanged();
    }

    @Override
    public void setNewData(List<String> data) {
        changData(data);
        super.setNewData(data);
    }

    @Override
    public void addData(String data) {
        super.addData(data);
        changData(mData);
        notifyDataSetChanged();
    }

    private void changData(List<String> data){
        if (maxCount < 1) return;
        if (data == null) {
            data = new ArrayList<>();
        }
        ArrayList copy = new ArrayList<>();
        if (data.size() > this.maxCount){
            for (int i = 0;i< this.maxCount;i++)
                copy.add(data.get(i));
            data.clear();
            data.addAll(copy);
        }else {
            int len = this.maxCount - data.size();
            for (int i = 0;i < len;i++)
                copy.add("drawable://"+drawableId);
            data.addAll(copy);
        }
        for (String url : data){
            if (TextUtils.isEmpty(url) )
                data.set(data.indexOf(url),"drawable://"+R.drawable.icon_3);
        }
    }
    public void setDefShow(int maxCount, @DrawableRes int drawableId){
        this.maxCount = maxCount;
        this.drawableId = drawableId;
        if (maxCount < 1 || (null != mData && mData.size() == maxCount))
            return;
        changData(mData);
        notifyDataSetChanged();
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView iv_pic = helper.getView(R.id.iv_pic);
        ViewGroup.LayoutParams lp = iv_pic.getLayoutParams();
        lp.height = width;
        lp.width = width;
        iv_pic.setLayoutParams(lp);
        ImageLoadHelper.getInstance().displayImage(item, iv_pic,width,width);
    }
}
