package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.MapperMethodResolver;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ReturnValueWrapper;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.mapper.Mapper;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
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
public abstract class AbstractSqlGeneratorEngine implements SqlGeneratorEngine {
  private final Object sqlInfoLock = new Object();
  private final Map<Class<? extends Mapper>, SqlInfo> sqlInfos = new HashMap<>();
  private final CheckerChain chain;
  private final SqlProvider baseSqlProvider;
  private final MapperMethodResolver resolver;

  protected AbstractSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider, MapperMethodResolver resolver) {
    this.chain = chain;
    baseSqlProvider = sqlProvider;
    this.resolver = resolver;
  }

  @Override
  public void analysis(Class<? extends Mapper<?, ?>> actualMapperClass) {
    if (!sqlInfos.containsKey(actualMapperClass)) {
      synchronized (sqlInfoLock) {
        if (!sqlInfos.containsKey(actualMapperClass)) {
          SqlInfo mapperSqlInfo = new SqlInfo(actualMapperClass, chain);
          sqlInfos.put(actualMapperClass, mapperSqlInfo);
        }
      }
    }
  }

  /**
   * 该方法无需同步处理，因为当执行该方法的时候，执行顺序可以保证缓存中一定有SqlInfo
   */
  private SqlInfo getSqlInfo(Class<? extends Mapper<?, ?>> mapperClass) {
    return sqlInfos.get(mapperClass);
  }

  @Override
  public void generateSql(MappedStatement ms, Class<? extends Mapper<?, ?>> mapperClass, Method mapperMethod, Object args) {
    // 得到当前实体类的映射数据库的信息
    SqlInfo sqlInfo = getSqlInfo(mapperClass);
    // 得到返回值
    ReturnValueWrapper returnValue = getReturnValue(sqlInfo, ms, mapperClass, mapperMethod, args);
    if (Objects.nonNull(returnValue)) {
      // 健壮性检查
      if (returnValue.needParseParameter())
        returnValue.parseParameterMappings(sqlInfo, args);
      MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
      // 重新设置sqlSource
      msMetaObject.setValue("sqlSource", returnValue.generateSqlSource(ms.getConfiguration()));
    }
  }

  /**
   * 根据不同的情况得到对应的SqlNode
   */
  private ReturnValueWrapper getReturnValue(SqlInfo sqlInfo, MappedStatement ms, Class<? extends Mapper<?, ?>> mapperClass, Method mapperMethod, Object args) {
    ReturnValueWrapper returnValue = null;
    if (resolver.isExampleMethod(mapperMethod)) {
      // 这里需要解析参数
      Method reflectionMethod = ReflectionUtils.getDeclaredMethod(baseSqlProvider.getClass(), mapperMethod.getName(), ms.getClass(), SqlInfo.class, Object.class);
      returnValue = (ReturnValueWrapper) ReflectionUtils.invokeMethod(baseSqlProvider, reflectionMethod, ms, sqlInfo, args);
    } else {
      // 这里需要注意，有些mapper方法只需要生成一次即可，不用每次都生成
      // 需要每次都生成sql的mapper方法是那些参数带了@Example注解的方法
      // 所以这里需要分开讨论，判断mapper方法是否是第一次调用
//      if (Mapper.DEFAULT_MAPPER_SQL.equals(ms.getSqlSource().getBoundSql(args).getSql())) {
      // 如果还是ProviderSqlSource，那么需要重新生成Sql，这么判断比上面那种方式更好，效率也更快
      if (ms.getSqlSource().getClass() == ProviderSqlSource.class) {
        // 这里需要重新生成sqlNode
        Method reflectionMethod = ReflectionUtils.getDeclaredMethod(baseSqlProvider.getClass(), mapperMethod.getName(), ms.getClass(), SqlInfo.class);
        returnValue = (ReturnValueWrapper) ReflectionUtils.invokeMethod(baseSqlProvider, reflectionMethod, ms, sqlInfo);
      }
    }
    return returnValue;
  }

}