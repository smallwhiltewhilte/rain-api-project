package com.xuecheng.orders.config;
 /**
 * @description 支付宝配置参数
 * @author wangzan
 * @date 2023/2/1
 * @version 1.0
 */
 public class AlipayConfig {
  // 商户appid
//	public static final String APPID = "";
  // 私钥 pkcs8格式的
//	public static final String RSA_PRIVATE_KEY = "";
  // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  public static final String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
  // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
  public static final String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
  // 请求网关地址
  public static final String URL = "https://openapi.alipaydev.com/gateway.do";
  // 编码
  public static final String CHARSET = "utf-8";
  // 返回格式
  public static final String FORMAT = "json";
  // 支付宝公钥
//	public static String ALIPAY_PUBLIC_KEY = "";
  // 日志记录目录
  public static final String log_path = "/log";
  // RSA2
  public static final String SIGN_TYPE = "RSA2";
  public static final String SUCCESS = "TRADE_SUCCESS";
 }