package com.knms.shop.android.adapter.orderpay;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.adapter.order.ShowImagesAdapter;
import com.knms.shop.android.bean.body.orderpay.OrderPayCommentListData;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.CalendarUtils;
import com.knms.shop.android.view.CircleImageView;
import com.knms.shop.android.view.Star;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;

import java.util.ArrayList;

/**
 * Created by 654654 on 2017/8/31.
 */

public class OrderPayCommentListAdapter extends BaseQuickAdapter<OrderPayCommentListData,BaseViewHolder> {

    private int width;
    private OnReplyClick onReplyClick;
    public OrderPayCommentListAdapter(OnReplyClick onReplyClick){
        super(R.layout.item_orderpay_comment_list);
        this.onReplyClick = onReplyClick;
    }
    public synchronized void removeData(String id){
        if (null == mData) return;
        int index = findOrderIndex(id);
        if (-1 == index ) return;
        remove(index);
        if (mData.size() < 1) {
            setEmptyView(R.layout.layout_order_empty);
        }
    }
    public int findOrderIndex(String id){
        if (TextUtils.isEmpty(id) || null == mData || mData.size() < 1)
            return -1;
        int len = mData.size();
        for (int i = 0;i < len; i++){
            OrderPayCommentListData item = mData.get(i);
            if (null != item && id.equals(item.id)){
                return i;
            }
        }
        return -1;
    }
    @Override
    protected void convert(final BaseViewHolder helper, OrderPayCommentListData item) {
        CircleImageView head = helper.getView(R.id.head);
        ImageLoadHelper.getInstance().displayImageHead(item.userPhoto,head);
        TextView nikeName = helper.getView(R.id.phone);
        nikeName.setText(item.nikeName);
        Star star = helper.getView(R.id.rating);
        star.setMark((int) item.score);
        TextView created = helper.getView(R.id.time);
        created.setText(CalendarUtils.getDataCut(item.created));

        ImageView state = helper.getView(R.id.state);
        if (2 == item.state)
            state.setVisibility(View.VISIBLE);
        else
            state.setVisibility(View.GONE);
        TextView parameterbriefing = helper.getView(R.id.parameterbriefing);
        parameterbriefing.setText(item.parameterbriefing);
        TextView content = helper.getView(R.id.content);
        content.setText(item.content);
//        content.post(new AppendTextAll(content));
        TextView visit = helper.getView(R.id.visit);
        visit.setText("浏览"+item.browsenumber);
        TextView comment = helper.getView(R.id.comment);
        comment.setText(""+item.agreenumber);
        final RecyclerView imgs = helper.getView(R.id.imgs);
        if (null != item.imgList && item.imgList.size() > 0){
            imgs.setVisibility(View.VISIBLE);
            imgs.setLayoutManager(new FullyGridLayoutManager(helper.getConvertView().getContext(), 3));
            imgs.setFocusable(false);
            ArrayList<String> imgData = new ArrayList<String>();
            for (OrderPayCommentListData.ImgList img : item.imgList) {
                if (!TextUtils.isEmpty(img.imageUrl))
                    imgData.add(img.imageUrl);//防止字段错误
            }
            if (0 == width) width = (int) (imgs.getWidth() * 0.33);
            ShowImagesAdapter adapter = new ShowImagesAdapter(imgData,true);
            adapter.setWidth(width);
            imgs.setAdapter(adapter);
        }else {
            imgs.setVisibility(View.GONE);
        }

        LinearLayout showReply = helper.getView(R.id.showReply);
        TextView replydate = helper.getView(R.id.replydate);
        TextView replymsg = helper.getView(R.id.replymsg);

        TextView reply = helper.getView(R.id.reply);

        if (null != item.shopReply && !TextUtils.isEmpty(item.shopReply.content) ){
            showReply.setVisibility(View.VISIBLE);
            replymsg.setVisibility(View.VISIBLE);
            reply.setVisibility(View.GONE);
            replydate.setText(CalendarUtils.getDataCut(item.shopReply.created));
            replymsg.setText(item.shopReply.content);
        }else {
            showReply.setVisibility(View.GONE);
            replymsg.setVisibility(View.GONE);
            if (null == mData || mData.size() == 1) {
                reply.setVisibility(View.GONE);
            }else {
                reply.setVisibility(View.VISIBLE);
                reply.setTag(item);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderPayCommentListData data = (OrderPayCommentListData) v.getTag();
                        if (null == data || TextUtils.isEmpty(data.id))
                            return;
                        if (null != onReplyClick)
                            onReplyClick.onClick(data.id);
                        }
                });
            }
        }
    }
    /** 回复点击事件 */
    public interface OnReplyClick{
        void onClick(String commentId);
    }
}
