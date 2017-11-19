package com.qiuyj.tools.mybatis.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 批量操作的接口
 * @author qiuyj
 * @since 2017/11/11
 */
public interface BatchMapper<T> {

  /**
   * 批量插入
   */
  @InsertProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  void batchInsert(List<T> insertionList);

  /**
   * 批量更新
   */
  @UpdateProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  void batchUpdate(List<T> updationList);
}