package com.knms.shop.android.bean.body.account;

import com.google.gson.annotations.SerializedName;
import com.knms.shop.android.bean.body.im.KnmsMsg;
import com.knms.shop.android.util.SPUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;

/**
 * Created by tdx on 2016/10/18.
 */

public class Client implements Serializable{
    private static final long serialVersionUID = -4637589207771727409L;
    @SerializedName("customerid")
    public String id;
    @SerializedName("usphoto")
    public String avatar;
    @SerializedName("usnickname")
    public String name;

    public RecentContact recentContact;

    private long tag;
    @Override
    public String toString() {
        return "id:" + this.id;
    }
    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof Client){
            return this.toString().equals(arg0.toString());
        }else{
            return false;
        }
    }

    public long getTag() {
//        if(this.recentContact != null) return recentContact.getTag();
        return Hawk.get("tag_" + SPUtils.getCurrentMobile() + id,0L);
    }

    public void setTag(long tag) {
        this.tag = tag;
//        if(this.recentContact != null){
//            this.recentContact.setTag(tag);
//            NIMClient.getService(MsgService.class).updateRecent(this.recentContact);
//        }else{
            Hawk.put("tag_"+ SPUtils.getCurrentMobile() + id,tag);
//        }
    }

    public void setDeleteTag(boolean isDelete){
        Hawk.put("delete_tag_" + SPUtils.getCurrentMobile() + id, isDelete);
    }

    public boolean getDeleteTag(){
        if(this.recentContact != null){
            Hawk.delete("delete_tag_" + SPUtils.getCurrentMobile() + id);
            return false;
        }
        return Hawk.get("delete_tag_" + SPUtils.getCurrentMobile() + id,false);
    }
}
