package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.mybatis.SqlInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 静态属性检查器，如果当前属性是静态类型，那么就不应该让当前属性接着执行其他的检查器了
 * @author qiuyj
 * @since 2017/11/20
 */
public class StaticConditionChecker implements ConditionChecker {

  @Override
  public int doCheck(Field field, SqlInfo sqlInfo) {
    return Modifier.isStatic(field.getModifiers()) ? -1 : 0;
  }
}
