package com.knms.shop.android.net;

import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.bean.body.account.Client;
import com.knms.shop.android.bean.body.account.InviterFriends;
import com.knms.shop.android.bean.body.account.User;
import com.knms.shop.android.bean.body.im.KnmsMsg;
import com.knms.shop.android.bean.body.order.CompainDetail;
import com.knms.shop.android.bean.body.order.Complaints;
import com.knms.shop.android.bean.body.order.OrderDetail;
import com.knms.shop.android.bean.body.order.OrderListData;
import com.knms.shop.android.bean.body.order.OrderState;
import com.knms.shop.android.bean.body.order.UserComment;
import com.knms.shop.android.bean.body.orderpay.GlobalData;
import com.knms.shop.android.bean.body.orderpay.OrderPayBody;
import com.knms.shop.android.bean.body.orderpay.OrderPayCommentListData;
import com.knms.shop.android.bean.body.orderpay.OrderPayDetailData;
import com.knms.shop.android.bean.body.orderpay.OrderPayGoodsDetailData;
import com.knms.shop.android.bean.body.orderpay.RefundDetailData;
import com.knms.shop.android.bean.body.other.BBpriceNewCount;
import com.knms.shop.android.bean.body.other.TipNum;
import com.knms.shop.android.bean.body.product.BBprice;
import com.knms.shop.android.bean.body.product.BBpriceDetail;
import com.knms.shop.android.bean.body.product.ClassifyDetail;
import com.knms.shop.android.bean.body.product.CustFurniture;
import com.knms.shop.android.bean.body.product.CustomDetail;
import com.knms.shop.android.bean.body.product.DeStyle;
import com.knms.shop.android.bean.body.product.ShopCommodity;
import com.knms.shop.android.bean.body.product.StyleDetails;
import com.knms.shop.android.bean.body.product.StyleId;
import com.knms.shop.android.core.upgrade.pojo.UpdateInfo;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 类说明：
 *
 * @author 作者:tdx
 * @version 版本:1.0
 * @date 时间:2016年8月26日 上午10:59:07
 */
public interface RxApiService {
    //消息中心
    @POST("api/merchant/sysInfo")
    Observable<ResponseBody<KnmsMsg>> getMsgCenter();

    //凯恩官网聊天详情
    @FormUrlEncoded
    @POST("api/merchant/sysInfoList")
    Observable<ResponseBody<List<KnmsMsg>>> getknmsMsgs(@Field("pageIndex") int pageNum);

    //登录
    @FormUrlEncoded
    @POST("api/merchant/login")
    Observable<ResponseBody<User>> login(@Field("username") String username, @Field("password") String password);

    //收藏客户
    @FormUrlEncoded
    @POST("api/merchant/collectCustomer")
    Observable<ResponseBody> collectCustomer(@Field("customerId") String id);

    //取消收藏客户
    @FormUrlEncoded
    @POST("api/merchant/cancelCollect")
    Observable<ResponseBody> cancelCollectCustomer(@Field("customerId") String id);

    //收藏客户列表
    @POST("api/merchant/cusCollectList")
    Observable<ResponseBody<List<Client>>> getCollectClients();

    //首页
    @FormUrlEncoded
    @POST("api/merchant/homepage")
    Observable<ResponseBody<List<BBprice>>> getIndexData(@Field("pageIndex") int pageNum,@Field("type")int type);

    //比比价、维修的联系记录增加、更新
    @FormUrlEncoded
    @POST("api/merchant/chatRecord")
    Observable<ResponseBody> sendRecord(@Field("id") String id);

    //修改密码
    @FormUrlEncoded
    @POST("api/merchant/updatePassword")
    Observable<ResponseBody> updatePwd(@Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword);

    //退出
    @POST("api/merchant/logout")
    Observable<ResponseBody> logout();

    //意见反馈
    @FormUrlEncoded
    @POST("api/merchant/feedback")
    Observable<ResponseBody> feedback(@Field("content") String content);

    //检查更新
    @GET("clientversion/{clientType}/{cliVerId}")
    Observable<ResponseBody<UpdateInfo>> clientupdate(@Path("clientType") String clientType, @Path("cliVerId") String cliVerId);

    //是否被收藏;0未收藏，其他为已收藏
    @FormUrlEncoded
    @POST("api/merchant/checkCollect")
    Observable<ResponseBody<Integer>> checkCollect(@Field("customerId") String usid);

    //获取店铺所有商品
    @FormUrlEncoded
    @POST("api/sellerShop/goodsList")
    Observable<ResponseBody<List<ShopCommodity>>> getShopProduct(@Field("shopId") String shopId,@Field("pageIndex") int pageNum);

    //获取店铺所有定制家具
    @FormUrlEncoded
    @POST("api/sellerShop/customizedList")
    Observable<ResponseBody<List<CustFurniture>>> getShopCustomized(@Field("shopId") String shopId, @Field("pageIndex") int pageNum);

    //获取店铺所有家装风格
    @FormUrlEncoded
    @POST("api/sellerShop/decorationStyleList")
    Observable<ResponseBody<List<DeStyle>>> getShopFurnitureStyle(@Field("shopId") String shopId, @Field("pageIndex") int pageNum);

    //分类商品详情
    @FormUrlEncoded
    @POST("api/labelGoods/detail")
    Observable<ResponseBody<ClassifyDetail>> getGoodsDetail(@Field("goid")String goid);

    //定制详情
    @FormUrlEncoded
    @POST("api/customized/detail")
    Observable<ResponseBody<CustomDetail>> getCustomizedDetail(@Field("inid") String inid);

