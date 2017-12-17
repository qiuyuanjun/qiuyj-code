package com.qiuyj.tools.mybatis.key;

/**
 * @author qiuyj
 * @since 2017/12/16
 */
public class OracleSequenceDialect implements SequenceDialect {

  @Override
  public String generateSequenceQueryString(String sequenceName) {
    return "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
  }
}
