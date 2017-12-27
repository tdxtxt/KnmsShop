package com.knms.shop.android.adapter.im;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.account.Client;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.fragment.MsgCenterFragment;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.StrHelper;
import java.util.ArrayList;

/**
 * Created by tdx on 2016/10/17.
 */

public class ClientAdapter extends BaseQuickAdapter<Client,BaseViewHolder> {
    public ClientAdapter() {
        super(R.layout.item_msg, new ArrayList<Client>(0));
    }
    @Override
    protected void convert(BaseViewHolder helper, Client item) {
        boolean isTag = IMHelper.getInstance().isTagSet(item, MsgCenterFragment.RECENT_TAG_STICKY);//是否是置顶消息
        if(isTag){
            helper.getConvertView().setBackgroundResource(R.color.blue_edfaff);
        }else{
            helper.getConvertView().setBackgroundResource(R.color.white_ffffff);
        }

        ImageView iv_avatar = helper.getView(R.id.iv_avatar);
        ImageLoadHelper.getInstance().displayImageHead(item.avatar,iv_avatar);
        helper.setText(R.id.tv_name, item.name);
        if(item.recentContact != null){
            helper.setText(R.id.tv_new_count, item.recentContact.getUnreadCount() + "")
                    .setText(R.id.tv_data, StrHelper.showImTime(item.recentContact.getTime()))
                    .setText(R.id.tv_content, item.recentContact.getContent());
        }else{
            helper.setText(R.id.tv_new_count, "0").setText(R.id.tv_data, "").setText(R.id.tv_content, "");
        }

    }
}
