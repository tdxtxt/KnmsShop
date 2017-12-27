package com.knms.shop.android.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.callback.LoadListener;
import com.knms.shop.android.callback.UnreadObservable;
import com.knms.shop.android.core.badgenumber.BadgeNumberManager;
import com.knms.shop.android.core.badgenumber.MobileBrand;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.im.cache.FriendDataCache;
import com.knms.shop.android.core.im.cache.NimUserInfoCache;
import com.knms.shop.android.core.im.config.UserPreferences;
import com.knms.shop.android.core.im.msg.ProductAttachParser;
import com.knms.shop.android.core.im.storage.StorageUtil;
import com.knms.shop.android.helper.DeviceIDFactory;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.receiver.BootCompleteReceiver;
import com.knms.shop.android.util.SPUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.orhanobut.hawk.GsonParser;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Stack;

import cn.jpush.android.api.JPushInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/30.
 */

public class KnmsShopApp extends Application {
    private static KnmsShopApp mApplication;
    private static Stack<Activity> activityStack;
    private View mLoadView;
    private UnreadObservable unreadObservable;
    public UnreadObservable getUnreadObservable(){
        if(unreadObservable == null) unreadObservable = new UnreadObservable();
        return unreadObservable;
    }
    public static KnmsShopApp getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
        initForeground();
        initImageLoader();
        initIMChat();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        Hawk.init(this).setEncryption(new NoEncryption())
//                .setConverter(new )
                .setParser(new GsonParser(new Gson()))
//                .setStorage(new )
                .build();
        initMobclick();
        initCrash();

    }

    private void initForeground() {
        if(inMainProcess()){
            //保活措施
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(new BootCompleteReceiver(),filter);

            ForegroundCallbacks.init(this);
            ForegroundCallbacks.get(this).addListener(new ForegroundCallbacks.Listener() {
                @Override
                public void onBecameForeground() {
                    RxRequestApi.getInstance().getApiService().uploadOpenRecord(DeviceIDFactory.getInstance().getUniqueID())
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ResponseBody>() {
                                @Override
                                public void call(ResponseBody body) {}
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    SPUtils.saveToApp("openRecord",true);
                                }
                            });
                }
                @Override
                public void onBecameBackground() {}
            });
        }
    }

    private void initMobclick() {
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.enableEncrypt(true);//日志加密
    }
    private void initCrash() {
//      installCrashException();//永不crash的Android
        // 获取当前包名
        String packageName = getPackageName();
        // 获取当前进程名
        String processName = getProcessName();
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(this, "1586da1596", false);//配置崩溃日志统计分享,建议在测试阶段建议设置成true，发布时设置为false。
    }
    /**
     * 初始化聊天库
     */
    private void initIMChat() {
        NIMClient.init(this, IMHelper.getInstance().getLoginInfo(), IMHelper.getInstance().getOptions());
        if(inMainProcess()){
            NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new ProductAttachParser());

            IMHelper.getInstance().registerLoginSyncDataStatus(true);
            NimUserInfoCache.getInstance().buildCache();
            FriendDataCache.getInstance().buildCache();
            NimUserInfoCache.getInstance().registerObservers(true);
            FriendDataCache.getInstance().registerObservers(true);

            // 初始化消息提醒
            NIMClient.toggleNotification(true);
            // 更新消息提醒配置 StatusBarNotificationConfig
            NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
            //存储
            StorageUtil.init(this, SDCardHelper.getCacheDir() + "/nim");

            //兼容手机应用icon右上角标数目的显示
            NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> imMessages) {
                    StatusCode statusCode = NIMClient.getStatus();
                    Integer count;
                    if(StatusCode.LOGINED != statusCode){
                        count = 0;
                    }else{
                        count = NIMClient.getService(MsgService.class).getTotalUnreadCount();
                    }
                    if(Build.MANUFACTURER.equalsIgnoreCase(MobileBrand.SAMSUNG)){
                        BadgeNumberManager.from(KnmsShopApp.getInstance()).setBadgeNumber(count ++);
                        BadgeNumberManager.from(KnmsShopApp.getInstance()).setBadgeNumber(count);
                    }else{
                        BadgeNumberManager.from(KnmsShopApp.getInstance()).setBadgeNumber(count);
                    }

                }
            },true);
        }
    }

    /**
     * 初始化图片加载库
     */
    private void initImageLoader() {
        com.nostra13.universalimageloader.utils.L.disableLogging();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(100)
                .diskCache(new UnlimitedDiskCache(SDCardHelper.getCacheImgDirFile()))// 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (null == activityStack) return null;
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = null;
        try {
            activity = activityStack.lastElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
//            ActivityManager activityMgr = (ActivityManager) context
//                    .getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.killBackgroundProcesses(context.getPackageName());
//            System.exit(0);
        } catch (Exception e) {
        }
    }
    public void showLoadViewIng(RelativeLayout layoutStatus) {
        if (layoutStatus == null) {
            return;
        }
        layoutStatus.removeAllViews();
        mLoadView = LayoutInflater.from(this).inflate(
                R.layout.layout_view_loading, null);
        RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutStatus.addView(mLoadView, lp);
        layoutStatus.setVisibility(View.VISIBLE);
    }

    public void showLoadViewFaild(RelativeLayout layoutStatus, final LoadListener listener) {
        if (layoutStatus == null) {
            return;
        }
        layoutStatus.removeAllViews();
        mLoadView = LayoutInflater.from(this).inflate(
                R.layout.layout_view_load_fail, null);

        mLoadView.findViewById(R.id.textBtn_overLoaded_new).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null)
                            listener.onclick();
                    }
                });
        RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutStatus.addView(mLoadView, lp);
        layoutStatus.setVisibility(View.VISIBLE);
    }
    public void hideLoadView(RelativeLayout layoutStatus) {
        if (layoutStatus == null) return;
        if(layoutStatus.getVisibility() == View.GONE) return;
        layoutStatus.removeAllViews();
        layoutStatus.setVisibility(View.GONE);
    }
    public void showDataEmpty(RelativeLayout layoutStatus, String text, int imgId) {
        layoutStatus.removeAllViews();
        mLoadView = LayoutInflater.from(this).inflate(R.layout.layout_view_no_data, null);
        mLoadView.setVisibility(View.VISIBLE);
        ((TextView) mLoadView.findViewById(R.id.tv_no_data)).setText(text);
        ((ImageView) mLoadView.findViewById(R.id.img_no_data)).setImageResource(imgId);
        RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutStatus.addView(mLoadView, lp);
        layoutStatus.setVisibility(View.VISIBLE);
    }
    public void showDataEmpty(RelativeLayout layoutStatus, String text, int imgId, String textBtn, final LoadListener listener) {
        layoutStatus.removeAllViews();
        mLoadView = LayoutInflater.from(this).inflate(R.layout.layout_view_no_data, null);
        mLoadView.setVisibility(View.VISIBLE);
        Button btn_bottom = (Button) mLoadView.findViewById(R.id.btn_bottom);

        if(TextUtils.isEmpty(textBtn)){
            btn_bottom.setVisibility(View.GONE);
        }else{
            btn_bottom.setVisibility(View.VISIBLE);
            btn_bottom.setText(textBtn);
        }
        btn_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onclick();
            }
        });
        ((TextView) mLoadView.findViewById(R.id.tv_no_data)).setText(text);
        ((ImageView) mLoadView.findViewById(R.id.img_no_data)).setImageResource(imgId);
        RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutStatus.addView(mLoadView, lp);
        layoutStatus.setVisibility(View.VISIBLE);
    }
    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = getProcessName();
        return packageName.equals(processName);
    }
    /**
     * 获取当前进程名
     * @return 进程名
     */
    public final String getProcessName() {
        String processName = null;
        // ActivityManager
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }
            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
