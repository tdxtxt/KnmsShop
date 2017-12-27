package com.knms.shop.android.activity.im;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.adapter.im.KnmsChatAdapter;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.im.KnmsMsg;
import com.knms.shop.android.callback.LoadListener;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.listview.AutoRefreshListView;
import com.knms.shop.android.view.listview.MessageListView;

import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tdx on 2016/9/29.
 * 铠恩生意宝官方消息列表界面
 */

public class KnmsChatActivity extends HeadBaseActivity {
    private MessageListView listView;
    private int pageNum = 1;
    private RelativeLayout rl_status;
    private KnmsChatAdapter adapter;
    private Subscription subscription;
    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("铠恩买手");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        findView(R.id.include_footerview).setVisibility(View.GONE);
        listView = findView(R.id.lv_chat);
        rl_status = findView(R.id.rl_status);
        listView.setMode(AutoRefreshListView.Mode.START);
    }

    @Override
    protected void initData() {
        adapter = new KnmsChatAdapter(this,null);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            @Override
            public void onRefreshFromStart() {
                reqApi();
            }
            @Override
            public void onRefreshFromEnd() {
            }
        });
        pageNum = 1;
        KnmsShopApp.getInstance().showLoadViewIng(rl_status);
        reqApi();
    }

    @Override
    protected String umTitle() {
        return "铠恩生意宝官方消息列表";
    }

    @Override
    protected void reqApi() {
        subscription = RxRequestApi.getInstance().getApiService().getknmsMsgs(pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<List<KnmsMsg>>>() {
                    @Override
                    public void call(ResponseBody<List<KnmsMsg>> body) {
                        listView.onRefreshComplete();
                        KnmsShopApp.getInstance().hideLoadView(rl_status);
                        if(body.isSuccess()){
                            updateView(body.data);
                            if(pageNum == 1) RxBus.get().post(BusAction.REFRESH_MSG_TIP,KnmsChatActivity.class.getSimpleName());
                            pageNum ++;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listView.onRefreshComplete();
                        if(pageNum == 1){
                            KnmsShopApp.getInstance().showLoadViewFaild(rl_status, new LoadListener() {
                                @Override
                                public void onclick() {
                                    KnmsShopApp.getInstance().showLoadViewIng(rl_status);
                                    reqApi();
                                }
                            });
                        }else{
                            KnmsShopApp.getInstance().hideLoadView(rl_status);
                        }
                        Tst.showToast(throwable.toString());
                    }
                });
    }

    private void updateView(List<KnmsMsg> data) {
        if(pageNum == 1){
            adapter.setNewData(data);
            listView.setSelection(adapter.getCount());
        }else{
            listView.setSelection(adapter.addData(data));
        }
    }

    @Override
    protected void onDestroy() {
        if(subscription != null) subscription.unsubscribe();
        super.onDestroy();
    }
}
