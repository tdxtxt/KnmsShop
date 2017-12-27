package com.knms.shop.android.adapter.mine;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.bean.body.product.CustFurniture;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.ScreenUtil;


/**
 * Created by Administrator on 2017/3/27.
 */

public class ShopCustomFurnitureAdapter extends BaseQuickAdapter<CustFurniture,BaseViewHolder> {
    public ShopCustomFurnitureAdapter() {
        super(R.layout.item_custom_furniture, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, CustFurniture item) {
        ImageView iv_icon = helper.getView(R.id.iv_icon);
        iv_icon.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenWidth()));
        TextView tv_xx = helper.getView(R.id.tv_xx);
        TextView tv_browse = helper.getView(R.id.tv_browse);
        tv_xx.setCompoundDrawablePadding(LocalDisplay.dp2px(5));
        tv_browse.setCompoundDrawablePadding(LocalDisplay.dp2px(5));
        tv_browse.setText(item.browseNumber + "");
        tv_xx.setText(item.collectNumber + "");
        helper.setText(R.id.tv_desc, item.cotitle);
        ImageLoadHelper.getInstance().displayImage(item.coInspirationPic, iv_icon,ScreenUtil.getScreenWidth(),ScreenUtil.getScreenWidth());
    }
}
