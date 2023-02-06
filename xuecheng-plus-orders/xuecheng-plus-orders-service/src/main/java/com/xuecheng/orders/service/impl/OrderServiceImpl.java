package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.xuecheng.orders.config.PayNotifyConfig.MESSAGE_TYPE;

/**
 * @author wangzan
 * @version 1.0
 * @description 订单服务service实现类
 * @date 2023/2/2
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    XcOrdersMapper ordersMapper;
    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;
    @Autowired
    OrderServiceImpl currentProxy;
    @Autowired
    XcPayRecordMapper payRecordMapper;
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Autowired
    MqMessageService mqMessageService;
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        // 创建商品订单
        XcOrders orders = currentProxy.saveXcOrders(userId, addOrderDto);
        // 生成支付记录
        XcPayRecord payRecord = createPayRecord(orders);
        // 生成二维码
        String qrCode = null;
        try {
            qrCode = new QRCodeUtil().createQRCode("http://192.168.85.1/api/orders/requestpay?payNo=" + payRecord.getPayNo(), 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码出错");
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;

    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        LambdaQueryWrapper<XcPayRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcPayRecord::getPayNo, payNo);
        return payRecordMapper.selectOne(lambdaQueryWrapper);
    }

    @Transactional
    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        String tradeStatus = payStatusDto.getTrade_status();
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            // 支付成功去更新订单状态
            // 拿到支付记录交易号
            String payNo = payStatusDto.getOut_trade_no();
            XcPayRecord payRecord = getPayRecordByPayNo(payNo);
            if (payRecord == null) {
                log.info("收到支付结果通知查询不到支付记录，收到的信息:{}", payStatusDto);
                return;
            }
            String status = payRecord.getStatus();
            if ("601002".equals(status)) {
                log.info("收到支付结果通知，支付状态已经为支付成功，不进行操作");
                return;
            }
            // 校验
            String app_id_alipay = payStatusDto.getApp_id();
            int totalPriceLocal = (int) (payRecord.getTotalPrice() * 100);
            int totalPriceAlipay = (int) (Float.parseFloat(payStatusDto.getTotal_amount()) * 100);
            if (totalPriceLocal != totalPriceAlipay || !app_id_alipay.equals(APP_ID)) {
                log.info("收到支付结果通知，校验失败，支付宝参数appid:{}，totalPrice:{}，本地参数appid:{}，totalPrice:{}", app_id_alipay, totalPriceAlipay, APP_ID, totalPriceLocal);
                return;
            }
            // 更新支付记录
            XcPayRecord updatePayRecord = new XcPayRecord();
            updatePayRecord.setStatus("601002");
            updatePayRecord.setOutPayNo(payStatusDto.getOut_trade_no());
            updatePayRecord.setOutPayChannel("603002");
            updatePayRecord.setPaySuccessTime(LocalDateTime.now());
            LambdaQueryWrapper<XcPayRecord> lambdaQueryWrapper = new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo);
            int update = payRecordMapper.update(updatePayRecord, lambdaQueryWrapper);
            if (update > 0) {
                log.info("收到支付宝支付结果通知，更新支付记录表成功:{}", payStatusDto);
            } else {
                log.info("收到支付宝支付结果通知，更新支付记录表失败:{}", payStatusDto);
            }
            // 获取订单
            Long orderId = payRecord.getOrderId();
            XcOrders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.info("收到支付宝支付结果通知，查不到对应订单，支付宝传来的参数:{}，订单号:{}", payStatusDto, orderId);
                return;
            }
            // 更新订单状态
            XcOrders ordersUpdate = new XcOrders();
            ordersUpdate.setStatus("600002");
            LambdaQueryWrapper<XcOrders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(XcOrders::getId, orderId);
            int update1 = ordersMapper.update(ordersUpdate, queryWrapper);
            if (update1 > 0) {
                log.info("收到支付宝支付结果通知，更新订单表成功，支付宝传来的参数:{}，订单号:{}", payStatusDto, orderId);
                // 找到订单表所关联的外部业务的主键
                String outBusinessId = orders.getOutBusinessId();
                // 向消息表插入记录
                mqMessageService.addMessage(MESSAGE_TYPE,outBusinessId,orders.getOrderType(),null);
            } else {
                log.info("收到支付宝支付结果通知，更新订单表失败，支付宝传来的参数:{}，订单号:{}", payStatusDto, orderId);

            }
        }
    }

    public void saveWxPayStatus(PayStatusDto payStatusDto) {
        // 支付渠道编号60302
        // 根据支付记录交易号查询支付记录
        // 从支付记录中拿到订单号，查询订单
        //更新订单状态
    }

    // 添加支付记录
    public XcPayRecord createPayRecord(XcOrders order) {
        XcPayRecord payRecord = new XcPayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        // 记录关联的订单id
        payRecord.setOrderId(order.getId());
        payRecord.setOrderName(order.getOrderName());
        payRecord.setTotalPrice(order.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        //未支付
        payRecord.setStatus("601001");
        payRecord.setUserId(order.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;

    }

    @Transactional
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {
        // 对创建订单进行幂等性判断
        // 选课记录id
        String outBusinessId = addOrderDto.getOutBusinessId();
        LambdaQueryWrapper<XcOrders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcOrders::getOutBusinessId, outBusinessId);
        XcOrders order = ordersMapper.selectOne(lambdaQueryWrapper);
        if (order != null) {
            return order;
        }
        // 添加订单
        order = new XcOrders();
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setTotalPrice(addOrderDto.getTotalPrice());
        order.setStatus("600001");
        order.setCreateDate(LocalDateTime.now());
        order.setUserId(userId);
        order.setOrderType(addOrderDto.getOrderType());
        order.setOrderName(addOrderDto.getOrderName());
        order.setOrderDetail(addOrderDto.getOrderDetail());
        order.setOrderDescrip(addOrderDto.getOrderDescrip());
        order.setOutBusinessId(addOrderDto.getOutBusinessId());
        ordersMapper.insert(order);
        // 插入订单明细表
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        // 将明细List插入数据库
        xcOrdersGoods.forEach(ordersGoods -> {
            ordersGoods.setOrderId(orderId);
            ordersGoodsMapper.insert(ordersGoods);
        });
        return order;
    }

}
