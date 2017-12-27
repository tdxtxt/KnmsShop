package com.knms.shop.android.core.upgrade;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.core.download.DownLoadListener;
import com.knms.shop.android.core.download.DownLoadManager;
import com.knms.shop.android.core.download.DownLoadService;
import com.knms.shop.android.core.download.dbcontrol.bean.SQLDownLoadInfo;
import com.knms.shop.android.core.upgrade.listener.OnUpdateListener;
import com.knms.shop.android.core.upgrade.pojo.UpdateInfo;
import com.knms.shop.android.core.upgrade.utils.NetWorkUtils;
import com.knms.shop.android.helper.CommonHelper;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxUpdateApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.ScreenUtil;
import com.knms.shop.android.util.SystemInfo;
import com.knms.shop.android.view.progress.FlikerProgressBar;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
//businessand
public class UpdateHelper {
    private Context mContext;
    private String checkUrl;
    private boolean isAutoInstall;
    private boolean isHintVersion;
    private boolean isForce = false;//是否强制更新
    private boolean isThinkTime = false;//false不需要时间判断
    private long intervalTime = 60 * 60 * 1000;
    private OnUpdateListener updateListener;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder ntfBuilder;
    private DownLoadManager downLoadManager;

    private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
    private static final String SUFFIX = ".apk";
    private static final String APK_PATH = "/ApkChache";
    private static final String APP_NAME = "Knms";
    private SharedPreferences preferences_update;

    private HashMap<String, String> cache = new HashMap<String, String>();

    private UpdateHelper(Builder builder) {
        this.mContext = builder.context;
        this.checkUrl = builder.checkUrl;
        this.isThinkTime = builder.isThinkTime;
        this.isAutoInstall = builder.isAutoInstall;
        this.isHintVersion = builder.isHintNewVersion;
        preferences_update = mContext.getSharedPreferences("Updater",
                Context.MODE_PRIVATE);
    }

    /**
     * 检查app是否有新版本，check之前先Builer所需参数
     */
    public void check() {
        check(null);
    }

    public void check(final OnUpdateListener listener) {
        synchronized(UpdateHelper.class) {//县城同步
            if (listener != null) {
                this.updateListener = listener;
            }
            if (mContext == null) {
                Log.e("NullPointerException", "The context must not be null.");
                return;
            }
            if (isThinkTime) {
                Date endDate = new Date(System.currentTimeMillis());
                Long data = (Long) SPUtils.getFromApp(SPUtils.KeyConstant.time, System.currentTimeMillis());
                Date curDate = new Date(data);
                long diff = endDate.getTime() - curDate.getTime();
                if (diff < intervalTime)
                    return;
            }

            RxUpdateApi.getInstance().getApiService().clientupdate("businessand", SystemInfo.getVerSerCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseBody<UpdateInfo>>() {
                        @Override
                        public void call(ResponseBody<UpdateInfo> updateInfoResponseBody) {
                            if (updateInfoResponseBody.isSuccess()) {
                                UpdateInfo info = updateInfoResponseBody.data;
                                if (SystemInfo.getVerSerCode().equals(info.getUpdatecliverid())) {
                                    if (updateListener != null) updateListener.noUpdata();
                                    return;
                                }
                                if(!TextUtils.isEmpty(info.getUpdateurl())){
                                    showUpdateUI(info);
                                    SPUtils.saveToApp(SPUtils.KeyConstant.time, System.currentTimeMillis());
                                }else {
                                    if (updateListener != null) updateListener.noUpdata();
                                }
                            } else {
                                if (updateListener != null) updateListener.onfail();
                                Tst.showToast(updateInfoResponseBody.desc);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (updateListener != null) updateListener.onfail();
                            Tst.showToast(throwable.getMessage());
                        }
                    });
        }
//		AsyncCheck asyncCheck = new AsyncCheck();
//		asyncCheck.execute(checkUrl);
//        UpdateInfo info = new UpdateInfo();
//        info.isForce = true;
//        info.apkUrl = "http://apk.r1.market.hiapk.com/data/upload/marketClient/HiMarket7.3.1.81_1474182404095.apk";
//        showUpdateUI(info);
    }

    /**
     * 2014-10-27新增流量提示框，当网络为数据流量方式时，下载就会弹出此对话框提示
     *
     * @param updateInfo
     */
    private void showNetDialog(final UpdateInfo updateInfo) {
        AlertDialog.Builder netBuilder = new AlertDialog.Builder(mContext);
        netBuilder.setTitle("下载提示");
        netBuilder.setMessage("您在目前的网络环境下继续下载将可能会消耗手机流量，请确认是否继续下载？");
        netBuilder.setNegativeButton("取消下载",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        netBuilder.setPositiveButton("继续下载",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadFileApk(updateInfo);
                    }
                });
        AlertDialog netDialog = netBuilder.create();
        netDialog.setCanceledOnTouchOutside(false);
        netDialog.show();
    }

