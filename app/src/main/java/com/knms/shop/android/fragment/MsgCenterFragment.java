package com.knms.shop.android.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.im.ChatActivity;
import com.knms.shop.android.activity.im.KnmsChatActivity;
import com.knms.shop.android.activity.main.MsgFragment;
import com.knms.shop.android.adapter.im.RecentContactAdapter;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.Client;
import com.knms.shop.android.bean.body.im.KnmsMsg;
import com.knms.shop.android.bean.body.im.MsgCenterData;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.StrHelper;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.NetworkHelper;
import com.knms.shop.android.net.RetrofitCache;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tdx on 2016/10/17.
 * 消息中心
 */

public class MsgCenterFragment extends BaseFragment {
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag
    private PullToRefreshScrollView refresh_scrollView;
    private CompositeSubscription mSubscriptions;
    private RecentContactAdapter adapter;

    private MsgFragment parentFragment;
    int knmsNotReadMsgCount = 0;//记录凯恩系统未读信息条数
    int chatCount = 0;//记录聊天未读消息条数

    View view_official;
    RecyclerView rv_im_msgs;

    @Override
    public String getTitle() {
        return "消息中心";
    }

    @Override
    protected String umTitle() {
        return "消息中心";
    }

    public static MsgCenterFragment newInstance() {
        MsgCenterFragment fragment = new MsgCenterFragment();
//        Bundle args = new Bundle();
//        args.putString("params", params);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            params = getArguments().getString("params");
        }
        RxBus.get().register(this);
    }
    private void initView(View view){
        refresh_scrollView = (PullToRefreshScrollView) view.findViewById(R.id.refresh_scrollView);
        refresh_scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        view_official = view.findViewById(R.id.item_msg_official);
        view_official.setVisibility(View.GONE);
        rv_im_msgs = (RecyclerView) view.findViewById(R.id.rv_im_msgs);
        rv_im_msgs.setFocusable(false);
        rv_im_msgs.setLayoutManager(new FullyLinearLayoutManager(getmActivity()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_center,null);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<Fragment> fragments = getFragmentManager().getFragments();
        if(fragments != null){
            for (Fragment fragment:fragments) {
                if(fragment instanceof MsgFragment){
                    parentFragment = (MsgFragment) fragment;
                    break;
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RecentContactAdapter(new ArrayList<RecentContact>(0));
        rv_im_msgs.setAdapter(adapter);
        mSubscriptions = new CompositeSubscription();
        refresh_scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                reqApi();
            }
        });
        refresh_scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh_scrollView.setRefreshing();
            }
        },200);
        Observable.just("").delay(5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        refresh_scrollView.onRefreshComplete();
                    }
                });
        view_official.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityAnimGeneral(KnmsChatActivity.class,null);
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<RecentContact>() {
            @Override
            public void onItemClick(BaseQuickAdapter<RecentContact, ? extends BaseViewHolder> adapter, View view, int position) {
                RecentContact item = adapter.getItem(position);
                if(item != null){
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("sid",item.getContactId());
                    startActivityAnimGeneral(ChatActivity.class,params);
                }
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener<RecentContact>() {
            @Override
            public boolean onItemLongClick(final BaseQuickAdapter<RecentContact, ? extends BaseViewHolder> adapter, View view, final int position) {
                final RecentContact item = adapter.getItem(position);
                String title = (IMHelper.getInstance().isTagSet(item, RECENT_TAG_STICKY) ? "取消置顶" : "置顶该会话");
                AlertDialog.Builder builder = new AlertDialog.Builder(getmActivity());
                String[] strarr = {title, "删除该会话"};
                builder.setItems(strarr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == 0) {//消息置顶
                            if (IMHelper.getInstance().isTagSet(item, RECENT_TAG_STICKY)) {
                                IMHelper.getInstance().removeTag(item, RECENT_TAG_STICKY);
                            } else {
                                IMHelper.getInstance().addTag(item, RECENT_TAG_STICKY);
                            }
                            NIMClient.getService(MsgService.class).updateRecent(item);
                            loadRecentContacts();
                        } else if (arg1 == 1) {//删除该会话
//                            NIMClient.getService(MsgService.class).clearChattingHistory(item.getContactId(), SessionTypeEnum.P2P);
                            chatCount -= item.getUnreadCount();
                            NIMClient.getService(MsgService.class).deleteRoamingRecentContact(item.getContactId(), SessionTypeEnum.P2P);//删除指定最近联系人的漫游消息
                            NIMClient.getService(MsgService.class).deleteRecentContact2(item.getContactId(), SessionTypeEnum.P2P);
                            adapter.remove(position);
                            RxBus.get().post(BusAction.REFRESH_MSG_TIP, "notify");
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        registerObservers(true);
    }
    ResponseBody<List<Client>> bodyClients;
    @Override
    public void reqApi() {
        mSubscriptions.add(Observable.zip(RetrofitCache.load("collectClients",RxRequestApi.getInstance().getApiService().getCollectClients()),
                RetrofitCache.load("msgCenter",RxRequestApi.getInstance().getApiService().getMsgCenter())
                , IMHelper.getInstance().getRecentContacts(), new Func3<ResponseBody<List<Client>>, ResponseBody<KnmsMsg>, List<RecentContact>, MsgCenterData>() {
                    @Override
                    public MsgCenterData call(ResponseBody<List<Client>> body1, ResponseBody<KnmsMsg> body2, List<RecentContact> body3) {
                        MsgCenterData msgCenterData = new MsgCenterData();
                        bodyClients = body1;
                        if (body2.isSuccess()) {//系统返回的消息
                            knmsNotReadMsgCount = 0;//清0
                            msgCenterData.knmsMsg = body2.data;
                            if (body2.data != null) {
                                knmsNotReadMsgCount += body2.data.notReadNumber;
                            }
                        }else{
                            Tst.showToast(body2.desc);
                        }
                        if(body1.isSuccess() && body1.data != null && body1.data.size() > 0){//表示有收藏的人了
                            List<RecentContact> temp = new ArrayList<RecentContact>();
                            if(body3 != null){//进行剔除操作
                                for (RecentContact recent : body3) {
                                    if(!body1.data.toString().contains(recent.getContactId())){
                                        temp.add(recent);
                                    }
                                }
                                Collections.sort(temp, comp);
                            }
                            msgCenterData.recentContacts = temp;
                        }else{
                            if (body3 != null && body3.size() != 0) {
                                Collections.sort(body3, comp);
                            }
                            msgCenterData.recentContacts = body3;
                        }
                        int msgCount = 0;
                        if(msgCenterData.recentContacts != null && msgCenterData.recentContacts.size() > 0){
                            for (RecentContact recent : msgCenterData.recentContacts) {
                                msgCount += recent.getUnreadCount();
                            }
                        }
                        chatCount = msgCount;
                        msgCenterData.count = msgCount + knmsNotReadMsgCount;
                        return msgCenterData;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends MsgCenterData>>() {
                    @Override
                    public Observable<? extends MsgCenterData> call(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<MsgCenterData>() {
                    @Override
                    public void call(MsgCenterData msgCenterData) {
                        refresh_scrollView.onRefreshComplete();
                        updateKnmsMsg(msgCenterData.knmsMsg);
                        RxBus.get().post(BusAction.REFRESH_MSG_TIP, "notify");
                        adapter.setNewData(msgCenterData.recentContacts);
                        setMsgCenterUnReadCount(msgCenterData.count);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        refresh_scrollView.onRefreshComplete();
                        Tst.showToast(throwable.toString());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        refresh_scrollView.onRefreshComplete();
                    }
                }));
    }
    private void updateKnmsMsg(KnmsMsg knmsMsg) {
        if(knmsMsg != null){
            view_official.setVisibility(View.VISIBLE);
            ((TextView) view_official.findViewById(R.id.tv_name)).setText("铠恩买手");
            ((TextView) view_official.findViewById(R.id.tv_content)).setText(knmsMsg.content);
            ((TextView) view_official.findViewById(R.id.tv_data)).setText(StrHelper.displayTime(knmsMsg.time,true,false));
            ((TextView) view_official.findViewById(R.id.tv_new_count)).setText(knmsMsg.notReadNumber + "");
            ((ImageView) view_official.findViewById(R.id.iv_avatar)).setImageResource(R.drawable.icon_knms);
            view_official.findViewById(R.id.tv_decorate).setVisibility(View.VISIBLE);
        }else{
            view_official.setVisibility(View.GONE);
        }
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, register);//监听最近会话变更
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, register);//注册消息已读回执对象
    }

    /**
     *  加载最近会话列表
     */
    private void loadRecentContacts() {
        mSubscriptions.add(Observable.zip(IMHelper.getInstance().getRecentContacts(),
                bodyClients != null ? Observable.just(bodyClients) :
                RxRequestApi.getInstance().getApiService().getCollectClients(),
                new Func2<List<RecentContact>, ResponseBody<List<Client>>, MsgCenterData>() {
                    @Override
                    public MsgCenterData call(List<RecentContact> body1, ResponseBody<List<Client>> body2) {
                        MsgCenterData msgCenterData = new MsgCenterData();
                        if(body2.isSuccess() && body2.data != null && body2.data.size() > 0){//表示有收藏的人了
                            List<RecentContact> temp = new ArrayList<RecentContact>();
                            if(body1 != null){//进行剔除操作
                                for (RecentContact recent : body1) {
                                    if(!body2.data.toString().contains(recent.getContactId())){
                                        temp.add(recent);
                                    }
                                }
                                Collections.sort(temp, comp);
                            }
                            msgCenterData.recentContacts = temp;
                        }else{
                            if (body1 != null && body1.size() != 0) {
                                Collections.sort(body1, comp);
                            }
                            msgCenterData.recentContacts = body1;
                        }
                        int msgCount = 0;
                        if(msgCenterData.recentContacts != null && msgCenterData.recentContacts.size() > 0){
                            for (RecentContact recent : msgCenterData.recentContacts) {
                                msgCount += recent.getUnreadCount();
                            }
                        }
                        chatCount = msgCount;
                        msgCenterData.count = msgCount + knmsNotReadMsgCount;
                        return msgCenterData;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends MsgCenterData>>() {
            @Override
            public Observable<? extends MsgCenterData> call(Throwable throwable) {
                return Observable.just(new MsgCenterData());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<MsgCenterData>() {
                    @Override
                    public void call(MsgCenterData msgCenterData) {
                        setMsgCenterUnReadCount(msgCenterData.count);
                        adapter.setNewData(msgCenterData.recentContacts);
                    }
                }));
    }
    /**
     * 接收通知
     *   发送通知需调用RxBus.get().post(BusAction.ACTION_REFRESH,"自定义tag标记");
     */
    @Subscribe(tags = {@Tag(BusAction.ACTION_REFRESH)})
    public void actionRefresh(String fromTag) {
        reqApi();
    }
    @Subscribe(tags = {@Tag(BusAction.REFRESH_MSG_TIP)})
    public void refreshNewTip(String simpeName){
        if(KnmsChatActivity.class.getSimpleName().equals(simpeName)) {//凯恩买手清零计数红点
            knmsNotReadMsgCount = 0;
            setMsgCenterUnReadCount(chatCount);
            view_official.setVisibility(View.VISIBLE);
            ((TextView) view_official.findViewById(R.id.tv_new_count)).setText(0 + "");
        }else{
            setMsgCenterUnReadCount(chatCount + knmsNotReadMsgCount);
        }
    }
    @Subscribe(tags = {@Tag(BusAction.REFRESH_MSG_KNMS)})
    public void refreshKnmsMsg(String msg) {
        if(!NetworkHelper.isNetwork()) Tst.showToast("网络不给力，请检查网络设置");
        RxRequestApi.getInstance().getApiService().getMsgCenter()
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ResponseBody<KnmsMsg>>() {
            @Override
            public void call(ResponseBody<KnmsMsg> body) {
                if(body.isSuccess() && body.data != null){
                    updateKnmsMsg(body.data);
                    int count = chatCount + body.data.notReadNumber;
                    setMsgCenterUnReadCount(count);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {}
        });
    }
    public void setMsgCenterUnReadCount(int count){
        String msg = count < 1 ? "消息中心" : "消息中心(" + count + ")";
        if(parentFragment != null){(parentFragment).setTitle(msg,0);}
    }
    /****************************IM监听******************************/
    // 消息已读回执设置成功监听
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            loadRecentContacts();
        }
    };
    //最近会话变更
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            loadRecentContacts();
        }
    };
    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {
        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };
    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        if(mSubscriptions != null) mSubscriptions.unsubscribe();
        registerObservers(false);
        super.onDestroy();
    }
}
