package com.qiuyj.tools.mybatis.mapper;

import com.qiuyj.tools.mybatis.SqlProvider;

/**
 * 通用Mapper接口
 * @author qiuyj
 * @since 2017/11/11
 */
@SqlProvider("com.qiuyj.tools.mybatis.build.SqlProvider")
public interface Mapper<ID, T> extends CrudMapper<ID, T>, BatchMapper<T> {

  final class SqlProvider {

    /**
     * 该方法仅仅是为了满足mybatis的语法要求，实际没有意义
     * sql会通过框架自动生成
     */
    public String dynamicSql() {
      return "";
    }
  }
}