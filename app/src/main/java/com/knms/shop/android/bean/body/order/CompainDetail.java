package com.knms.shop.android.bean.body.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 投诉详情
 * Created by 654654 on 2017/5/3.
 */

public class CompainDetail {
    @SerializedName("ispayment")
    public int ispayment;//是否先行赔付 1是，0否
    @SerializedName("ocstate")
    public int ocstate;//投诉状态 1未受理理,2已受理,3已完成
    @SerializedName("occreated")
    public String occreated;//投诉时间
    @SerializedName("sysCurrentTime")
    public String sysCurrentTime;//系统当前时间
    @SerializedName("ocservicetime")
    public String ocservicetime;//受理时间
    @SerializedName("ocrelationmobile")
    public String ocrelationmobile;//联系电话
    @SerializedName("ocrelationname")
    public String ocrelationname;//联系人
    @SerializedName("occomplaintstype")
    public String occomplaintstype;//投诉类型
    @SerializedName("occontent")
    public String occontent;//投诉内容
    @SerializedName("imgList")
    public List<ImgList> imgList;//图片

    public class ImgList {
        @SerializedName("imageId")
        public String imageId;//图片id
        @SerializedName("imageUrl")
        public String imageUrl;//图片链接
        public String imageSeq;
    }
}
