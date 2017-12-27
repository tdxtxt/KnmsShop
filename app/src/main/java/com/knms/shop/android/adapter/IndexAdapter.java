package com.knms.shop.android.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
import com.knms.shop.android.activity.browser.ImgBrowerPagerActivity;
import com.knms.shop.android.activity.details.BBpriceDetailsFragment;
import com.knms.shop.android.activity.im.ChatActivity;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.callback.RecyclerItemClickListener;
import com.knms.shop.android.core.im.msg.Product;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.util.LocalDisplay;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.ScreenUtil;
import com.knms.shop.android.util.ToolsHelper;
import com.knms.shop.android.view.clash.FullyGridLayoutManager;
import com.knms.shop.android.view.flowlayout.FlowLayout;
import com.knms.shop.android.view.flowlayout.TagAdapter;
import com.knms.shop.android.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by tdx on 2016/10/19.
 */

public class IndexAdapter extends BaseQuickAdapter<BBprice, BaseViewHolder> {
    private int width;
    private String type;
    public IndexAdapter() {
        super(R.layout.item_release_bbprice, new ArrayList<BBprice>(0));
    }
    public IndexAdapter(String type) {
        super(R.layout.item_release_bbprice, new ArrayList<BBprice>(0));
        this.type = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, BBprice item) {
        ImageView headImg = helper.getView(R.id.iv_avatar);
        //设置头像
        if (!TextUtils.isEmpty(item.avatar))
            ImageLoadHelper.getInstance().displayImageHead(item.avatar, headImg);
        else
            headImg.setImageResource(R.drawable.icon_avatar);
        //名称
        helper.setText(R.id.tv_name, ToolsHelper.getInstance().isMobileNO(item.nickName) ? item.nickName.substring(0, 3) + "****" + item.nickName.substring(7, 11) : item.nickName)
                .setText(R.id.tv_time, StrHelper.displayTime(item.time, false, false))
                .setText(R.id.tv_content, item.content);
        if (item.area == null || item.area.equals("")) helper.setVisible(R.id.area, false);
        else helper.setText(R.id.area, item.area);
        final RecyclerView recyclerView = helper.getView(R.id.recyclerview);
        recyclerView.setLayoutManager(new FullyGridLayoutManager(helper.getConvertView().getContext(), 3));
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_img_70x70, item.imgs) {
            @Override
            protected void convert(final BaseViewHolder helper, String items) {
                ImageView iv_pic = helper.getView(R.id.iv_pic);
                ViewGroup.LayoutParams lp = iv_pic.getLayoutParams();
                if (width == 0) width = ScreenUtil.getScreenWidth() / 3;
                lp.height = lp.width = width;
                iv_pic.setLayoutParams(lp);
                ImageLoadHelper.getInstance().displayImage(items, iv_pic, LocalDisplay.dp2px(160), LocalDisplay.dp2px(160));
            }
        });
        helper.setTag(R.id.recyclerview,item);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(helper.getConvertView().getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BBprice item = (BBprice) recyclerView.getTag();
                if (item != null && position >= 0) {
                    Intent intent = new Intent(mContext, ImgBrowerPagerActivity.class);
                    ArrayList<String> list = new ArrayList<String>();
                    list.addAll(item.imgs);
                    intent.putExtra("data", list);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                } else {
                    if("shop".equals(type)) ((MainActivity) mContext).switchContent(MainActivity.getCurrentFragement(), BBpriceDetailsFragment.newInstance(item));
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
        helper.setTag(R.id.tvBtn_chat,item);
        helper.getView(R.id.tvBtn_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BBprice item = (BBprice) v.getTag();
                Map<String, Object> parmas = new HashMap<String, Object>();
                parmas.put("sid", item.userId);
                parmas.put("id", item.id);
                parmas.put("chatRecord", true);
                Product product = new Product();
                product.content = item.content;
                product.icon = (item.imgs != null && item.imgs.size() > 0) ? item.imgs.get(0) : "";
                product.price = "";
                product.productId = item.id;
                User user = SPUtils.getUser();
                if (user != null) {
                    if ("1".equals(user.type)) {//1：商户看到的只有比比货
                        product.productType = Product.ProductTpe.TYPE_BBPRICE;
                    } else if ("2".equals(user.type)) {//2：维修师傅看到的只有维修
                        product.productType = Product.ProductTpe.TYPE_REPAIR;
                    }
                }
                parmas.put("prodcut", product);
                ((BaseFragmentActivity) v.getContext()).startActivityAnimGeneral(ChatActivity.class, parmas);
            }
        });
        TagFlowLayout tagFlowLayout = helper.getView(R.id.label_layout);
        tagFlowLayout.setFocusable(false);
        if (item.labels != null && item.labels.size() > 0) {
            tagFlowLayout.setVisibility(View.VISIBLE);
            tagFlowLayout.setAdapter(new TagAdapter<String>(item.labels) {
                @Override
                public View getView(FlowLayout parent, int position, String item) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label, parent, false);
                    TextView tv_label = ((TextView) view.findViewById(R.id.label));
                    tv_label.setText(item);
                    if (!TextUtils.isEmpty(item)) {
                        tv_label.setVisibility(View.VISIBLE);
                    } else {
                        tv_label.setVisibility(View.GONE);
                    }
                    tv_label.setPadding(LocalDisplay.dp2px(7), LocalDisplay.dp2px(3), LocalDisplay.dp2px(7), LocalDisplay.dp2px(3));
                    view.setTag(item);
                    return view;
                }
            });
        } else {
            tagFlowLayout.setVisibility(View.GONE);
        }

    }
}
