package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
 * @author wangzan
 * @version 1.0
 * @description 订单服务service
 * @date 2023/2/2
 */
public interface OrderService {
    /**
     * @param userId      用户id
     * @param addOrderDto 订单信息
     * @return com.xuecheng.orders.model.dto.PayRecordDto
     * @description 创建商品订单
     * @date 2023/2/2
     **/
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @description 根据交易记录号查询
     * @date 2023/2/2
     * @param    payNo 交易记录号
     **/
    XcPayRecord getPayRecordByPayNo(String payNo);
/**
 * @description 保存支付宝支付结果
 * @date 2023/2/2
 * @param	payStatusDto 支付结果信息
 * @return void
 **/
    void saveAliPayStatus(PayStatusDto payStatusDto);

}
