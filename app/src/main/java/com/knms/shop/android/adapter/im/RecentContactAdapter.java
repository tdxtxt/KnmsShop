package com.knms.shop.android.adapter.im;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.im.cache.NimUserInfoCache;
import com.knms.shop.android.fragment.MsgCenterFragment;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.StrHelper;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import rx.functions.Action1;

import static com.knms.shop.android.R.id.tv_name;

/**
 * Created by tdx on 2016/10/17.
 */

public class RecentContactAdapter extends BaseQuickAdapter<RecentContact,BaseViewHolder> {
    public RecentContactAdapter(List<RecentContact> data) {
        super(R.layout.item_msg, data);
    }
    @Override
    protected void convert(final BaseViewHolder helper, RecentContact item) {
        boolean isTag = IMHelper.getInstance().isTagSet(item, MsgCenterFragment.RECENT_TAG_STICKY);//是否是置顶消息
        if(isTag){
            helper.getConvertView().setBackgroundResource(R.color.blue_edfaff);
        }else{
            helper.getConvertView().setBackgroundResource(R.color.white_ffffff);
        }
        final ImageView iv_avatar = helper.getView(R.id.iv_avatar);
        final TextView tv_name = helper.getView(R.id.tv_name);
        NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfoFromLocal(item.getContactId());
        String name = "未知账号", avatar = "drawable://" + R.drawable.icon_avatar;
        if(userInfo != null){
            name = userInfo.getName();
            avatar = TextUtils.isEmpty(userInfo.getAvatar()) ? "drawable://" + R.drawable.icon_avatar : userInfo.getAvatar();
        }else {
            NimUserInfoCache.getInstance().getUserInfoObserable(item.getContactId())
                    .subscribe(new Action1<NimUserInfo>() {
                        @Override
                        public void call(NimUserInfo nimUserInfo) {
                            if(nimUserInfo != null){
                                tv_name.setText(nimUserInfo.getName());
                                ImageLoadHelper.getInstance().displayImageHead(nimUserInfo.getAvatar(), iv_avatar);
                            }
                        }
                    });
        }
        tv_name.setText(name);
        ImageLoadHelper.getInstance().displayImageHead(avatar, iv_avatar);
        helper.setText(R.id.tv_new_count,item.getUnreadCount() + "")
                .setText(R.id.tv_data, StrHelper.showImTime(item.getTime()))
                .setText(R.id.tv_content,item.getContent());
    }
}
