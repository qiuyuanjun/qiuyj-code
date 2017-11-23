package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.mapper.Mapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSqlGeneratorEngine implements SqlGeneratorEngine {
  private final Object sqlInfoLock = new Object();
  private final Object mappedStatementMetaObjectLock = new Object();
  private final Map<Class<? extends Mapper>, SqlInfo> sqlInfos = new HashMap<>();
  private final Map<String, MetaObject> mappedStatementMetaObjects = new HashMap<>();
  private final CheckerChain chain;
  private final SqlProvider baseSqlProvider;
  private final Class<? extends SqlProvider> baseSqlProviderClass;

  protected AbstractSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider) {
    this.chain = chain;
    baseSqlProvider = sqlProvider;
    baseSqlProviderClass = baseSqlProvider.getClass();
  }

  @Override
  public void analysis(Method mapperMethod) {
    Class<? extends Mapper> actualMapperClass = (Class<? extends Mapper>) mapperMethod.getDeclaringClass();
    if (!sqlInfos.containsKey(actualMapperClass)) {
      synchronized (sqlInfoLock) {
        if (!sqlInfos.containsKey(actualMapperClass)) {
          SqlInfo mapperSqlInfo = new SqlInfo(actualMapperClass, chain);
          sqlInfos.put(actualMapperClass, mapperSqlInfo);
        }
      }
    }
  }

  private SqlInfo getSqlInfo(Method mapperMethod) {
    Class<? extends Mapper> actualMapperClass = (Class<? extends Mapper>) mapperMethod.getDeclaringClass();
    return sqlInfos.get(actualMapperClass);
  }

  @Override
  public void generateSql(MappedStatement ms, Method mapperMethod, Object args) {
    MetaObject msMetaObject = getMetaObject(ms);
    // 首先得到对应的SqlNode
    Method sqlNodeMethod = ReflectionUtils.getDeclaredMethod(baseSqlProviderClass, mapperMethod.getName(), ms.getClass(), SqlInfo.class);
    SqlInfo sqlInfo = getSqlInfo(mapperMethod);

    // 重新设置sqlSource，即可生成sql语句
    msMetaObject.setValue("sqlSource", null);
  }

  private MetaObject getMetaObject(MappedStatement ms) {
    String msId = ms.getId();
    MetaObject msMetaObject = mappedStatementMetaObjects.get(msId);
    if (Objects.isNull(msMetaObject)) {
      synchronized (mappedStatementMetaObjectLock) {
        msMetaObject = mappedStatementMetaObjects.get(msId);
        if (Objects.isNull(msMetaObject)) {
          msMetaObject = ms.getConfiguration().newMetaObject(ms);
          mappedStatementMetaObjects.put(msId, msMetaObject);
        }
      }
    }
    return msMetaObject;
  }
}