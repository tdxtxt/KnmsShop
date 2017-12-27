package com.knms.shop.android.core.im.cache;

import com.knms.shop.android.helper.L;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.UserServiceObserve;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户资料数据缓存，适用于用户体系使用网易云信用户资料托管
 * 注册缓存变更通知，请使用UserInfoHelper的registerObserver方法
 * Created by huangjun on 2015/8/20.
 */

public class NimUserInfoCache {
    public static NimUserInfoCache getInstance() {
        return InstanceHolder.instance;
    }
    static class InstanceHolder {
        final static NimUserInfoCache instance = new NimUserInfoCache();
    }
    private Map<String, NimUserInfo> account2UserMap = new ConcurrentHashMap<>();
    /**
     * 构建缓存与清理
     */
    public void buildCache() {
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getAllUserInfo();
        addOrUpdateUsers(users, false);
        L.i_im("build NimUserInfoCache completed, users count = " + account2UserMap.size());
    }

    public void clear() {
        clearUserCache();
    }
    public NimUserInfo getUserInfoFromLocal(String account){
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        if(user == null){
            List<String> accounts = new ArrayList<>();
            accounts.add(account);
        }
        return user;
    }
    /**
     * ******************************* 业务接口（获取缓存的用户信息） *********************************
     */
    public Observable<NimUserInfo> getUserInfoObserable(String account){
        NimUserInfo userInfo = getUserInfoFromLocal(account);
        if(userInfo != null){
            return Observable.just(userInfo).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        }
        return Observable.create(new NimUserInfoOnSubscribe(account)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    private void clearUserCache() {
        account2UserMap.clear();
    }

    /**
     * ************************************ 用户资料变更监听(监听SDK) *****************************************
     */

    /**
     * 在Application的onCreate中向SDK注册用户资料变更观察者
     */
    public void registerObservers(boolean register) {
        NIMClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoUpdateObserver, register);
    }

    private Observer<List<NimUserInfo>> userInfoUpdateObserver = new Observer<List<NimUserInfo>>() {
        @Override
        public void onEvent(List<NimUserInfo> users) {
            if (users == null || users.isEmpty()) {
                return;
            }
            addOrUpdateUsers(users, true);
        }
    };

    /**
     * *************************************** User缓存管理与变更通知 ********************************************
     */

    private void addOrUpdateUsers(final List<NimUserInfo> users, boolean notify) {
        if (users == null || users.isEmpty()) {
            return;
        }
        // update cache
        for (NimUserInfo u : users) {
            account2UserMap.put(u.getAccount(), u);

        }
        // log
        List<String> accounts = getAccounts(users);

        // 通知变更
        if (notify && accounts != null && !accounts.isEmpty()) {
//            NimUIKit.notifyUserInfoChanged(accounts); // 通知到UI组件
        }
    }

    private List<String> getAccounts(List<NimUserInfo> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }
        List<String> accounts = new ArrayList<>(users.size());
        for (NimUserInfo user : users) {
            accounts.add(user.getAccount());
        }

        return accounts;
    }
}
