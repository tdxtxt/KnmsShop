package com.knms.shop.android.bean.body.product;


import com.knms.shop.android.bean.body.media.Pic;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class StyleDetails {
    public String inid;//商品id
    public String cotitle;
    public String coremark;
    public String collectNumber;
    public String iscollectNumber;//当前用户是否收藏(0是,1否)
    public String shopid;
    public String shopName;
    public String shopPhoto;
    public String ssmerchantid;
    public List<Pic> imglist;//图片list
    public List<Prefer> preferList;//优惠券list
    public class Prefer {
        public String spid;
        public String spmoney;
        public String spconditions;
        public String spvalid;
        public String spinvalid;
    }
}