    //获取个人信息
    @POST("api/merchant/userinfo")
    Observable<ResponseBody<User>> getUser();

    //家装风格详情
    @FormUrlEncoded
    @POST("api/descorate/decorationStyleDetail")
    Observable<ResponseBody<StyleDetails>> getFurnitureStyleDeails(@Field("inid")String id);

    //家装风格下一组数据
    @FormUrlEncoded
    @POST("api/descorate/nextPageId")
    Observable<ResponseBody<List<StyleId>>> getStyleIds(@Field("type") String type, @Field("typeId") String typeId, @Field("shopId") String shopId);

    //获取比比货是否有新类容
    @FormUrlEncoded
    @POST("api/merchant/hasnewparity")
    Observable<ResponseBody<BBpriceNewCount>> hasNewBBprice(@Field("innertime") String knmsTime, @Field("time") String mytime);

    //比比价详情
    @FormUrlEncoded
    @POST("api/merchant/paritydetail")
    Observable<ResponseBody<BBpriceDetail>> getBBpriceDetail(@Field("id") String id);

    //邀请好友
    @FormUrlEncoded
    @POST("api/merchant/invite/save")
    Observable<ResponseBody<String>> saveInviter(@Field("phoneNumber")String phoneNumber);

    @FormUrlEncoded
    @POST("api/merchant/invite/list")
    Observable<ResponseBody<InviterFriends>> getInviteList(@Field("pageIndex")int pageIndex);

    /* ************************************** 订单信息 start ************************************** */
    /** 获取订单列表 */
    @FormUrlEncoded
    @POST("api/merchant/order/list")
    Observable<ResponseBody<List<OrderListData>>> getOrderList(@Field("shopId") String shopId, @Field("state") int state, @Field("pageIndex") int pageIndex);

    /** 获取订单详情 */
    @FormUrlEncoded
    @POST("api/merchant/order/detail")
    Observable<ResponseBody<OrderDetail>> getOrderDetail(@Field("orderId") String orderId);

    /** 获取订单状态 */
    @FormUrlEncoded
    @POST("api/merchant/order/state")
    Observable<ResponseBody<OrderState>> getOrderState(@Field("orderId") String orderId);

    /** 提交回复评价 */
    @FormUrlEncoded
    @POST("api/merchant/order/replyComment")
    Observable<ResponseBody<String>> postReplyComment(@Field("commentId") String commentId,@Field("content") String content);

    /** 评价详情 */
    @FormUrlEncoded
    @POST("api/merchant/order/commentDetail")
    Observable<ResponseBody<UserComment>> getUserComment(@Field("orderId") String orderId);

    /** 提交回复评价 */
    @FormUrlEncoded
    @POST("api/merchant/order/delayDelivery")
    Observable<ResponseBody<String>> postDelayDelivery(@Field("orderId") String orderId,@Field("time") String time,@Field("remark") String remark);

    /** 送货完成 */
    @FormUrlEncoded
    @POST("api/merchant/order/completeDelivery")
    Observable<ResponseBody<String>> postCompleteDelivery(@Field("orderId") String orderId);

    /** 订单退货 */
    @FormUrlEncoded
    @POST("api/merchant/order/returnDelivery")
    Observable<ResponseBody<String>> postReturnDelivery(@Field("orderId") String orderId);

    /** 投诉详情 */
    @FormUrlEncoded
    @POST("api/u/myorder/complaintDetail")
    Observable<ResponseBody<CompainDetail>> getCompainDetail(@Field("ocid") String ocid);

    /**定时获取订单信息红点数量**/
    @FormUrlEncoded
    @POST("api/merchant/order/stateNumber")
    Observable<ResponseBody<TipNum>> getTipNum(@Field("shopId") String shopId);
    /* ************************************** 订单信息 end ************************************** */

    String openRedcord = "api/merchant/openRecord";
    /**上报打开应用次数**/
    @FormUrlEncoded
    @POST(openRedcord)
    Observable<ResponseBody> uploadOpenRecord(@Field("systemcode")String systemcode);

    /* ************************************** APP支付订单信息 start ************************************** */
    /** 列表订单信息 **/
    @POST("merchant/order/orderTrading_shop_list_status")
    Observable<OrderPayBody<GlobalData>> getOrderPayList(@Body Map<String,Object> params);

    /** 订单详情 **/
    @POST("merchant/order/orderTrading_shop_one")
    Observable<OrderPayBody<OrderPayDetailData>> getOrderPayDetail(@Body Map<String,Object> params);

    /** 订单发货 **/
    @POST("merchant/order/orderTrading_shop_ship")
    Observable<OrderPayBody<OrderPayDetailData>> postOrderPayShip(@Body Map<String,Object> params);

    /** 退款详情 **/
    @POST("merchant/order/orderTrading_shop_refunds_one")
    Observable<OrderPayBody<RefundDetailData>> getRefundDetail(@Body Map<String,Object> params);

    /** 评论列表 **/
    @FormUrlEncoded
    @POST("api/sellerShop/orderCommentList")
    Observable<ResponseBody<List<OrderPayCommentListData>>> getOrderPayCommentList(@Field("orderId") String orderId);

    /** 投诉列表 **/
    @FormUrlEncoded
    @POST("api/merchant/order/complaintList")
    Observable<ResponseBody<List<Complaints>>> getComplainList(@Field("orderId") String orderId);

    /** 商品详情 **/
    @POST("mall/goods/commodityShow_one")
    Observable<OrderPayBody<OrderPayGoodsDetailData>> getOrderPayGoodsDetail(@Body Map<String,Object> params);

    /* ************************************** APP支付订单信息 end ************************************** */

}
