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
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.im.ChatActivity;
import com.knms.shop.android.activity.main.MsgFragment;
import com.knms.shop.android.adapter.im.ClientAdapter;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.Client;
import com.knms.shop.android.core.im.IMHelper;
import com.knms.shop.android.core.rxbus.BusAction;
import com.knms.shop.android.core.rxbus.RxBus;
import com.knms.shop.android.core.rxbus.annotation.Subscribe;
import com.knms.shop.android.core.rxbus.annotation.Tag;
import com.knms.shop.android.fragment.base.BaseFragment;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.NetworkHelper;
import com.knms.shop.android.net.RxRequestApi;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.view.clash.FullyLinearLayoutManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.R.id.list;
import static com.knms.shop.android.R.id.refresh_scrollView;
import static com.knms.shop.android.fragment.MsgCenterFragment.RECENT_TAG_STICKY;
import static u.aly.av.S;

/**
 * Created by tdx on 2016/10/17.
 * 收藏客户
 */

public class ClientsFragment extends BaseFragment {
    private PullToRefreshScrollView pullToRefreshScrollView;
    private RecyclerView recyclerView;

    private ClientAdapter adapter;

    private CompositeSubscription mSubscriptions;
    private MsgFragment parentFragment;
    private List<Client> clients;
    private RelativeLayout rl_status;
    @Override
    public String getTitle() {
        return "收藏客户";
    }

    @Override
    protected String umTitle() {
        return "收藏客户";
    }

