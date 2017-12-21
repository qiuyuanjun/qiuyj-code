package com.qiuyj.mybatis.sqlbuild.customer;

import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.ParameterResolver;
import com.qiuyj.mybatis.sqlbuild.SqlProvider;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2017/12/10
 */
public class BatchDeleteParameterObjectResolver implements CustomizedParameterObjectResolver {
  private static final BatchDeleteParameterObjectResolver INSTANCE = new BatchDeleteParameterObjectResolver();
  private final BatchDeleteTypeHandler batchDeleteTypeHandler;

  private BatchDeleteParameterObjectResolver() {
    batchDeleteTypeHandler = new BatchDeleteTypeHandler();
  }

  public static BatchDeleteParameterObjectResolver getInstance() {
    return INSTANCE;
  }

  @Override
  public List<ParameterMapping> resolveParameterObject(Configuration config, SqlInfo sqlInfo, Object paramObj, SqlNode sqlNode) {
    Object paramValue = ParameterResolver.resolveParameter(paramObj).getParameterValues()[0];
    int len = Array.getLength(paramValue);
    if (len == 0) {
      throw new IllegalArgumentException("Method batchDelete() parameter can not be an empty array");
    }
    else {
      List<ParameterMapping> parameterMappings = new ArrayList<>(len);
      StringJoiner joiner = new StringJoiner(",", "(", ")");
      ParameterMapping canonic = new ParameterMapping.Builder(
          config,
          "array",
          batchDeleteTypeHandler
      ).build();
      for (int i = 0; i < len; i++) {
        parameterMappings.add(canonic);
        joiner.add(SqlProvider.PREPARE_FLAG);
      }
      resetStaticSqlNode((StaticTextSqlNode) sqlNode, joiner.toString());
      return parameterMappings;
    }
  }

  private static final class BatchDeleteTypeHandler extends BaseTypeHandler<Object[]> {

    /**
     * 用于参数是数组的设置值，如果参数是数组，而需要预处理的值都在数组里面
     * 那么就需要用到当前这个handler
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object[] parameter, JdbcType jdbcType) throws SQLException {
      ps.setObject(i, parameter[i - 1]);
    }

    /**
     * 此方法没有作用
     */
    @Override
    public Object[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
      return new Object[0];
    }

    /**
     * 此方法没有作用
     */
    @Override
    public Object[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      return new Object[0];
    }

    /**
     * 此方法没有作用
     */
    @Override
    public Object[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      return new Object[0];
    }
  }

}