package com.knms.shop.android.core.im.msg;

import java.io.Serializable;

import static u.aly.av.S;

/**
 * Created by Administrator on 2016/10/20.
 */

public class Product implements Serializable {
    public String icon;
    public String content;
    public String price;
    public String productId;//详情id
    public String userId;//发布人id
    public String productType;//产品类型【1、家装风格详情(参数：风格类型type、店铺id、商品id) DecorationStyleDetailsActivity
    // 2、闲置详情(参数:商品id) IdleDetailsActivity
    // 3、定制家具详情(参数:商品id) CustomFurnitureDetailsActivity
    // 4、买手活动详情(参数:商品id) MallCommodityDetailsActivity
    // 5、分类商品详情(参数:商品id) ClassificationCommodityDetailsActivity
    // 6、我的维修详情(参数:id)】MineRepairDetailActivity
    public String attachJson;//以json格式传递参数
    public static class ProductTpe{
        public static String TYPE_STYLE = "1";//家装风格
        public static String TYPE_IDLE = "2";//闲置
        public static String TYPE_CUSTOM_FURNITURE = "3";//定制家具
        public static String TYPE_MALL_PRODCUT = "4";//买手活动
        public static String TYPE_CLASSIF_PRODCUT = "5";//家装风格
        public static String TYPE_REPAIR = "6";//我的维修
        public static String TYPE_BBPRICE = "7";//比比价

    }
    @Override
    public String toString() {
        return "icon:" + this.icon + ";content:" + this.content + ";price:" + this.price;
    }
}
