package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public abstract class AbstractSqlGeneratorEngine implements SqlGeneratorEngine {
  private final Object sqlInfoLock = new Object();
  private final Map<Class<? extends Mapper>, SqlInfo> sqlInfos = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public void analysis(Method mapperMethod) {
    Class<? extends Mapper> actualMapperClass = (Class<? extends Mapper>) mapperMethod.getDeclaringClass();
    if (!sqlInfos.containsKey(actualMapperClass)) {
      synchronized (sqlInfoLock) {
        if (!sqlInfos.containsKey(actualMapperClass)) {
          SqlInfo mapperSqlInfo = new SqlInfo(actualMapperClass);
          sqlInfos.put(actualMapperClass, mapperSqlInfo);
        }
      }
    }
  }
}