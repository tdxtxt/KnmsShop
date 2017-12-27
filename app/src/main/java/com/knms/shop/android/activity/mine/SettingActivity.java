package com.knms.shop.android.activity.mine;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.im.config.UserPreferences;
import com.knms.shop.android.core.upgrade.UpdateHelper;
import com.knms.shop.android.core.upgrade.pojo.UpdateInfo;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxUpdateApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.SystemInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

import java.io.File;
import java.text.DecimalFormat;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/10/13.
 */
public class SettingActivity extends HeadBaseActivity implements View.OnClickListener {
    private RelativeLayout mClearCache, mAboutUs, mCheckUpdate;
    private ToggleButton mPushNotification,mToggleRing;
    private TextView mCacheSize, mVersion;

    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("设置");
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        mPushNotification = (ToggleButton) findViewById(R.id.push_notification);
        mClearCache = (RelativeLayout) findViewById(R.id.clear_cache);
        mAboutUs = (RelativeLayout) findViewById(R.id.about_us);
        mCheckUpdate = (RelativeLayout) findViewById(R.id.check_update);
        mCacheSize = (TextView) findViewById(R.id.cache_data);
        mVersion = (TextView) findViewById(R.id.version_information);
        mToggleRing = findView(R.id.toggle_ring);

    }

    @Override
    protected void initData() {
        try {
            mCacheSize.setText(setFileSize(getFolderSize(SDCardHelper.getCacheDirFile())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mClearCache.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mCheckUpdate.setOnClickListener(this);
        mPushNotification.setChecked(SPUtils.getNotificationStatus());
        mPushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.saveNotificationStatus(isChecked);
                IMHelper.getInstance().enableNotification(isChecked);
            }
        });
        mToggleRing.setChecked(SPUtils.getRingStatus());
        mToggleRing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
                if(config == null){
                    config = new StatusBarNotificationConfig();
                }
                config.ring = isChecked;
                SPUtils.saveRingStatus(isChecked);
                UserPreferences.setStatusConfig(config);
                NIMClient.updateStatusBarNotificationConfig(config);
            }
        });
        reqApi();
    }

    @Override
    protected String umTitle() {
        return "设置";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_cache:
                RecursionDeleteFile(SDCardHelper.getCacheDirFile());
                Tst.showToast("成功清除缓存");
                mCacheSize.setText("0kb");
                break;
            case R.id.about_us:
                startActivityAnimGeneral(AboutActivity.class, null);
                break;
            case R.id.check_update:
                UpdateHelper updateHelper = new UpdateHelper.Builder(this)
                        .isAutoInstall(false)
                        .isThinkTime(false)
                        .build();
                updateHelper.check();
                break;
        }
    }

    public long getFolderSize(File file) throws Exception {
        long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }

        return size;
    }

    public String setFileSize(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        float f = ((float) size / (float) (1024 * 1024));

        if (f < 1.0) {
            float f2 = ((float) size / (float) (1024));

            return df.format(new Float(f2).doubleValue()) + "KB";

        } else {
            return df.format(new Float(f).doubleValue()) + "M";
        }

    }

    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    @Override
    protected void reqApi() {
        RxUpdateApi.getInstance().getApiService().clientupdate("businessand", SystemInfo.getVerSerCode()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<UpdateInfo>>() {
                    @Override
                    public void call(ResponseBody<UpdateInfo> updateInfoResponseBody) {
                        if (updateInfoResponseBody.isSuccess()) {
                            UpdateInfo info = updateInfoResponseBody.data;
                            if (info.getUpdatecliverid().equals(SystemInfo.getVerSerCode())) {
                                mVersion.setTextColor(getResources().getColor(R.color.gray_999999));
                                mVersion.setText(SystemInfo.getVerSerName() + "已是最新版本");
                            } else {
                                mVersion.setTextColor(getResources().getColor(R.color.red_ee4b62));
                                mVersion.setText("有新版本，点击更新");
                            }

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Tst.showToast(throwable.getMessage());
                        mVersion.setText("");
                    }
                });
    }
}
