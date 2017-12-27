package com.knms.shop.android.bean.body.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 订单列表数据
 * Created by 654654 on 2017/4/30.
 */

public class OrderListData {
    @SerializedName("orid")
    public String orid;//订单id
    @SerializedName("orcontractno")
    public String orcontractno;//合同号
    @SerializedName("orcreated")
    public String orcreated;//订货时间
    @SerializedName("ordeliverytime")
    public String ordeliverytime;//送货时间
    @SerializedName("delayTime")
    public String  delayTime;//延期送货时间
    @SerializedName("isComplaint")
    public int isComplaint;//是否在投诉中（1：否，2：是）
    @SerializedName("deliverystate")
    public int deliverystate;//送货状态(0:退货，1：待送货，2：送货完成，3、6：确认收货，4：客户评价，5：商家回复)
    @SerializedName("orderComment")
    public orderComment orderComment;//订单评价（仅在订单状态为4待回复时 该字段有值）

    public class  orderComment{
        @SerializedName("id")
        public String id;//评价id
        @SerializedName("userPhoto")
        public String userPhoto;//头像
        @SerializedName("nikeName")
        public String nikeName;//昵称
        @SerializedName("score")
        public double score;//星级
        @SerializedName("created")
        public String created;//评价时间
        @SerializedName("content")
        public String content;//评价内容
        @SerializedName("imgList")
        public List<imgList> imgList;//评价图片列表
    }
    public class imgList{
        @SerializedName("imid")
        public String imid;//id
        @SerializedName("imgUrl")
        public String imgUrl;//url imageUrl
        public String imageUrl;//url
    }
}
