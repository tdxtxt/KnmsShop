package com.knms.shop.android.net;

import com.knms.shop.android.util.CacheManager;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017/12/1 14:04
 * 传参：
 * 返回:
 */
public class RetrofitCache {
    public static <T> Observable<T> load(final String cacheKey,
                                         Observable<T> fromNetwork) {
        return fromNetwork.map(new Func1<T, T>() {
            @Override
            public T call(T result) {
                Hawk.put(cacheKey,result);
                CacheManager.saveObject((Serializable) result, cacheKey);
                return result;
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
            @Override
            public Observable<? extends T> call(Throwable throwable) {
                return Observable.unsafeCreate(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        T cache = (T) CacheManager.readObject(cacheKey, 0);
                        if (cache != null) {
                            subscriber.onNext(cache);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        });
    }
}
