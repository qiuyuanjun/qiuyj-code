package com.qiuyj.mybatis.mapper;

import com.qiuyj.mybatis.SqlProvider;

/**
 * 通用Mapper接口
 * @author qiuyj
 * @since 2017/11/11
 */
@SqlProvider("com.qiuyj.mybatis.build.SqlProvider")
public interface Mapper<ID, T> extends CrudMapper<ID, T>, BatchMapper<ID, T> {

  String DEFAULT_MAPPER_SQL = SqlProvider.class.getName() + ".MAPPER";

  final class SqlProvider {

    /**
     * 该方法仅仅是为了满足mybatis的语法要求，实际没有意义
     * sql会通过框架自动生成
     */
    public String dynamicSql() {
      return Mapper.DEFAULT_MAPPER_SQL;
    }
  }
}