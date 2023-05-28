//package com.rain.rainapigateway.feignclient;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//
//
//@FeignClient(value = "rain-api-backend")
//public interface UserInterfaceInfoClient {
//    @GetMapping("/api/userInterfaceInfo/haveInvokeCount")
//    boolean haveInvokeCount(@RequestParam("interfaceInfoId")long interfaceInfoId,@RequestParam("userId") long userId);
////    @GetMapping("/api/userInterfaceInfo/test")
////    Integer get();
//}
