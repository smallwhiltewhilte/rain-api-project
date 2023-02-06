package com.xuecheng.orders.model.dto;

import com.xuecheng.orders.model.po.XcOrders;
import lombok.Data;
import lombok.ToString;


/**
 * @description 创建商品订单
 * @author wangzan
 * @date 2023/2/1
 * @version 1.0
 */
@Data
@ToString
public class AddOrderDto  {

    /**
     * 总价
     */
    private Float totalPrice;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 订单描述
     */
    private String orderDescrip;

    /**
     * 订单明细json，不可为空
     * [{"goodsId":"","goodsType":"","goodsName":"","goodsPrice":"","goodsDetail":""},{...}]
     */
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    private String outBusinessId;

}
