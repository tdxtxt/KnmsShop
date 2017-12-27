package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.order.ShowImagesAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.order.UserComment;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.CalendarUtils;
import com.knms.shop.android.util.ScreenUtil;
import com.knms.shop.android.view.CircleImageView;
import com.knms.shop.android.view.Star;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * 用户评价
 * Created by 654654 on 2017/4/28.
 */

public class UserCommentActivity extends HeadBaseActivity {
    @Override
    protected int layoutResID() {
        return R.layout.activity_comment;
    }

    private CircleImageView userHead;
    private TextView phone,time,content,visit,comment;
    private Star star;
    private RecyclerView imgs;
    private TextView replyComment;
    private LinearLayout showMerchantComment;
    private CircleImageView replyHead;
    private TextView replyName,replyTime,replyMassage;
    private ScrollView scrollView;
    @Override
    protected void initView() {
        scrollView = findView(R.id.scrollView);
        View view = findView(R.id.userComment);
        view.findViewById(R.id.showTop).setVisibility(View.GONE);
        view.findViewById(R.id.lineTitle).setVisibility(View.GONE);
        view.findViewById(R.id.lineReply).setVisibility(View.GONE);
        view.findViewById(R.id.reply).setVisibility(View.GONE);
        view.findViewById(R.id.show_attach).setVisibility(View.VISIBLE);

        userHead = (CircleImageView) view.findViewById(R.id.head);
        phone = (TextView) view.findViewById(R.id.phone);
        star = (Star) view.findViewById(R.id.rating);
        time = (TextView) view.findViewById(R.id.time);
        content = (TextView) view.findViewById(R.id.content);
        imgs = (RecyclerView) view.findViewById(R.id.imgs);
        visit = (TextView) view.findViewById(R.id.visit);
        comment = (TextView) view.findViewById(R.id.comment);
        showMerchantComment = findView(R.id.showMerchantComment);

        View v = findView(R.id.replyItem);
        replyHead = (CircleImageView) v.findViewById(R.id.head);
        replyName = (TextView) v.findViewById(R.id.name);
        replyTime = (TextView) v.findViewById(R.id.time);
        replyMassage = (TextView) v.findViewById(R.id.massage);
        replyComment = findView(R.id.replyComment);

        showMerchantComment.setVisibility(View.GONE);
        replyComment.setVisibility(View.GONE);
        imgs.setVisibility(View.GONE);
        imgs.setLayoutManager(new FullyGridLayoutManager(this,3));

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) time.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP|RelativeLayout.ALIGN_PARENT_RIGHT);
        time.setLayoutParams(rlp);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    private String orderId;
    @Override
    protected void getParmas(Intent intent) {
        if (null != intent)
            orderId = intent.getStringExtra("orderId");
    }

    @Override
    protected void initData() {
        reqApi();
    }

    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(orderId))
            return;
        showProgress();
        RxRequestApi.getInstance().getApiService().getUserComment(orderId).compose(this.<ResponseBody<UserComment>>applySchedulers())
                .subscribe(new Action1<ResponseBody<UserComment>>() {
                    @Override
                    public void call(ResponseBody<UserComment> userCommentResponseBody) {
                        if (userCommentResponseBody.isSuccess()) {
                            updateView(userCommentResponseBody.data);
                        }
                        hideProgress();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        hideProgress();
                    }
                });
    }

    private int width;
    private void updateView(UserComment data) {
        if (null == data)
            return;
        ImageLoadHelper.getInstance().displayImageHead(data.userPhoto,userHead);
        phone.setText(data.nikeName);
        star.setMark((int) data.score);
        time.setText(CalendarUtils.getDataCut(data.created));
        content.setText(data.content);
        visit.setText("浏览"+data.browsenumber);
        comment.setText(""+data.agreenumber);
        if (null != data.imgList && data.imgList.size() > 0){
            imgs.setVisibility(View.VISIBLE);
            ArrayList<String> imgData = new ArrayList<String>();
            for (UserComment.ImgList img : data.imgList)
                imgData.add(TextUtils.isEmpty(img.imgUrl)? img.imageUrl :img.imgUrl);
            if (0 == width) width = ScreenUtil.getScreenWidth() / 3;
            imgs.setAdapter(new ShowImagesAdapter(imgData,true));
        }else {
            imgs.setVisibility(View.GONE);
        }
        if (null != data.shopReply){
            showMerchantComment.setVisibility(View.VISIBLE);
            replyComment.setVisibility(View.GONE);
            ImageLoadHelper.getInstance().displayImageHead(data.shopReply.sslogo,replyHead);
            replyName.setText(data.shopReply.ssname);
            replyTime.setText(CalendarUtils.getDataCut(data.shopReply.created));
            replyMassage.setText(data.shopReply.content);
        }else {
            showMerchantComment.setVisibility(View.GONE);
            replyComment.setVisibility(View.VISIBLE);
            replyComment.setTag(data.id);
            replyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentId = (String) v.getTag();
                    if (TextUtils.isEmpty(commentId))
                        return;
                    HashMap<String,Object> params = new HashMap<>();
                    params.put("orderId",orderId);
                    params.put("commentId",commentId);
                    startActivityAnimGeneral(ReplyCommentActivity.class,params);
                }
            });
        }
    }

    @Subscribe(tags = {@Tag(BusAction.ORDER_STATE_REPLYCOMMENT)})
    public void refreshData(String orderId){
        reqApi();
    }
    @Override
    protected String umTitle() {
        return "用户评论";
    }
    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("用户评论");
    }
}
