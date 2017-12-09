package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.mybatis.SqlInfo;

import java.lang.reflect.Field;

/**
 * @author qiuyj
 * @since 2017/12/9
 */
public class FieldCountConditionChecker implements ConditionChecker {

  @Override
  public int doCheck(Field field, SqlInfo sqlInfo) {
    sqlInfo.fieldCountIncrement();
    return ConditionChecker.CONTINUE_EXECUTION;
  }
}