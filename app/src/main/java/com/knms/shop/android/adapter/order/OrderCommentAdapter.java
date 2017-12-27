package com.knms.shop.android.adapter.order;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.order.OrderDetailActivity;
import com.knms.shop.android.bean.body.order.OrderListData;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.ui.AppendTextAll;
import com.knms.shop.android.util.CalendarUtils;
import com.knms.shop.android.view.CircleImageView;
import com.knms.shop.android.view.Star;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;

import java.util.ArrayList;

/**
 * 待回复列表
 * Created by 654654 on 2017/4/30.
 */

public class OrderCommentAdapter extends BaseQuickAdapter<OrderListData,BaseViewHolder> {

    private int width;
    private OnReplyClick onReplyClick;
    public OrderCommentAdapter(OnReplyClick onReplyClick){
        super(R.layout.item_comment_list,null);
        this.onReplyClick = onReplyClick;
    }
    public synchronized void removeData(String orderId){
        if (null == mData) return;
        int index = findOrderIndex(orderId);
        if (-1 == index ) return;
        remove(index);
        if (mData.size() < 1) {
            setEmptyView(R.layout.layout_order_empty);
        }
    }
    public int findOrderIndex(String orderId){
        if (TextUtils.isEmpty(orderId) || null == mData || mData.size() < 1)
            return -1;
        int len = mData.size();
        for (int i = 0;i < len; i++){
            OrderListData item = mData.get(i);
            if (null != item && orderId.equals(item.orid)){
                return i;
            }
        }
        return -1;
    }
    @Override
    protected void convert(final BaseViewHolder helper, OrderListData item) {
        TextView id = helper.getView(R.id.id);
        TextView state = helper.getView(R.id.state);
        id.setText(item.orcontractno);
        String sState;
        switch (item.deliverystate){
            case 0:
                sState = "客户退货";
                break;
            case 1:
                sState = "待送货";
                break;
            case 2:
                sState = "待收货";
                break;
            case 3:
                sState = "待评价";
                break;
            case 6:
                sState = "系统默认收货";
                break;
            case 4:
                sState = "待回复";
                break;
            case 5:
                sState = "已回复";
                break;
            default:
                sState = "待送货";
        }
        state.setText(sState);
        ImageView complain = helper.getView(R.id.complain);
        if (2 == item.isComplaint){
            complain.setVisibility(View.VISIBLE);
        }else {
            complain.setVisibility(View.GONE);
        }
        CircleImageView head = helper.getView(R.id.head);
        ImageLoadHelper.getInstance().displayImageHead(item.orderComment.userPhoto,head);
        TextView nikeName = helper.getView(R.id.phone);
        nikeName.setText(item.orderComment.nikeName);
        Star star = helper.getView(R.id.rating);
        star.setMark((int) item.orderComment.score);
        TextView created = helper.getView(R.id.time);
        created.setText(CalendarUtils.getDataCut(item.orderComment.created));

        TextView content = helper.getView(R.id.content);

        content.setText(item.orderComment.content);
        content.post(new AppendTextAll(content));
        final RecyclerView imgs = helper.getView(R.id.imgs);
        if (null != item.orderComment.imgList && item.orderComment.imgList.size() > 0){
            imgs.setVisibility(View.VISIBLE);
            imgs.setLayoutManager(new FullyGridLayoutManager(helper.getConvertView().getContext(), 3));
            imgs.setFocusable(false);
            ArrayList<String> imgData = new ArrayList<String>();
            for (OrderListData.imgList img : item.orderComment.imgList)
                imgData.add(TextUtils.isEmpty(img.imgUrl) ? img.imageUrl : img.imgUrl);//防止字段错误
            if (0 == width) width = (int) (imgs.getWidth() * 0.33);
            ShowImagesAdapter adapter = new ShowImagesAdapter(imgData);
            adapter.setDefShow(3,R.drawable.icon_3);
            adapter.setWidth(width);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    OrderCommentAdapter.this.getOnItemClickListener().onItemClick(OrderCommentAdapter.this,helper.getView(R.id.itemContent),helper.getPosition());
                }
            });
            imgs.setAdapter(adapter);
        }else {
            imgs.setVisibility(View.GONE);
        }

        TextView reply = helper.getView(R.id.reply);
        reply.setTag(item);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListData data = (OrderListData) v.getTag();
                if (null == data || TextUtils.isEmpty(data.orid) || TextUtils.isEmpty(data.orderComment.id))
                    return;
                if (null != onReplyClick)
                    onReplyClick.onClick(data.orid,data.orderComment.id);
            }
        });
        RelativeLayout showTop = helper.getView(R.id.showTop);
        showTop.setTag(item.orid);
        showTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = (String) v.getTag();
                if (TextUtils.isEmpty(orderId))
                    return;
                Intent it = new Intent(mContext, OrderDetailActivity.class);
                it.putExtra("orderId",orderId);
                mContext.startActivity(it);
            }
        });

    }
    /** 回复点击事件 */
    public interface OnReplyClick{
        public void onClick(String orderId,String commentId);
    }
}
