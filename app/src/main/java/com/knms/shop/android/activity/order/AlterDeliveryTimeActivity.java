package com.knms.shop.android.activity.order;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.ui.UiNumberEdit;
import com.knms.shop.android.ui.UiPickView;
import com.knms.shop.android.util.CalendarUtils;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * 延迟送货时间
 * Created by 654654 on 2017/5/2.
 */

public class AlterDeliveryTimeActivity extends HeadBaseActivity {
    private UiNumberEdit uiNumberEdit;
    private UiPickView uiPickView;
    private TextView alterTime,submit;
    @Override
    protected int layoutResID() {
        return R.layout.activity_alter_deliverytime;
    }

    @Override
    protected void initView() {
        uiNumberEdit = findView(R.id.message);
        uiPickView = findView(R.id.timePick);
        alterTime = findView(R.id.alterTime);
        submit = findView(R.id.submit);
    }

    private String orderId,orderTime,oldOrderTime;
    @Override
    protected void getParmas(Intent intent) {
        if (null != intent){
            orderId = intent.getStringExtra("orderId");
            orderTime = intent.getStringExtra("orderTime");
        }
    }

    @Override
    protected void initData() {
        uiNumberEdit.setCursorVisible(false);
        uiNumberEdit.setHint("请填写备注信息");
        uiNumberEdit.setMaxLength(50);
        int year = CalendarUtils.getyear(null);
        String nowTime = CalendarUtils.getToday(null);
        oldOrderTime = orderTime;
        if (TextUtils.isEmpty(orderTime)){
            orderTime = nowTime;
        }else {
            orderTime = CalendarUtils.getDataFormat(orderTime);
            if ( -1 ==  CalendarUtils.compare(orderTime,nowTime)) {
                orderTime = nowTime;
            }
        }
        if (isErrorTime(orderTime)){
//            submit.setEnabled(false);
            submit.setBackgroundResource(R.drawable.gray_round_3dp);
        }else {
            submit.setBackgroundResource(R.drawable.yellow_round_3dp);
//            submit.setEnabled(true);
        }
        String oldYear = "",olMonth = "",oldDay = "";
        String[] data = orderTime.split("-");
        if (data.length == 3) {
            oldYear = data[0];
            try {
                olMonth = Integer.parseInt(data[1]) + "";//小于10去掉前面的0 与控件显示文字格式一致
                oldDay = Integer.parseInt(data[2]) + "";
            }catch (Exception ex){}
        }
        uiPickView.createTimeBuilder(year - 1,year + 1).setDefData(oldYear,olMonth,oldDay).setLabelRight(10).builder();//设置时间范围和默认显示时间
        alterTime.setText(uiPickView.getItem1()+"年"+
                uiPickView.getItem2()+"月"+
                uiPickView.getItem3()+"日");
        uiPickView.setOnPickChange(new UiPickView.OnPickChange() {
            @Override
            public void onChange(String item1, String item2, String item3) {
                alterTime.setText(item1+"年"+item2+"月"+item3+"日");
                String atime = CalendarUtils.getDataFormat(item1+"-"+item2+"-"+item3);//防止格式不一致比较出错
                //时间必须大于等于今天 大于ordertime
                if (isErrorTime(atime)){
//                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.gray_round_3dp);
                }else {
                    submit.setBackgroundResource(R.drawable.yellow_round_3dp);
//                    submit.setEnabled(true);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now =  System.currentTimeMillis();
                if (now - clickTime > 1000*1) {//防止点击过快
                    AlterTime = uiPickView.getItem1()+"-"+uiPickView.getItem2()+"-"+ uiPickView.getItem3();
                    AlterTime = CalendarUtils.getDataFormat(AlterTime);//防止格式不一致比较出错
                    if (isErrorTime(AlterTime)){
                        Toast.makeText(AlterDeliveryTimeActivity.this,"只能修改为当前送货日期之后时间",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DialogHelper.showPromptDialog(AlterDeliveryTimeActivity.this, null, "送货时间确认调整为" + AlterTime, "取消", null, "确定", new DialogHelper.OnMenuClick() {
                        @Override
                        public void onLeftMenuClick() {}
                        @Override
                        public void onCenterMenuClick() {}
                        @Override
                        public void onRightMenuClick() {//确定
                            reqApi();
                        }
                    });
                }
            }
        });
    }

    private boolean isErrorTime(String aTime){
        return -1 == CalendarUtils.compare(aTime,orderTime) || -1 == CalendarUtils.compare(aTime,CalendarUtils.getToday(null)) || 0 == CalendarUtils.compare(aTime,oldOrderTime);
    }

    private long clickTime = 0;
    private String AlterTime;
    @Override
    protected void reqApi() {
        showProgress();
        RxRequestApi.getInstance().getApiService().postDelayDelivery(orderId,AlterTime,uiNumberEdit.getText().toString()).compose(this.<ResponseBody<String>>applySchedulers())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        hideProgress();
                    }
                })
                .subscribe(new Action1<ResponseBody<String>>() {
                    @Override
                    public void call(ResponseBody<String> stringResponseBody) {
                        if (stringResponseBody.isSuccess()) {
                            Toast.makeText(AlterDeliveryTimeActivity.this, "修改送货时间成功", Toast.LENGTH_SHORT).show();
                            RxBus.get().post(BusAction.ORDER_DELIVERY_TIME, AlterTime);
                            finish();
                        } else {
                            Toast.makeText(AlterDeliveryTimeActivity.this, stringResponseBody.desc, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected String umTitle() {
        return "修改送货时间";
    }

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("修改送货时间");
    }
}
