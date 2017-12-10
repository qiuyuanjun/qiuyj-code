package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ParameterResolver;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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

  @Override
  public List<ParameterMapping> resolveParameterObject(Configuration config, SqlInfo sqlInfo, Object paramObj, SqlNode sqlNode) {
    Object paramValue = ParameterResolver.resolveParameter(paramObj).getParameterValues()[0];
    int len = Array.getLength(paramValue);
    List<ParameterMapping> parameterMappings = new ArrayList<>(len);
    StringJoiner joiner = new StringJoiner(",", "(", ")");
    for (int i = 0; i < len; i++) {
      parameterMappings.add(new ParameterMapping.Builder(
          config,
          "array",
          new BatchDeleteTypeHandler()
      ).build());
      joiner.add(SqlProvider.PREPARE_FLAG);
    }
    StaticTextSqlNode staticNode = (StaticTextSqlNode) sqlNode;
    // 最后通过反射修改SqlNode里面的sql
    Field textField = ReflectionUtils.getDeclaredField(staticNode.getClass(), "text");
    // 由于StaticTextSqlNode里面的text属性是final类型的，所以这里需要设置accessible
    textField.setAccessible(true);
    try {
      String origin = (String) textField.get(staticNode);
      textField.set(staticNode, origin + joiner.toString());
    } catch (IllegalAccessException e) {
      // ignore
    }
    return parameterMappings;
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