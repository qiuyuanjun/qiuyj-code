package com.qiuyj.mybatis.sqlbuild.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于batchDelete方法的TypeHandler
 * @author qiuyj
 * @since 2018/4/6
 */
public class BatchDeleteTypeHandler extends BaseTypeHandler<Object[]> {

  private static final BatchDeleteTypeHandler INSTANCE = new BatchDeleteTypeHandler();

  public static BatchDeleteTypeHandler getInstance() {
    return INSTANCE;
  }

  /**
   * 用于参数是数组的设置值，如果参数是数组，而需要预处理的值都在数组里面
   * 那么就需要用到当前这个handler
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object[] parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter[i - 1]);
  }

  /**
   * 此方法没有用到
   */
  @Override
  public Object[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    throw new SQLException("Never get here.");
  }

  /**
   * 此方法没有用到
   */
  @Override
  public Object[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    throw new SQLException("Never get here.");
  }

  /**
   * 此方法没有用到
   */
  @Override
  public Object[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    throw new SQLException("Never get here.");
  }
}