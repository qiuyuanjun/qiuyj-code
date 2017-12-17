package com.qiuyj.tools.mybatis.key;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ParameterResolver;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    if (executeBefore)
      processSequenceQueryBeforeMainExecutor(executor, ms.getConfiguration(), parameter);
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
    if (!executeBefore)
      processSequenceQuery(ms.getConfiguration(), stmt, parameter);
  }

  private void processSequenceQuery(Configuration configuration, Statement stmt, Object parameter) {
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery(sequenceQuerySql);
      if (rs.next()) {
        Object sequence = sqlInfo.getPrimaryKey().getTypeHandler().getResult(rs, 1);
        Object parameterObject = ParameterResolver.resolveParameter(parameter).getParameterValues()[0];
        configuration.newMetaObject(parameterObject).setValue(sqlInfo.getPrimaryKey().getJavaClassPropertyName(), sequence);
      }
    } catch (SQLException e) {
      throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
    } finally {
      if (Objects.nonNull(rs)) {
        try {
          rs.close();
        } catch (SQLException e) {
          // ignore
        }
      }
    }
  }

}
