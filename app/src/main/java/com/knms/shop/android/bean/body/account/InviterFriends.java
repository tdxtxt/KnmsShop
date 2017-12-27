package com.knms.shop.android.bean.body.account;

import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

public class InviterFriends {
    public int total;
    public List<FriendsInfo> list;
    public class FriendsInfo{
        public String phoneNumber;
        public String inviteTime;
    }
}
