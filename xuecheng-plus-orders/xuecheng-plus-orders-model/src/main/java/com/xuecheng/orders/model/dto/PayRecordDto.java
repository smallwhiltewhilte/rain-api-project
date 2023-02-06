package com.xuecheng.orders.model.dto;

import com.xuecheng.orders.model.po.XcPayRecord;
import lombok.Data;
import lombok.ToString;


/**
 * @description 支付记录dto
 * @author wangzan
 * @date 2023/2/1
 * @version 1.0
 */
@Data
@ToString
public class PayRecordDto extends XcPayRecord {

    //二维码
    private String qrcode;

}
