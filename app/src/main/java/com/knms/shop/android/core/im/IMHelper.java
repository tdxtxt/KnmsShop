package com.knms.shop.android.core.im;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.Client;
import com.knms.shop.android.bean.body.im.KnmsMsg;
import com.knms.shop.android.core.im.cache.FriendDataCache;
import com.knms.shop.android.core.im.cache.NimUserInfoCache;
import com.knms.shop.android.core.im.config.UserPreferences;
import com.knms.shop.android.helper.L;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.SPUtils;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.knms.shop.android.R.raw.push_notify;
import static com.netease.nimlib.sdk.NIMClient.getService;

/**
 * Created by tdx on 2016/9/27.
 */
public class IMHelper {
    /**
     * 单例
     */
    public static IMHelper getInstance() {
        return InstanceHolder.instance;
    }
    static class InstanceHolder {
        final static IMHelper instance = new IMHelper();
    }
    public LoginInfo getLoginInfo() {
        LoginInfo loginInfo = SPUtils.getSerializable(SPUtils.KeyConstant.imAccount,LoginInfo.class);
        if (loginInfo != null) {
            return loginInfo;
        }
        return null;
    }
    public void relogin(){
        LoginInfo info = getLoginInfo();
        if(info != null)
            login(info.getAccount(),info.getToken());
    }
    public void login(String username,String password){
        LoginInfo loginInfo = new LoginInfo(username, password);
        SPUtils.saveSerializable(SPUtils.KeyConstant.imAccount,loginInfo);
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);
        loginRequest.setCallback(new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                L.i_im("im login, code=" + code);
                if (code == ResponseCode.RES_SUCCESS) {
                } else {
//                    Tst.showToast("im 登录失败:" + code);
                }
            }
        });
    }
    public String getAccount(){
        LoginInfo info = getLoginInfo();
        if(info != null) return info.getAccount();
        else  return "";
    }
    public String getToken(){
        LoginInfo info = getLoginInfo();
        if(info != null) return info.getToken();
        else  return "";
    }
    public SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();
        options.appKey = "1b461a0bdb48a66d14f4b9ede1677d7d";
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (config == null) {
            config = new StatusBarNotificationConfig();
        }
        // 点击通知需要跳转到的界面
        config.notificationEntrance = MainActivity.class;
        config.notificationSmallIconId = R.drawable.icon_avatar;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.knms.shop.android/raw/" + R.raw.push_notify;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        options.statusBarNotificationConfig = config;
//        DemoCache.setNotificationConfig(config);
        UserPreferences.setStatusConfig(config);

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = SDCardHelper.getCacheDir() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 用户信息提供者
        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = infoProvider;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
