package com.knms.shop.android.bean.body.orderpay;

import java.util.List;

/**
 * Created by 654654 on 2017/8/31.
 */

public class OrderPayCommentListData {
    public String id;//评价id
    public String userId;//用户id
    public String userPhoto;//用户头像
    public String nikeName;//昵称
    public String imToken;//云信唯一标识
    public double score;//	星级
    public int state;//是否为精彩评价（1：否，2：是）
    public String created;//评价时间
    public String content;//评价内容
    public String browsenumber;//浏览量
    public String parameterbriefing;//商品名称 (商城新增字段
    public int agreenumber;//点赞数量
    public int isAgree;//是否点赞，0：否，1：是
    public List<ImgList> imgList;//图片列表
    public ShopReply shopReply;//商家回复
    public class ShopReply {
        public String content;//
        public String created;//
    }

    public class ImgList {
        public String imageId;//
        public String imageUrl;//
    }
}
