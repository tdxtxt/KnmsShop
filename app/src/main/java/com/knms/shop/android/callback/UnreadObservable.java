package com.knms.shop.android.callback;

import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.other.TipNum;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;

import java.util.Observable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tdx on 2017/4/26.
 * 被观察者-发送数据方
 */

public class UnreadObservable extends Observable {
    public void sendData(TipNum data){
        this.setChanged();//标记此 Observable对象为已改变的对象
        this.notifyObservers(data);//通知所有的观察者
    }
    public void refreshTips(){
        User user = SPUtils.getUser();
        if(user == null) return;
        RxRequestApi.getInstance().getApiService().getTipNum(user.shopid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<TipNum>>() {
                    @Override
                    public void call(ResponseBody<TipNum> body) {
                        if (body.isSuccess()) {
                            sendData(body.data);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {}
                });
    }
}
