package com.knms.shop.android.bean.body.product;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.knms.shop.android.bean.body.media.Pic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class ClassifyDetail {

    public String goid;//商品id
    public String cotitle;//
    public String coremark;//
    public double gooriginal;//
    public double goprice;//
    public int collectNumber;//
    public int browseNumber;//
    public int iscollectNumber;//当前用户是否已收藏（0：是，1：否）
    public String usid;//店铺id
    public String usnickname;//
    public String userPhoto;//
    public int coState;//是否下架：0正常,非0（3或4）下架
    @SerializedName("ssmerchantid")
    public String sid;
    public List<Pic> imglist;
    public List<Coupons> preferList;//优惠券list
    public ShareEntity shareData;
    @SerializedName("attributeList")
    public List<Attribute> attributes;
    @SerializedName("serviceList")
    public List<ServiceInfo> serviceInfos;
    public class ServiceInfo{
        @SerializedName("sename")
        public String name;
        @SerializedName("seremark")
        public String remark;
        @SerializedName("sephoto")
        public String photo;
        @SerializedName("url")
        public String detailUrl;
        public int seq;
    }

    public class Coupons implements Serializable {
        public String spid;//id
        public String spmoney;//优惠金额
        public String spconditions;//优惠条件值
        public String spvalid;//优惠券开始时间
        public String spinvalid;//优惠券截止时间
        public int isReceive;
    }
    public class ShareEntity implements Serializable {
        public String title;
        public String remark;
        public String url;
        public String img;

        private int drawableId;
        private Bitmap bitmap;
    }

}