//        options.messageNotifierCustomization = messageNotifierCustomization;
        return options;
    }
    public void logout() {
        SPUtils.clearSerializable(SPUtils.KeyConstant.imAccount, LoginInfo.class);
        getService(AuthService.class).logout();
        FriendDataCache.getInstance().clear();
        NimUserInfoCache.getInstance().clear();
    }


    public UserInfoProvider infoProvider =  new UserInfoProvider() {
        @Override
        public UserInfoProvider.UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfoFromLocal(account);
            if (user == null) {
//                user = NimUserInfoCache.getInstance().getUserInfoFromLocal(account);
            }
            return user;
        }
        /**
         * 如果根据用户账号找不到UserInfo的avatar时，显示的默认头像资源ID
         * @return 默认头像的资源ID
         */
        @Override
        public int getDefaultIconResId() {
            return R.drawable.icon_avatar;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            // 从内存缓存中查找群头像
            // 默认图
            Drawable drawable = KnmsShopApp.getInstance().getResources().getDrawable(R.drawable.icon_avatar);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            return null;
        }
        /**
         * 为通知栏提供用户头像（一般从本地缓存中取，若未下载或本地不存在，返回null，通知栏将显示默认头像）
         * @return 头像位图
         */
        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoadHelper.getInstance().getBitmapFromCache(user.getAvatar()) : null;
        }
        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum
        sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                if(NimUserInfoCache.getInstance().getUserInfoFromLocal(account) != null)
                    nick = NimUserInfoCache.getInstance().getUserInfoFromLocal(account).getName();
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = "Team";
            }
            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }
            return nick;
        }
    };

    Observer<LoginSyncStatus> loginSyncStatusObserver = new Observer<LoginSyncStatus>() {
        @Override
        public void onEvent(LoginSyncStatus status) {
            //同步开始时，SDK 数据库中的数据可能还是旧数据（如果是首次登录，那么 SDK 数据库中还没有数据，重新登录时 SDK 数据库中还是上一次退出时保存的数据）
            if (status == LoginSyncStatus.BEGIN_SYNC) {
                L.i_im("login sync data begin");
            } else if (status == LoginSyncStatus.SYNC_COMPLETED) {//同步完成时， SDK 数据库已完成更新。
                L.i_im("login sync data completed");
            }
        }
    };
    /**
     * 登录成功后，SDK 会立即同步数据（用户资料、用户关系、群资料、离线消息、漫游消息等），同步开始和同步完成都会发出通知。
     * 在App启动时向SDK注册登录后同步数据过程状态的通知
     * 调用时机：主进程Application onCreate中
     */
    public void registerLoginSyncDataStatus(boolean register) {
        L.i_im("observe login sync data completed event on Application create");
        getService(AuthServiceObserver.class).observeLoginSyncDataStatus(loginSyncStatusObserver, register);
    }

    // 发送文本消息
    public IMMessage sendTextMsg(String account, String txtMsg,RequestCallback callback) {
        IMMessage msg = MessageBuilder.createTextMessage(account, SessionTypeEnum.P2P, txtMsg);
        return sendMsg(msg,callback);
    }
    // 发送图片消息
    public IMMessage sendImageMsg(String account, String path,RequestCallback callback) {
        File file = new File(path);
        if (!file.exists()) {
            Tst.showToast("图片不存在!");
            return null;
        }
        // 创建图片消息
        IMMessage msg = MessageBuilder.createImageMessage(account, SessionTypeEnum.P2P, file, null);
        return sendMsg(msg,callback);
//        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);
    }
    // 发送消息
    public IMMessage sendMsg(final IMMessage msg,RequestCallback callback) {
        Map<String,Object> map = new HashMap<>();
        map.put("fromId",getAccount());
        msg.setPushPayload(map);
        InvocationFuture invocationFuture = NIMClient.getService(MsgService.class).sendMessage(msg,true);
        if(callback != null) invocationFuture.setCallback(callback);
        return msg;
    }
    /**
     * 设置最近联系人的消息为已读
     * 聊天对象帐号，或者以下两个值：
     *  {@link # MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
     *  {@link # MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
     *  enable true显示到通知栏,fasle不显示到通知栏
     */
    public void enableMsgNotification(boolean enable) {
        if (enable) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }
    public void enableMsgNotification(String account, boolean enable) {
        if (enable) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(account, SessionTypeEnum.P2P);
        }
    }
    public void enableNotification(boolean enable){
        if(enable){//开启推送
            JPushInterface.resumePush(KnmsShopApp.getInstance());
        }else { //关闭推送
            JPushInterface.stopPush(KnmsShopApp.getInstance());
        }
        NIMClient.toggleNotification(enable);
    }
    /**
     * 获取IM聊天未读消息条数
     * @return
     */
    public Observable<Integer> getIMTotalUnreadCount (){
        return Observable.just(NIMClient.getService(MsgService.class).getTotalUnreadCount());
    }

    /**
     * 返回是否有未读消息（包含IM聊天和凯恩服务器聊天）
     * true有未读消息 false没有未读消息
     * @return
     */
    public Observable<Boolean> isUnreadMsg(){
        if(!SPUtils.isLogin()) return Observable.just(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        int count = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        if(count != 0){ //有未读消息
            return Observable.just(true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
        return RxRequestApi.getInstance().getApiService().getMsgCenter().flatMap(new Func1<ResponseBody<KnmsMsg>, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(ResponseBody<KnmsMsg> body) {
                int readNumber = 0;
                if(body != null && body.data != null)
                    readNumber += body.data.notReadNumber;
                return Observable.just(readNumber != 0);
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Boolean>>() {
            @Override
            public Observable<? extends Boolean> call(Throwable throwable) {
                return Observable.just(false);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 返回im聊天和系统未读消息总条数
     * @return
     */
    public Observable<Integer> unreadMsgCount(){
        if(!SPUtils.isLogin()) return Observable.just(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return Observable.zip(RxRequestApi.getInstance().getApiService().getMsgCenter(), getIMTotalUnreadCount(),
                new Func2<ResponseBody<KnmsMsg>, Integer, Integer>() {
                    @Override
                    public Integer call(ResponseBody<KnmsMsg> body, Integer integer) {
                        if (body.isSuccess()) {
                            if (body.data != null) {
                                integer += body.data.notReadNumber;
                            }
                        }
                        return integer;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(Throwable throwable) {
                return Observable.just(-1);
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Tst.showToast(throwable.toString());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<RecentContact>> getRecentContacts(){
        return Observable.create(new Observable.OnSubscribe<List<RecentContact>>() {
            @Override
            public void call(final Subscriber<? super List<RecentContact>> subscriber) {
                final InvocationFuture<List<RecentContact>> req = NIMClient.getService(MsgService.class).queryRecentContacts();
                req.setCallback(new RequestCallbackWrapper<List<RecentContact>>(){
                    @Override
                    public void onResult(int code, List<RecentContact> result, Throwable exception) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                });
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends List<RecentContact>>>() {
            @Override
            public Observable<? extends List<RecentContact>> call(Throwable throwable) {
                return Observable.just(new ArrayList<RecentContact>(0));
            }
        });
    }
    /**********消息置顶标记的增删**********/
    public void addTag(RecentContact recent, long tag) {
        tag = recent.getTag() | tag;
        recent.setTag(tag);
    }
    public void removeTag(RecentContact recent, long tag) {
        tag = recent.getTag() & ~tag;
        recent.setTag(tag);
    }
    public boolean isTagSet(RecentContact recent, long tag) {
        return (recent.getTag() & tag) == tag;
    }

    public void addTag(Client recent, long tag) {
        tag = recent.getTag() | tag;
        recent.setTag(tag);
    }
    public void removeTag(Client recent, long tag) {
        tag = recent.getTag() & ~tag;
        recent.setTag(tag);
    }
    public boolean isTagSet(Client recent, long tag) {
        return (recent.getTag() & tag) == tag;
    }


    public Observable<Map<String,Object>> queryMessageListByImage(final IMMessage message){
        final IMMessage anchor = MessageBuilder.createEmptyMessage(message.getSessionId(),message.getSessionType(),0);
        return Observable.unsafeCreate(new Observable.OnSubscribe<List<IMMessage>>() {
            @Override
            public void call(final Subscriber<? super List<IMMessage>> subscriber) {
                NIMClient.getService(MsgService.class).queryMessageListByType(MsgTypeEnum.image, anchor, Integer.MAX_VALUE).setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        subscriber.onNext(param);
                        subscriber.onCompleted();
                    }
                    @Override
                    public void onFailed(int code) {
                        subscriber.onError(new Throwable("code:" + code));
                        subscriber.onCompleted();
                    }
                    @Override
                    public void onException(Throwable exception) {
                        subscriber.onError(exception);
                        subscriber.onCompleted();
                    }
                });
            }
        }).map(new Func1<List<IMMessage>, Map<String,Object>>() {
            @Override
            public Map<String,Object> call(List<IMMessage> imMessages) {
                Map<String,Object> value = new HashMap<String, Object>();
                List<String> imgs = new ArrayList<String>();
                int temp = 0;
                if(imMessages != null && imMessages.size() > 0){
                    Collections.reverse(imMessages);
                    int positon = 0;
                    for (IMMessage msg: imMessages) {
                        if(compareObjects(msg,message)){
                            temp = positon;
                        }
                        FileAttachment msgAttachment = (FileAttachment) msg.getAttachment();
                        String path = msgAttachment.getPath();
                        if(TextUtils.isEmpty(path)){
                            path = msgAttachment.getUrl();
                        }
                        imgs.add(path);
                        positon ++;
                    }
                }
                value.put("data",imgs);
                value.put("position",temp);
                return value;
            }
        });
    }

    protected boolean compareObjects(IMMessage t1, IMMessage t2) {
        return (t1.getUuid().equals(t2.getUuid()));
    }

}
