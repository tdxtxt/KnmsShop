package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.ui.UiNumberEdit;

import java.util.HashMap;

import rx.functions.Action1;

/**
 * 回复评价
 * Created by 654654 on 2017/4/30.
 */

public class ReplyCommentActivity extends HeadBaseActivity {
    private UiNumberEdit uiNumberEdit;
    private TextView submit;
    private int defColor,changColor;
    @Override
    protected int layoutResID() {
        return R.layout.activity_replycomment;
    }

    @Override
    protected void initView() {
        defColor = getResources().getColor(R.color.gray_999999);
        changColor = getResources().getColor(R.color.black_333333);
        uiNumberEdit = findView(R.id.message);
        submit = findView(R.id.submit);
    }

    private String orderId = "";
    private String commentId = "";
    /** 实体店支付列表 */
    private boolean fromList = false;
    @Override
    protected void getParmas(Intent intent) {
        if (null != intent) {
            orderId = intent.getStringExtra("orderId");
            commentId = intent.getStringExtra("commentId");
            fromList = intent.getBooleanExtra("fromList",false);
        }
    }

    @Override
    protected void initData() {
        submit.setEnabled(false);
        uiNumberEdit.setFocusable(true);
        uiNumberEdit.setFocusableInTouchMode(true);
        uiNumberEdit.setHint("请输入你的回复内容");
        uiNumberEdit.setOnTextChanged(new UiNumberEdit.TextChanged() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = uiNumberEdit.getText();
                if (TextUtils.isEmpty(str)){
                    submit.setEnabled(false);
                    submit.setTextColor(defColor);
                    submit.setBackgroundResource(R.drawable.gray_round_3dp);
                }else {
                    submit.setEnabled(true);
                    submit.setTextColor(changColor);
                    submit.setBackgroundResource(R.drawable.yellow_round_3dp);

                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now =  System.currentTimeMillis();
                if (now - clickTime > 1000*2)//防止点击过快
                    reqApi();
            }
        });
    }
    private long clickTime = 0;
    @Override
    protected void reqApi() {
        if (TextUtils.isEmpty(commentId))
            return;
        String content = uiNumberEdit.getText();
        RxRequestApi.getInstance().getApiService().postReplyComment(commentId,content).compose(this.<ResponseBody<String>>applySchedulers())
                .subscribe(new Action1<ResponseBody<String>>() {
                    @Override
                    public void call(ResponseBody<String> stringResponseBody) {
                            if (stringResponseBody.isSuccess()){
                                Toast.makeText(ReplyCommentActivity.this,"回复评价成功",Toast.LENGTH_SHORT).show();
                                RxBus.get().post(BusAction.ORDER_STATE_REPLYCOMMENT,orderId);
                                KnmsShopApp.getInstance().getUnreadObservable().refreshTips();
                                if (fromList){
                                    HashMap<String,Object> params = new HashMap<>();
                                    params.put("orderId",orderId);
                                    startActivityAnimGeneral(UserCommentActivity.class,params);
                                }
                                finish();
                            }else {
                                Toast.makeText(ReplyCommentActivity.this,stringResponseBody.desc,Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    @Override
    protected String umTitle() {
        return "回复用户评价";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("回复用户评价");
    }
}