    private FlikerProgressBar progressBar;
    private TextView tv_current_value, tv_total_value, tv_percent;
    private boolean isNotify = false;

    private void showDownLoadProgress() {
        isNotify = false;
        final Dialog dialog = new Dialog(mContext);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        int screenWidth = (int) (ScreenUtil.getScreenWidth() * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        View view = LayoutInflater.from(KnmsShopApp.getInstance()).inflate(R.layout.dialog_download_progress, null);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        tv_current_value = (TextView) view.findViewById(R.id.tv_current_value);
        tv_total_value = (TextView) view.findViewById(R.id.tv_total_value);
        tv_percent = (TextView) view.findViewById(R.id.tv_percent);
        progressBar = (FlikerProgressBar) view.findViewById(R.id.flikerbar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(keylistener);
        if (isForce) {
            btn_cancel.setEnabled(false);
            btn_cancel.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            btn_cancel.setEnabled(true);
            btn_cancel.setBackgroundColor(Color.parseColor("#FEDD4D"));
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNotify = true;
                if(updateListener != null) updateListener.noUpdata();
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);

    }

    private void download(UpdateInfo info) {
        if (downLoadManager == null) {
            return;
        }
        showDownLoadProgress();
        /*设置用户ID，客户端切换用户时可以显示相应用户的下载任务*/
        downLoadManager.changeUser("knms");
        /*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*/
        downLoadManager.setSupportBreakpoint(true);
        String taskId = StrHelper.getMd5(info.getUpdateurl());
        downLoadManager.addTask(taskId, info.getUpdateurl(), APP_NAME + SUFFIX, SDCardHelper.getCacheDir() + APK_PATH);
        downLoadManager.setSingleTaskListener(taskId, downLoadListener);
        downLoadManager.startTask(taskId);
        cache.put(APP_NAME, APP_NAME + SUFFIX);
        cache.put(APK_PATH, SDCardHelper.getCacheDir() + APK_PATH);
    }

    /**
     * 弹出提示更新窗口
     *
     * @param updateInfo
     */
    private void showUpdateUI(final UpdateInfo updateInfo) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final Dialog dialog = new Dialog(mContext);
        View view = LayoutInflater.from(KnmsShopApp.getInstance()).inflate(R.layout.dialog_version_upgrade, null);
//        final AlertDialog dialog = builder.create();

        TextView tvBtn_cancel = (TextView) view.findViewById(R.id.btn_common_prompt_left);
        TextView tvBtn_update = (TextView) view.findViewById(R.id.btn_common_prompt_right);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_common_prompt_title);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_common_prompt_content);

