package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.mybatis.SqlInfo;

import java.lang.reflect.Field;

/**
 * @author qiuyj
 * @since 2017/12/9
 */
public class FieldCountConditionChecker implements ConditionChecker {

  @Override
  public ReturnValue doCheck(Field field, SqlInfo sqlInfo, ReturnValue preRv) {
    sqlInfo.fieldCountIncrement();
    preRv.intValue = ConditionChecker.CONTINUE_EXECUTION;
    return preRv;
  }
}