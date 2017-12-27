package com.knms.shop.android.bean.body.orderpay;

import java.util.List;

/**
 * Created by 654654 on 2017/8/30.
 */

public class RefundDetailData {

    public OrderRecedeBos orderRecedeBo;
    public OrderTradingCommodityBo orderTradingCommodityBo;

    public class OrderRecedeBos {
        public String recedeId;//订单ID
        public String tradingCommodityId;//
        public String tradingId;//
        public String recedeType;//订单处理类型 1、仅退款。 3、退款取消 4、退款已经处理',
        public String recedeMoney;//退款商品金额 合计
        public String recedeFreightMoney;//退款运费金额 总和
        public String recedeReason;//退款原因
        public String recedeRemarks;//退款说明
        public String createTime;//创建时间
        public List<String> recedeImg;//退款 说明图片 无排序
        public String recedeTo;//退款去向
    }
    public class OrderTradingCommodityBo {
        public String tradingCommodityId;//订单商品ID
        public String showId;//
        public String showName;//商品展示名
        public String specificationImg;//	展示图片
        public String parameterBriefing;// 规格简述
        public String totalRealityMoney;//	商品费用
        public String totalTransportMoney;// 商品运费
    }
}