        if(!TextUtils.isEmpty(updateInfo.getUpdatetitle())) tv_title.setText(updateInfo.getUpdatetitle());
        if(!TextUtils.isEmpty(updateInfo.getUpdatetext())) tv_content.setText(updateInfo.getUpdatetext());

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setOnKeyListener(keylistener);
        if (updateInfo.isUpdatemust()) {
            isForce = true;
            tvBtn_cancel.setVisibility(View.GONE);
        } else {
            isForce = false;
            tvBtn_cancel.setVisibility(View.VISIBLE);
        }
        tvBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateListener != null) updateListener.nextUpdata();
                dialog.dismiss();
            }
        });
        tvBtn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils netWorkUtils = new NetWorkUtils(mContext);
                int type = netWorkUtils.getNetType();
                if (type != 1) {
                    showNetDialog(updateInfo);
                } else {//下载
                    dialog.dismiss();
                    downloadFileApk(updateInfo);
                }
            }
        });
        dialog.show();
        int screenWidth = (int) (ScreenUtil.getScreenWidth() * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        dialog.addContentView(view, new WindowManager.LayoutParams());
    }

    public DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
        }
    };

    /**
     * 通知栏弹出下载提示进度
     *
     * @param progress
     */
    private void showDownloadNotificationUI(final int progress) {
        if (mContext != null) {
            String contentText = new StringBuffer().append(progress)
                    .append("%").toString();
            PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                    0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            if (notificationManager == null) {
                notificationManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (ntfBuilder == null) {
                ntfBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setTicker("开始下载...")
                        .setContentTitle(SystemInfo.getVerName())
                        .setContentIntent(contentIntent);
            }
            ntfBuilder.setContentText(contentText);
            ntfBuilder.setProgress(100, progress, false);
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                    ntfBuilder.build());
        }
    }

    private void downloadFileApk(final UpdateInfo updateInfo) {
        if (downLoadManager == null) {
            downLoadManager = DownLoadService.getDownLoadManager();
        }
        if (downLoadManager == null) {
            mContext.startService(new Intent(mContext, DownLoadService.class));//初始化下载服务
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    downLoadManager = DownLoadService.getDownLoadManager();
                    download(updateInfo);
                }
            }, 50);
        } else {
            download(updateInfo);
        }
    }

    private DownLoadListener downLoadListener = new DownLoadListener() {
        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            progressBar.setProgress(getProgress(sqlDownLoadInfo));
            tv_current_value.setText(CommonHelper.convertFileSize(sqlDownLoadInfo.getDownloadSize()));
            tv_total_value.setText("/" + CommonHelper.convertFileSize(sqlDownLoadInfo.getFileSize()));
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            int progress = getProgress(sqlDownLoadInfo);
            //更新进度
            if (isNotify) {
                showDownloadNotificationUI(progress);
            } else {
                progressBar.setProgress(getProgress(sqlDownLoadInfo));
                tv_current_value.setText(CommonHelper.convertFileSize(sqlDownLoadInfo.getDownloadSize()));
                tv_total_value.setText("/" + CommonHelper.convertFileSize(sqlDownLoadInfo.getFileSize()));
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
        }

        @Override
        public void onError(String error, SQLDownLoadInfo sqlDownLoadInfo) {
        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            if (isNotify) {
                if (UpdateHelper.this.isAutoInstall) {
                    installApk(sqlDownLoadInfo.getFilePath() + File.separator + sqlDownLoadInfo.getFileName());
                } else {
                    if (ntfBuilder == null) {
                        ntfBuilder = new NotificationCompat.Builder(mContext);
                    }
                    ntfBuilder.setSmallIcon(mContext.getApplicationInfo().icon)
                            .setContentTitle(sqlDownLoadInfo.getFileName())
                            .setContentText("下载完成，点击安装").setTicker("任务下载完成");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(
                            Uri.parse("file://" + sqlDownLoadInfo.getFilePath() + File.separator + sqlDownLoadInfo.getFileName()),
                            "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
                    ntfBuilder.setContentIntent(pendingIntent);
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) mContext
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                            ntfBuilder.build());
                }
            } else {
                installApk(sqlDownLoadInfo.getFilePath() + File.separator + sqlDownLoadInfo.getFileName());
            }
        }
    };

    private int getProgress(SQLDownLoadInfo info) {
        if (info == null) return 0;
        long current = info.getDownloadSize();
        long total = info.getFileSize();
        return ((int) ((total == 0) ? 0 : ((float) current / (float) total) * 100));
    }

    public static class Builder {
        private Context context;
        private String checkUrl;
        private boolean isAutoInstall = true;
        private boolean isHintNewVersion = true;
        private boolean isThinkTime = false;

        public Builder(Context ctx) {
            this.context = ctx;
        }

        public Builder isThinkTime(boolean isThinkTime) {
            this.isThinkTime = isThinkTime;
            return this;
        }

        /**
         * 检查是否有新版本App的URL接口路径
         *
         * @param checkUrl
         * @return
         */
        public Builder checkUrl(String checkUrl) {
            this.checkUrl = checkUrl;
            return this;
        }

        /**
         * 是否需要自动安装, 不设置默认自动安装
         *
         * @param isAuto true下载完成后自动安装，false下载完成后需在通知栏手动点击安装
         * @return
         */
        public Builder isAutoInstall(boolean isAuto) {
            this.isAutoInstall = isAuto;
            return this;
        }

        /**
         * 当没有新版本时，是否Toast提示
         *
         * @param isHint
         * @return true提示，false不提示
         */
        public Builder isHintNewVersion(boolean isHint) {
            this.isHintNewVersion = isHint;
            return this;
        }

        /**
         * 构造UpdateManager对象
         *
         * @return
         */
        public UpdateHelper build() {
            return new UpdateHelper(this);
        }
    }

    private void installApk(String filePath) {
        File apkFile = new File(filePath);
        if (mContext != null && apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if (notificationManager != null) {
                notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
            }
        } else {
            Log.e("NullPointerException", "The context must not be null.");
        }
    }


}