    public static ClientsFragment newInstance() {
        ClientsFragment fragment = new ClientsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        RxBus.get().register(this);
        mSubscriptions = new CompositeSubscription();
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comm_refresh_scrollview,null);
        initView(view);
        return view;
    }

    private void initView(View view){
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(refresh_scrollView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        rl_status = (RelativeLayout) view.findViewById(R.id.rl_status);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(getContext()));

        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ClientAdapter();
        recyclerView.setAdapter(adapter);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                reqApi();
                Observable.just("").delay(5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                pullToRefreshScrollView.onRefreshComplete();
                            }
                        });
            }
        });
        pullToRefreshScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshScrollView.setRefreshing();
            }
        },200);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<Client>() {
            @Override
            public void onItemClick(BaseQuickAdapter<Client, ? extends BaseViewHolder> adapter, View view, int position) {
                Client item = adapter.getItem(position);
                if(item != null){
                    Map<String,Object> parmas = new HashMap<String, Object>();
                    parmas.put("sid",item.id);
                    startActivityAnimGeneral(ChatActivity.class,parmas);
                }
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener<Client>() {
            @Override
            public boolean onItemLongClick(final BaseQuickAdapter<Client, ? extends BaseViewHolder> adapter, View view, final int position) {
                final Client item = adapter.getItem(position);
//                final RecentContact item = client.recentContact;
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
                            NIMClient.getService(MsgService.class).updateRecent(item.recentContact);
                            loadRecentContacts();
                        } else if (arg1 == 1) {//删除该会话
                            item.setDeleteTag(true);
                            if(item.recentContact != null){
                                RecentContact recentContact = item.recentContact;
                                NIMClient.getService(MsgService.class).deleteRecentContact(recentContact);
                                notReadCount -= recentContact.getUnreadCount();
                            }
                            adapter.remove(position);
                            if(adapter.getItemCount() == 0) KnmsShopApp.getInstance().showDataEmpty(rl_status, "您暂时还没有收藏的客户哦~", R.drawable.bg_no_data, null, null);
                            setClientsUnReadCount(notReadCount);
                            RxBus.get().post(BusAction.REFRESH_MSG_TIP, "notify");
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        registerObservers(true);
        super.onViewCreated(view, savedInstanceState);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, register);//监听最近会话变更
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, register);//注册消息已读回执对象
    }
    int notReadCount = 0;
    /**
     * 接收通知
     *   发送通知需调用RxBus.get().post(BusAction.ACTION_REFRESH,fromTag);
     */
    @Subscribe(tags = {@Tag(BusAction.ACTION_REFRESH)})
    public void actionRefresh(String fromTag) {
        reqApi();
    }

    @Override
    public void reqApi() {
        if(!NetworkHelper.isNetwork()) Tst.showToast("网络不给力，请检查网络设置");
        notReadCount = 0;
        mSubscriptions.add(Observable.zip(RxRequestApi.getInstance().getApiService().getCollectClients(), IMHelper.getInstance().getRecentContacts(),
                new Func2<ResponseBody<List<Client>>, List<RecentContact>, ResponseBody<List<Client>>>() {
                    @Override
                    public ResponseBody<List<Client>> call(ResponseBody<List<Client>> body1, List<RecentContact> body2) {
                        if(body1 != null && body1.isSuccess() && body1.data != null && body1.data.size() > 0){
                            if(clients == null) clients = new ArrayList<Client>();
                            if(!(clients.size() == body1.data.size() && clients.containsAll(body1.data))){//不相同，需要通知消息中心列表刷新数据
                                RxBus.get().post(BusAction.ACTION_REFRESH,"");
                            }
                            clients.clear();
                            clients.addAll(body1.data);

                            Iterator<Client> it = body1.data.iterator();
                            while(it.hasNext()){
                                Client client = it.next();
                                for (RecentContact item : body2){
                                    if(client.id.equals(item.getContactId())){
                                        client.recentContact = item;
                                        notReadCount += item.getUnreadCount();
                                        break;
                                    }
                                }
                                if(client.getDeleteTag()){
                                    it.remove();
                                }
                            }
                            Collections.sort(body1.data, comp);//升序(小的在前，大的在后)
                        }
                        return body1;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends ResponseBody<List<Client>>>>() {
            @Override
            public Observable<? extends ResponseBody<List<Client>>> call(Throwable throwable) {
                return Observable.empty();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody<List<Client>>>() {
                    @Override
                    public void call(ResponseBody<List<Client>> body) {
                        pullToRefreshScrollView.onRefreshComplete();
                        if (body.isSuccess()) {
                            if (body.data != null && body.data.size() > 0) {
                                KnmsShopApp.getInstance().hideLoadView(rl_status);
                                adapter.setNewData(body.data);
                            } else {
                                KnmsShopApp.getInstance().showDataEmpty(rl_status, "您暂时还没有收藏的客户哦~", R.drawable.bg_no_data, null, null);
                            }
                            setClientsUnReadCount(notReadCount);
                        } else {
                            Tst.showToast(body.desc);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pullToRefreshScrollView.onRefreshComplete();
                        Tst.showToast(throwable.toString());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        pullToRefreshScrollView.onRefreshComplete();
                    }
                }));
    }
    /****************************IM监听******************************/
    // 消息已读回执设置成功监听
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
//            updateMsg();
        }
    };
    //最近会话变更
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            loadRecentContacts();
        }
    };
    private void loadRecentContacts(){
        IMHelper.getInstance().getRecentContacts().compose(this.<List<RecentContact>>applySchedulers())
                .subscribe(new Action1<List<RecentContact>>() {
                    @Override
                    public void call(List<RecentContact> messages) {
                        List<Client> adapterData = new ArrayList<Client>();
                        if(clients != null) adapterData.addAll(clients);

                        if(adapterData != null) {
                            notReadCount = 0;
                            Iterator<Client> it = adapterData.iterator();
                            while(it.hasNext()){
                                Client client = it.next();
                                for (RecentContact item : messages) {
                                    if (client.id.equals(item.getContactId())) {
                                        client.recentContact = item;
                                        break;
                                    }
                                }
                                if (client.recentContact != null)
                                    notReadCount += client.recentContact.getUnreadCount();

                                if(client.getDeleteTag()){
                                    it.remove();
                                }
                            }
                            Collections.sort(adapterData, comp);//升序(小的在前，大的在后)
                            if(adapterData.size() > 0){
                                KnmsShopApp.getInstance().hideLoadView(rl_status);
                            }else{
                                KnmsShopApp.getInstance().showDataEmpty(rl_status, "您暂时还没有收藏的客户哦~", R.drawable.bg_no_data, null, null);
                            }
                            adapter.setNewData(adapterData);
                            setClientsUnReadCount(notReadCount);
                        }
                    }
                });
    }
    public void setClientsUnReadCount(int count){
        String msg = count < 1 ? "收藏客户" : "收藏客户(" + count + ")";
        if(parentFragment != null){(parentFragment).setTitle(msg,1);}
    }
    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        if(mSubscriptions != null) mSubscriptions.unsubscribe();
        super.onDestroy();
    }
    private static Comparator<Client> comp = new Comparator<Client>() {
        /**
         * 回负数表示o1 小于o2，返回0 表示o1和o2相等，返回正数表示o1大于o2
         * @param c1
         * @param c2
         * @return
         */
        @Override
        public int compare(Client c1, Client c2) {
            // 先比较置顶tag
            long sticky = (c1.getTag() & RECENT_TAG_STICKY) - (c2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                RecentContact o1 = c1.recentContact;
                RecentContact o2 = c2.recentContact;
                if (o1 == null) return 1;//o1>o2
                if (o2 == null) return -1;//o1<o2

                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };
}
