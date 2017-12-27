package com.knms.shop.android.bean.body.product;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tdx on 2016/10/19.
 */

public class BBprice implements Serializable{
    public String id;
    public String userId;
    @SerializedName("usPhoto")
    public String avatar;
    @SerializedName("usNickname")
    public String nickName;
    @SerializedName("releaseTime")
    public String time;
    public String content;
    @SerializedName("labelList")
    public List<String> labels;
    @SerializedName("imgList")
    public List<String> imgs;
    public String area;

}
