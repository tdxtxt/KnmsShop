package com.knms.shop.android.bean.body.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class OrderState {
    public String orid;
    public int state;
    public int delayable;//是否可以延期收货（1：是，2：否）
    public String deliveryTime;//订单送货时间
    public Userinfo userinfo;
    @SerializedName("complaintList")
    public List<Complaints> complaintList;
    public List<DeliveryListBean> deliveryList;

    public class Userinfo {
        public String usPhoto;
        public String usNickname;
        public String usPhone;
        public String imToken;
        public String usId;
    }

    public class DeliveryListBean {
        public int orderstate;
        public String created;
        public String delaytime;
        public String delayRemark;
        
    }
}
