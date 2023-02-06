package com.xuecheng.base.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
/**
 * @author wangzan
 * @version 1.0
 * @description 分页返回结果
 * @date 2023/1/14
 */

 @Data
 @ToString
 @NoArgsConstructor
public class PageResult<T> {
  // 数据列表
  private List<T> items;

  //总记录数
  private long counts;

  //当前页码
  private long page;

  //每页记录数
  private long pageSize;

  public PageResult(List<T> items, long counts, long page, long pageSize) {
   this.items = items;
   this.counts = counts;
   this.page = page;
   this.pageSize = pageSize;
  }

 }
