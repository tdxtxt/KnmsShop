package com.knms.shop.android.bean.body.order;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/30.
 */
public class Complaints implements Serializable, MultiItemEntity {

    public String ocid;
    public String occontent;
    public String occomplaintstype;
    public String occreated;
    public String ocservicetime;
    public String ocdealwithtime;
    public int ocstate;
    public boolean isFold = false;


    @Override
    public int getItemType() {
        return ocstate;
    }
}
