package com.qiuyj.mybatis.engine;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.mybatis.MapperMethodResolver;
import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.ReturnValueWrapper;
import com.qiuyj.mybatis.sqlbuild.SqlProvider;
import com.qiuyj.mybatis.checker.CheckerChain;
import com.qiuyj.mybatis.mapper.Mapper;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public abstract class AbstractSqlGeneratorEngine implements SqlGeneratorEngine {

  private final Object sqlInfoWriteLock = new Object();
  private Map<Class<? extends Mapper>, SqlInfo> sqlInfos;

  private final Object resultMapWriteLock = new Object();
  private final Map<Class<? extends Mapper>, ResultMap> resultMaps;

  private final CheckerChain chain;

  private final SqlProvider baseSqlProvider;

  private final MapperMethodResolver resolver;

  protected AbstractSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider, MapperMethodResolver resolver) {
    this.chain = chain;
    baseSqlProvider = sqlProvider;
    this.resolver = resolver;
    resultMaps = new HashMap<>();
  }

  @Override
  public void analysis(Class<? extends Mapper<?, ?>> actualMapperClass, Configuration configuration) {
    if (Objects.isNull(sqlInfos)) {
      sqlInfos = new HashMap<>();
    }

    if (!sqlInfos.containsKey(actualMapperClass)) {
      synchronized (sqlInfoWriteLock) {
        if (!sqlInfos.containsKey(actualMapperClass)) {
          /*
           * 这里生成SqlInfo会非常的耗时
           * 所以这里一定要将结果缓存起来
           */
          SqlInfo mapperSqlInfo = new SqlInfo(actualMapperClass, chain, configuration);
          sqlInfos.put(actualMapperClass, mapperSqlInfo);
        }
      }
    }
    /*
     * 如果当前解析的mapperClass所对应的实体类有枚举类型
     * 那么就解析所有属性生成对应的ResultMap
     */
    SqlInfo currentSqlInfo = getSqlInfo(actualMapperClass);

    // 这里判断下，sqlinfo里面的Configuration是否为null，如果为null，那么需要设置
    if (Objects.isNull(currentSqlInfo.getConfiguration())) {
      currentSqlInfo.setConfiguration(configuration);
      // 此时需要设置sqlInfo里面的
      if (Objects.isNull(currentSqlInfo.getPrimaryKey().getTypeHandler())) {
        currentSqlInfo.getPrimaryKey().setTypeHandler(configuration.getTypeHandlerRegistry().getTypeHandler(currentSqlInfo.getPrimaryKey().getTargetClass()));
      }
      for (PropertyColumnMapping pcm : currentSqlInfo.getPropertyColumnMappings()) {
        if (Objects.isNull(pcm.getTypeHandler())) {
          pcm.setTypeHandler(configuration.getTypeHandlerRegistry().getTypeHandler(pcm.getTargetClass()));
        }
      }
    }

    if (currentSqlInfo.hasEnumField() && !resultMaps.containsKey(actualMapperClass)) {
      synchronized (resultMapWriteLock) {
        if (!resultMaps.containsKey(actualMapperClass)) {
          initResultMap(currentSqlInfo, actualMapperClass);
        }
      }
    }
  }


  private void initResultMap(SqlInfo sqlInfo, Class<? extends Mapper<?, ?>> mapperClass) {
    List<ResultMapping> resultMappings = new ArrayList<>(sqlInfo.getFieldCount());
    for (PropertyColumnMapping pcm : sqlInfo.getWithoutPrimaryKey()) {
      resultMappings.add(new ResultMapping.Builder(
          sqlInfo.getConfiguration(),
          pcm.getJavaClassPropertyName(),
          pcm.getJavaClassPropertyName(),
          pcm.getTypeHandler()
      ).build());
    }
    resultMappings.add(0, new ResultMapping.Builder(
        sqlInfo.getConfiguration(),
        sqlInfo.getPrimaryKey().getJavaClassPropertyName(),
        sqlInfo.getPrimaryKey().getJavaClassPropertyName(),
        sqlInfo.getPrimaryKey().getTypeHandler()
    ).flags(Collections.singletonList(ResultFlag.ID)).build());
    resultMaps.put(mapperClass, new ResultMap.Builder(
        sqlInfo.getConfiguration(),
        "mapper-" + mapperClass.getName(),
        sqlInfo.getBeanType(),
        resultMappings
    ).build());
  }

  /**
   * 该方法无需同步处理，因为当执行该方法的时候，执行顺序可以保证缓存中一定有SqlInfo
   */
  private SqlInfo getSqlInfo(Class<? extends Mapper<?, ?>> mapperClass) {
    return sqlInfos.get(mapperClass);
  }

  /**
   * 该方法无需同步处理，因为当执行该方法的时候，执行顺序可以保证缓存中一定有ResultMap
   */
  private List<ResultMap> getResultMap(Class<? extends Mapper<?, ?>> mapperClass) {
    return Collections.singletonList(resultMaps.get(mapperClass));
  }

  @Override
  public void generateSql(MappedStatement ms, Class<? extends Mapper<?, ?>> mapperClass, Method mapperMethod, Object args) {
    // 得到当前实体类的映射数据库的信息
    SqlInfo sqlInfo = getSqlInfo(mapperClass);
    // 得到返回值
    ReturnValueWrapper returnValue = getReturnValue(sqlInfo, ms, mapperMethod, args);
    if (Objects.nonNull(returnValue)) {
      // 处理自定义生成ParameterMapping，如果没有自定义，那么该方法犹如一个空方法
      returnValue.customizedResolveParameterObject(sqlInfo, args, ms.getConfiguration());
      MetaObject msMetaObject = ms.getConfiguration().newMetaObject(ms);
      // 重新设置sqlSource
      msMetaObject.setValue("sqlSource", returnValue.generateSqlSource(ms.getConfiguration()));
      // 如果类型是Select的，那么还需要设置resultMap
      if (sqlInfo.hasEnumField() && ms.getSqlCommandType() == SqlCommandType.SELECT) {
        msMetaObject.setValue("resultMaps", getResultMap(mapperClass));
      }
      // 如果类型是Insert，那么有可能需要设置KeyGenerator
      // 当然，前提是当前的主键标注了@Sequence注解
      else if (Objects.nonNull(sqlInfo.getSequenceName()) && ms.getSqlCommandType() == SqlCommandType.INSERT) {
        generateSequenceKey(ms, msMetaObject, sqlInfo);
      }
    }
  }

  /**
   * 处理数据库主键不是自增的情况
   */
  protected abstract void generateSequenceKey(MappedStatement ms, MetaObject msMetaObject, SqlInfo sqlInfo);

  /**
   * 根据不同的情况得到对应的SqlNode
   */
  private ReturnValueWrapper getReturnValue(SqlInfo sqlInfo, MappedStatement ms, Method mapperMethod, Object args) {
    ReturnValueWrapper returnValue = null;
    if (resolver.isExampleMethod(mapperMethod)) {
      // 这里需要解析参数
      Method reflectionMethod = ReflectionUtils.getDeclaredMethod(baseSqlProvider.getClass(), mapperMethod.getName(), ms.getClass(), SqlInfo.class, Object.class);
      returnValue = (ReturnValueWrapper) ReflectionUtils.invokeMethod(baseSqlProvider, reflectionMethod, ms, sqlInfo, args);
    }
    else {
      // 这里需要注意，有些mapper方法只需要生成一次即可，不用每次都生成
      // 需要每次都生成sql的mapper方法是那些参数带了@Example注解的方法
      // 所以这里需要分开讨论，判断mapper方法是否是第一次调用
      // 如果参数是数组或者集合类型，那么依然还需要重新生成sqlSource
//      if (Mapper.DEFAULT_MAPPER_SQL.equals(ms.getSqlSource().getBoundSql(args).getSql())) {
      // 如果还是ProviderSqlSource，那么需要重新生成Sql，这么判断比上面那种方式更好，效率也更快
      if (ms.getSqlSource().getClass() == ProviderSqlSource.class || parameterObjectIsArrayOrCollection(args)) {
        // 这里需要重新生成sqlNode
        Method reflectionMethod = ReflectionUtils.getDeclaredMethod(baseSqlProvider.getClass(), mapperMethod.getName(), ms.getClass(), SqlInfo.class);
        returnValue = (ReturnValueWrapper) ReflectionUtils.invokeMethod(baseSqlProvider, reflectionMethod, ms, sqlInfo);
      }
    }
    return returnValue;
  }

  private boolean parameterObjectIsArrayOrCollection(Object paramObj) {
    return paramObj instanceof DefaultSqlSession.StrictMap;
  }

  public void addSqlInfo(Class<? extends Mapper> mapper, SqlInfo sqlInfo) {
    if (Objects.nonNull(mapper)) {
      if (Objects.isNull(sqlInfo)) {
        sqlInfos.remove(mapper);
      }
      else {
        sqlInfos.putIfAbsent(mapper, sqlInfo);
      }
    }
  }

  public void addSqlInfos(Set<Class<? extends Mapper<?, ?>>> mapperClassSet) {
    if (Objects.nonNull(mapperClassSet) && mapperClassSet.size() > 0) {
      if (Objects.isNull(sqlInfos)) {
        sqlInfos = new HashMap<>(mapperClassSet.size());
      }
      for (Class<? extends Mapper<?, ?>> mapperClass : mapperClassSet) {
        // 由于此时还无法获取到Mybatis的Configuration对象，所以这里暂时设置null
        // 后面运行阶段，一定要重新设置configuraiton为null的sqlInfo
        SqlInfo currSqlInfo = new SqlInfo(mapperClass, chain, null);
        addSqlInfo(mapperClass, currSqlInfo);
        // 这里暂时无法获取resultMap，由于configuration为null
        // 所以获取对应的resultMap将放在运行的时候动态获取
//        if (currSqlInfo.hasEnumField()) {
//          initResultMap(currSqlInfo, mapperClass);
//        }
      }
    }
  }
}