package com.qiuyj.mybatis.key;

import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.ParameterResolver;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/16
 */
public class SequenceKeyGenerator implements KeyGenerator {
  private final String sequenceQuerySql;
  private final SqlInfo sqlInfo;
  private boolean executeBefore = true;

  public SequenceKeyGenerator(SqlInfo sqlInfo, String sequenceQuerySql) {
    this.sqlInfo = sqlInfo;
    this.sequenceQuerySql = sequenceQuerySql;
  }

  public SequenceKeyGenerator(SqlInfo sqlInfo, String sequenceQuerySql, boolean executeBefore) {
    this(sqlInfo, sequenceQuerySql);
    this.executeBefore = executeBefore;
  }

  @Override
  public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    if (executeBefore) {
      processSequenceQueryBeforeMainExecutor(executor, ms.getConfiguration(), parameter);
    }
  }

  /**
   * 由于processBefore方法的Statement是null，所以这里需要重新构建一个Statement对象
   */
  private void processSequenceQueryBeforeMainExecutor(Executor executor, Configuration configuration, Object parameter) {
    Statement stmt = null;
    try {
      Connection sequenceQueryConn = executor.getTransaction().getConnection();
      stmt = sequenceQueryConn.createStatement();
      processSequenceQuery(configuration, stmt, parameter);
    } catch (SQLException e) {
      throw new ExecutorException("Error obtaining database connection or creating statement. \nCaused by: " + e, e);
    } finally {
      if (Objects.nonNull(stmt)) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // ignore
        }
      }
    }
  }

  @Override
  public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    if (!executeBefore) {
      processSequenceQuery(ms.getConfiguration(), stmt, parameter);
    }
  }

  private void processSequenceQuery(Configuration configuration, Statement stmt, Object parameter) {
    // 这里只能处理一个参数，多个参数无法处理
    ParameterResolver.ParameterResolverResult parameterObjectResult = ParameterResolver.resolveParameter(parameter);
    Object parameterObject = parameterObjectResult.getParameterValues()[0];
    Class<?> parameterType = parameterObjectResult.getParameterTypes()[0];
    // 这里需要分情况讨论
    if (sqlInfo.getBeanType() == parameterType) {
      // 这种情况对应的参数是一个实体类，那么只需要查询一次序列值即可
      resolveBeanInstance(configuration, stmt, parameterObject);
    }
    else if (configuration.getObjectFactory().isCollection(parameterType)) {
      // 这种情况对应的参数是一个集合类型，那么可能需要查询多次序列值
      resolveCollection(configuration, stmt, (Collection<?>) parameterObject);
    }
    else if (parameterType.isArray()) {
      // 这种情况对应的参数是一个数组，那么可能需要查询多次序列值
      resolveArray(configuration, stmt, parameterObject);
    }
  }

  private void resolveArray(Configuration configuration, Statement stmt, Object parameterObject) {
    int len = Array.getLength(parameterObject);
    Object instance;
    ResultSet rs = null;
    try {
      for (int i = 0; i < len; i++) {
        instance = Array.get(parameterObject, i);
        rs = stmt.executeQuery(sequenceQuerySql);
        if (rs.next()) {
          Object sequence = sqlInfo.getPrimaryKey().getTypeHandler().getResult(rs, 1);
          configuration.newMetaObject(instance).setValue(sqlInfo.getPrimaryKey().getJavaClassPropertyName(), sequence);
        }
      }
    } catch (SQLException e) {
      throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
    } finally {
      closeResultSetQuietly(rs);
    }
  }

  private void resolveCollection(Configuration configuration, Statement stmt, Collection<?> parameterObject) {
    ResultSet rs = null;
    try {
      for (Object instance : parameterObject) {
        rs = stmt.executeQuery(sequenceQuerySql);
        if (rs.next()) {
          Object sequence = sqlInfo.getPrimaryKey().getTypeHandler().getResult(rs, 1);
          configuration.newMetaObject(instance).setValue(sqlInfo.getPrimaryKey().getJavaClassPropertyName(), sequence);
        }
      }
    } catch (SQLException e) {
      throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
    } finally {
      closeResultSetQuietly(rs);
    }
  }

  private void resolveBeanInstance(Configuration configuration, Statement stmt, Object parameterObject) {
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery(sequenceQuerySql);
      if (rs.next()) {
        Object sequence = sqlInfo.getPrimaryKey().getTypeHandler().getResult(rs, 1);
        configuration.newMetaObject(parameterObject).setValue(sqlInfo.getPrimaryKey().getJavaClassPropertyName(), sequence);
      }
    } catch (SQLException e) {
      throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
    } finally {
      closeResultSetQuietly(rs);
    }
  }

  private static void closeResultSetQuietly(ResultSet rs) {
    if (Objects.nonNull(rs)) {
      try {
        rs.close();
      } catch (SQLException e) {
        // ignore
      }
    }
  }
}