package com.qiuyj.tools.mybatis.key;

/**
 * @author qiuyj
 * @since 2017/12/16
 */
public interface SequenceDialect {

  /**
   * 生成对应的Sequence序列查询的sql语句
   * @param sequenceName 序列名
   */
  String generateSequenceQueryString(String sequenceName);
}
