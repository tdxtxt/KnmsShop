package com.knms.shop.android.bean.body.account;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tdx on 2016/10/18.
 */
    public class User implements Serializable {
    @SerializedName("usid")
    public String id;
    @SerializedName("usphoto")
    public String avatar;
    @SerializedName("usphone")
    public String mobile;
    @SerializedName("usnickname")
    public String nickname;
    @SerializedName("ustype")
    public String type;//用户类型（1：商户，2：维修师傅）
    @SerializedName("imtoken")
    public String token;//聊天token
    public String accountumber;
    public String pushtoken;
    public String shopname;
    public String shoppic;
    public String shopid;//店铺id

    /** 是不是维修师傅 */
    public boolean isRepairMan(){
        return  "2".equals(type);
    }
}
