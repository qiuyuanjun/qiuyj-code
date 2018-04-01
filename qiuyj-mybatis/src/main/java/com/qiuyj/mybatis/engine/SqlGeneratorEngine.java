package com.qiuyj.mybatis.engine;

import com.qiuyj.mybatis.mapper.Mapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public interface SqlGeneratorEngine {

  /**
   * 生成对应的sql
   * @param ms mappedStatement
   * @param mapperClass Mapper接口的Class对象
   * @param method mapper方法
   * @param args 参数
   */
  void generateSql(MappedStatement ms,
                   Class<? extends Mapper<?, ?>> mapperClass,
                   Method method,
                   Object args);

  /**
   * 分析mapper
   */
  void analysis(Class<? extends Mapper<?, ?>> actualMapperClass, Configuration configuration);
}