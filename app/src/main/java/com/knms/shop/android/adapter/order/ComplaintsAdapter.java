package com.knms.shop.android.adapter.order;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.order.Complaints;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


/**
 * Created by Administrator on 2016/9/30.
 */

public class ComplaintsAdapter extends BaseMultiItemQuickAdapter<Complaints, BaseViewHolder> {

    private Drawable up,down;
    private boolean isOrderPay = false;
    private int lineColor = 0;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * super(R.layout.item_order_complain3, data);
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ComplaintsAdapter(List<Complaints> data) {
        super(data);
        addItemType(1, R.layout.item_order_complain1);
        addItemType(2, R.layout.item_order_complain2);
        addItemType(3, R.layout.item_order_complain3);
    }

    public void setOrderPay(boolean orderPay) {
        isOrderPay = orderPay;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Complaints item) {
        if (null == up ) up = mContext.getResources().getDrawable(R.drawable.icon_flod);
        if (null == down) down = mContext.getResources().getDrawable(R.drawable.icon_un_flod);
        if (isOrderPay){//app支付的时候全部为展开状态
            item.isFold = true;
            if (0 == lineColor)
                lineColor = mContext.getResources().getColor(R.color.yellow_ffe816);
        }
        Drawable drawable = item.isFold ? up :down ;
        switch (item.getItemType()) {
            case 1:
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((TextView) helper.getView(R.id.tv_fold)).setCompoundDrawables(null, null, drawable, null);
                helper.setText(R.id.tv_fold, item.isFold ?  "收起":"展开")
                        .setText(R.id.complaint_time, item.occreated)
                        .setText(R.id.complaint_content, item.occontent)
                        .setText(R.id.complaint_type, "投诉类型：" + item.occomplaintstype)
                        .setVisible(R.id.complaint_content,item.isFold)
                        .setVisible(R.id.complaint_type,item.isFold)
                        .addOnClickListener(R.id.tv_complaints_details1);
                ((TextView) helper.getView(R.id.tv_complaints_details1)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                helper.setOnClickListener(R.id.tv_fold, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(mContext,item.isFold? "orderStateCompainDown":"orderStateCompainUp");
                        item.isFold = !item.isFold;
                        notifyDataSetChanged();
                    }
                });
                break;
            case 2:
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((TextView) helper.getView(R.id.tv_fold)).setCompoundDrawables(null, null, drawable, null);
                helper.setText(R.id.tv_fold, item.isFold ? "收起":"展开")
                        .setText(R.id.complaint_time, item.occreated)
                        .setText(R.id.complaint_content, item.occontent)
                        .setText(R.id.complaint_type, "投诉类型：" + item.occomplaintstype)
                        .setText(R.id.accept_time, item.ocservicetime)
                        .setVisible(R.id.complaints_one_layout, item.isFold)
                        .setVisible(R.id.lineOne,item.isFold)
                        .setVisible(R.id.cpainhint,item.isFold)
                        .addOnClickListener(R.id.tv_complaints_details2);
                ((TextView) helper.getView(R.id.tv_complaints_details2)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                helper.setOnClickListener(R.id.tv_fold, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(mContext,item.isFold? "orderStateCompainDown":"orderStateCompainUp");
                        item.isFold = !item.isFold;
                        notifyDataSetChanged();
                    }
                });
                if (isOrderPay){
                    helper.setImageResource(R.id.icon_succeed,R.drawable.icon_2);
                    helper.setBackgroundColor(R.id.line_succeed,lineColor);
                }

                break;
            case 3:
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((TextView) helper.getView(R.id.tv_fold)).setCompoundDrawables(null, null, drawable, null);
                helper.setText(R.id.tv_fold, item.isFold ? "收起":"展开")
                        .setText(R.id.complaint_time, item.occreated)
                        .setText(R.id.complaint_content, item.occontent)
                        .setText(R.id.complaint_type, "投诉类型：" + item.occomplaintstype)
                        .setText(R.id.accept_time, item.ocservicetime)
                        .setText(R.id.dispose_time, item.ocdealwithtime)
                        .setText(R.id.dispose_content, "您的问题客服已处理完成")
                        .setVisible(R.id.complaints_one_layout, item.isFold)
                        .setVisible(R.id.complaints_two_layout, item.isFold)
                        .setVisible(R.id.lineOne,item.isFold)
                        .setVisible(R.id.dispose_content,item.isFold)
                        .addOnClickListener(R.id.tv_complaints_details3);
                ((TextView) helper.getView(R.id.tv_complaints_details3)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                helper.setOnClickListener(R.id.tv_fold, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(mContext,item.isFold? "orderStateCompainDown":"orderStateCompainUp");
                        item.isFold = !item.isFold;
                        notifyDataSetChanged();
                    }
                });
                if (isOrderPay){
                    helper.setImageResource(R.id.icon_succeed,R.drawable.icon_2);
                    helper.setBackgroundColor(R.id.line_succeed,lineColor);
                    helper.setImageResource(R.id.icon_accepted,R.drawable.icon_2);
                    helper.setBackgroundColor(R.id.line_accepted,lineColor);
                }
                break;
        }
        if (isOrderPay) {
            helper.setBackgroundColor(R.id.line,lineColor);
            TextView tv_fold = helper.getView(R.id.tv_fold);
            tv_fold.setVisibility(View.INVISIBLE);
            tv_fold.setWidth(1);
        }
    }
}
