package com.knms.shop.android.adapter.mine;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.product.ShopCommodity;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.LocalDisplay;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */

public class ShopProductAdapter extends BaseQuickAdapter<ShopCommodity,BaseViewHolder> {
    public ShopProductAdapter() {
        super(R.layout.item_commodity, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShopCommodity item) {
        helper.setText(R.id.commodity_name, item.cotitle)
                .setText(R.id.browse_amount, item.browseNumber + "")
        .setText(R.id.collect_count,item.collectNumber);
        ImageView imgCommodity = helper.getView(R.id.img_commodity);
        ImageLoadHelper.getInstance().displayImage(item.coInspirationPic,imgCommodity, LocalDisplay.dp2px(160),LocalDisplay.dp2px(160));
    }
}
