package com.qiuyj.mybatis.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;

import java.util.List;

/**
 * 批量操作的接口
 * @author qiuyj
 * @since 2017/11/11
 */
public interface BatchMapper<ID, T> {

  /**
   * 批量插入
   */
  @InsertProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  int batchInsert(List<T> insertionList);

  /*
   * 批量更新，暂时无法支持
   */
//  @UpdateProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
//  void batchUpdate(@Example List<T> updationList);

  /**
   * 批量删除
   */
  @DeleteProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  int batchDelete(ID[] ids);
}