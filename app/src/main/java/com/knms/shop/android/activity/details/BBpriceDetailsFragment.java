package com.knms.shop.android.activity.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseFragmentActivity;
import com.knms.shop.android.activity.browser.ImgBrowerPagerActivity;
import com.knms.shop.android.activity.im.ChatActivity;
import com.knms.shop.android.activity.main.MainActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.bean.body.product.BBpriceDetail;
import com.knms.shop.android.core.im.msg.Product;
import com.knms.shop.android.fragment.base.BaseHeadFragment;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;
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
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/3/21.
 */

public class BBpriceDetailsFragment extends BaseHeadFragment {
    ImageView iv_avatar;
    TextView tv_name, tv_time, tv_content, tv_area, tvBtn_chat;
    RecyclerView recyclerview;
    TagFlowLayout tagFlowLayout;
    BBprice data;
    String id;
    int width, type = 0;
    RelativeLayout rl_status;

    public static BBpriceDetailsFragment newInstance(BBprice data) {
        BBpriceDetailsFragment fragment = new BBpriceDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        fragment.setArguments(args);
        return fragment;
    }

    public static BBpriceDetailsFragment newInstance(String id, int type) {
        BBpriceDetailsFragment fragment = new BBpriceDetailsFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = (BBprice) getArguments().getSerializable("data");
        if (data == null) {
            id = getArguments().getString("id");
            type = getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bbprice_details, null);
        rl_status = findView(view, R.id.rl_status);
        iv_avatar = findView(view, R.id.iv_avatar);
        tv_name = findView(view, R.id.tv_name);
        tv_time = findView(view, R.id.tv_time);
        tv_content = findView(view, R.id.tv_content);
        tv_area = findView(view, R.id.area);
        tvBtn_chat = findView(view, R.id.tvBtn_chat);
        recyclerview = findView(view, R.id.recyclerview);
        recyclerview.setLayoutManager(new FullyGridLayoutManager(getmActivity(), 3));
        tagFlowLayout = findView(view, R.id.label_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setTitle("求购详情");
        reqApi();
        super.onViewCreated(view, savedInstanceState);
        //必须放到super方法的后面
        findView(view, R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    ((MainActivity) getActivity()).toHomePage();
                } else {
                    getActivity().finish();
                }
            }
        });
    }

    private void updateView(final BBprice data) {
        if (data != null) {
            //设置头像
            if (!TextUtils.isEmpty(data.avatar))
                ImageLoadHelper.getInstance().displayImageHead(data.avatar, iv_avatar);
            else
                iv_avatar.setImageResource(R.drawable.icon_avatar);
            //名称
            tv_name.setText(ToolsHelper.getInstance().isMobileNO(data.nickName) ? data.nickName.substring(0, 3) + "****" + data.nickName.substring(7, 11) : data.nickName);
            tv_time.setText(StrHelper.displayTime(data.time, false, false));
            tv_content.setText(data.content);

            if (TextUtils.isEmpty(data.area)) {
                tv_area.setVisibility(View.GONE);
            } else {
                tv_area.setText(data.area);
            }
            recyclerview.setFocusable(false);
            recyclerview.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_img_70x70, data.imgs) {
                @Override
                protected void convert(final BaseViewHolder helper, String items) {
                    ImageView iv_pic = helper.getView(R.id.iv_pic);
                    ViewGroup.LayoutParams lp = iv_pic.getLayoutParams();
                    if (width == 0) width = ScreenUtil.getScreenWidth() / 3;
                    lp.height = lp.width = width;
                    iv_pic.setLayoutParams(lp);
                    ImageLoadHelper.getInstance().displayImage(items, iv_pic, LocalDisplay.dp2px(160), LocalDisplay.dp2px(160));
                    iv_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ImgBrowerPagerActivity.class);
                            ArrayList<String> list = new ArrayList<String>();
                            list.addAll(data.imgs);
                            intent.putExtra("data", list);
                            intent.putExtra("position", helper.getAdapterPosition());
                            mContext.startActivity(intent);
                        }
                    });
                }
            });
        }
        tvBtn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data == null) return;
                Map<String, Object> parmas = new HashMap<String, Object>();
                parmas.put("sid", data.userId);
                parmas.put("id", data.id);
                parmas.put("chatRecord", true);
                Product product = new Product();
                product.content = data.content;
                product.icon = (data.imgs != null && data.imgs.size() > 0) ? data.imgs.get(0) : "";
                product.price = "";
                product.productId = data.id;
                User user = SPUtils.getUser();
                if (user != null) {
                    if ("1".equals(user.type)) {//1：商户看到的只有比比货
                        product.productType = "7";
                    } else if ("2".equals(user.type)) {//2：维修师傅看到的只有维修
                        product.productType = "6";
                    }
                }
                parmas.put("prodcut", product);
                ((BaseFragmentActivity) v.getContext()).startActivityAnimGeneral(ChatActivity.class, parmas);
            }
        });

        if (data.labels != null && data.labels.size() > 0) {
            tagFlowLayout.setVisibility(View.VISIBLE);
            tagFlowLayout.setAdapter(new TagAdapter<String>(data.labels) {
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

    @Override
    public void reqApi() {
        Observable<BBprice> observable;
        if (data == null) {
            observable = RxRequestApi.getInstance().getApiService().getBBpriceDetail(id)
                    .map(new Func1<ResponseBody<BBpriceDetail>, BBprice>() {
                        @Override
                        public BBprice call(ResponseBody<BBpriceDetail> body) {
                            if (body.code.equals("1")) {
                                return null;
                            }
                            BBpriceDetail detail = body.data;
                            BBprice bBprice = new BBprice();
                            if (detail == null) return bBprice;
                            bBprice.id = detail.coid;
                            bBprice.userId = detail.couserid;
                            bBprice.avatar = detail.userPhoto;
                            bBprice.nickName = detail.usnickname;
                            bBprice.content = detail.cotitle;
                            bBprice.time = detail.updatetime;
                            List<String> imgs = new ArrayList<String>();
                            List<String> labels = new ArrayList<String>();
                            for (BBpriceDetail.ImglistBean pic :
                                    detail.imglist) {
                                imgs.add(pic.imageUrl);
                            }
                            for (BBpriceDetail.LabelListBean lable :
                                    detail.labelList) {
                                labels.add(lable.laname);
                            }
                            bBprice.imgs = imgs;
                            bBprice.labels = labels;
                            return bBprice;
                        }
                    });
        } else {
            observable = Observable.just(data);
        }
        observable.compose(this.<BBprice>applySchedulers())
                .subscribe(new Action1<BBprice>() {
                    @Override
                    public void call(BBprice data) {
                        if (data == null)
                            KnmsShopApp.getInstance().showDataEmpty(rl_status, "该信息已被用户删除了", R.drawable.no_data_on_offer);
                        else
                            updateView(data);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Tst.showToast(throwable.getMessage());
                    }
                });
    }
}
