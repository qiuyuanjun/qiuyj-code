package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.mybatis.config.SqlGeneratorConfig;
import com.qiuyj.tools.mybatis.engine.SqlGeneratorEngine;
import com.qiuyj.tools.mybatis.mapper.Mapper;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author qiuyj
 * @since 2017/11/11
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlGenerator implements Interceptor {
  private SqlGeneratorConfig config;
  private MapperMethodResolver resolver;
  private SqlGeneratorEngine engine;

  @Override
  @SuppressWarnings("unchecked")
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
    /*
     * 只有SqlSource是ProviderSqlSource并且当前执行的方式是mapper方法的时候，
     * 才会自动生成sql
     */
    if (ms.getSqlSource() instanceof ProviderSqlSource) {
      int lastDot = ms.getId().lastIndexOf(".");
      Class<? extends Mapper<?, ?>> mapperClass//
          = (Class<? extends Mapper<?, ?>>) ClassUtils.resolveClassName(ms.getId().substring(0, lastDot), Thread.currentThread().getContextClassLoader());
      String methodStr = ms.getId().substring(lastDot + 1);
      // 得到对应的mapper方法
      Method mapperMethod = resolver.getMapperDeclaredMethod(mapperClass, methodStr);
      if (resolver.isMapperMethod(mapperMethod)) {
        // 解析mapper接口，得到对应的sql信息
        engine.analysis(mapperClass);
        // 生成对应的sql
        engine.generateSql(ms, mapperClass, mapperMethod, invocation.getArgs()[1]);
      }
    }
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object obj) {
    // 生成Executor的代理对象
    return obj instanceof Executor ? Plugin.wrap(obj, this) : obj;
  }

  @Override
  public void setProperties(Properties properties) {
    // 初始化配置信息
    config = SqlGeneratorConfig.init(properties);
    // 得到所有的mapper方法名
    resolver = new MapperMethodResolver(config.getBaseMapperClass());
    // 得到对应的Sql生成引擎
    engine = SqlGeneratorEngine.determineSqlGenerator(config);
  }
}