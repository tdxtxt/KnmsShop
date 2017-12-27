package com.knms.shop.android.bean.body.product;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class BBpriceDetail {

    public String coid;
    public String couserid;
    public String usnickname;
    public String userPhoto;
    public String updatetime;
    public String cotitle;
    public List<LabelListBean> labelList;
    public List<ImglistBean> imglist;
    
    public static class LabelListBean {
        public String laname;
    }

    public static class ImglistBean {
        public Object imageId;
        public String imageUrl;
        public Object imageSeq;
    }
}
