package com.knms.shop.android.bean.body.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 评价详情数据
 * Created by 654654 on 2017/5/3.
 */

public class UserComment {
    @SerializedName("id")
    public String id;//评价id
    @SerializedName("userPhoto")
    public String userPhoto;//用户头像
    @SerializedName("nikeName")
    public String nikeName;//用户别名
    @SerializedName("score")
    public double score;//星级
    @SerializedName("created")
    public String created;//评价时间
    @SerializedName("content")
    public String content;//评价内容
    @SerializedName("browsenumber")
    public int browsenumber;//浏览数量
    @SerializedName("agreenumber")
    public int agreenumber;//点赞数量
    @SerializedName("imgList")
    public List<ImgList> imgList;//图片列表
    @SerializedName("shopReply")
    public ShopReply shopReply;//商家回复

    /** 商家回复（若没有回复则该字段为null） */
    public class ShopReply{
        public String sslogo;
        public String ssname;
        public String created;
        public String content;
    }
    /** 图片列表 */
    public class ImgList{
        public String imid;
        public String imgUrl;
        public String imageUrl;//url
    }
}
