package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;
import com.knms.shop.android.bean.body.media.Pic;
import com.knms.shop.android.bean.body.product.Attribute;

import java.util.List;

/**
 * Created by 654654 on 2017/9/7.
 */

public class OrderPayGoodsDetailData {
    @SerializedName("commodityShowBo")
    public CommodityShowBo commodityShowBo;

    public class CommodityShowBo {
        public String shopId;//	店铺ID
        public String ssmerchantid;//	资金账户ID(用户id)
        public String shopNickname;//	店铺昵称
        public String shopImage;//	店铺图标名
        public String showId;//	展示商品系统ID
        public String showName;//	展示商品显示名
        public String showDescription;//	展示商品描述
        public String showType;//	展示商品类型 1.实体物品 2.虚拟物品
        public String showTypeTitle;//	展示商品类型 字符标题 显示
        public String showStartTimeActivity;//	商品消费开始时间 倒计时 效果: 0:没这个效果 1：有这个效果（显示即将开始的倒计时，到计时间进行中购买按钮是灰色，到计时到0的时候， 判定是否有购买结束计时）
        public int showStartTime;//	商品消费开始时间 倒计时：秒
        public String showEndTimeActivity;//	商品消费结束时间 倒计时 效果: 0:没这个效果 1：有这个效果 （显示即将开始的倒计时，到计时间进行中购买按钮是可操作状态，到计时到0的时候，购买与收藏按钮变为灰色，不可操作）
        public int showEndTime;//	商品消费结束时间 倒计时：秒
        public String showPrice;//	展示用价 用于展示时被划掉，的小字
        public String realityPrice;//	实际价 实际的买价
        public int collectNumber;//	收藏量
        public int browseNumber;//	浏览量
        public int iscollectNumber;//	当前用户是否已收藏（0：是，1：否）
        public String showSalesTitle;//	已销售数量
        public String showFreightTitle;//	运费显示
        public String showStatus;//	商品状态 : 2.上架(正常状态) 3.下架
        public List<Pic> showImages;//图片列表
        @SerializedName("showParameters")
        public List<Attribute> showParameter;//参数
    }
}
