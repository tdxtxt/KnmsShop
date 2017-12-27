package com.knms.shop.android.adapter.mineshop;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.product.Attribute;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public class AttrAdapter extends BaseQuickAdapter<Attribute,BaseViewHolder> {
    public AttrAdapter(List<Attribute> data) {
        super(R.layout.item_goods_attribute, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, Attribute item) {
        helper.setText(R.id.tv_name,item.name).setText(R.id.tv_value,item.value);
        if(helper.getLayoutPosition() == 0){
            helper.getView(R.id.view_top).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.view_top).setVisibility(View.GONE);
        }

        if(helper.getLayoutPosition() == getData().size() - 1){
            helper.getView(R.id.view_bottom).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.view_bottom).setVisibility(View.GONE);
        }

    }
}
