package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.mybatis.config.Database;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public interface SqlGeneratorEngine {

  static SqlGeneratorEngine determineSqlGenerator(Database database) {
    switch (database) {
      case ORACLE:
        return new OracleSqlGeneratorEngine();
      case MYSQL:
      default:
        return new MySQLSqlGeneratorEngine();
    }
  }

  /**
   * 生成对应的sql
   * @param ms mappedStatement
   * @param method mapper方法
   * @param args 参数
   */
  void generateSql(MappedStatement ms, Method method, Object args);

  /**
   * 分析mapper方法
   * @param method mapper方法
   */
  void analysis(Method method);
}