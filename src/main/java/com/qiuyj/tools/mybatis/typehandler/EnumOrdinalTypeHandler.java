package com.qiuyj.tools.mybatis.typehandler;

import com.qiuyj.tools.EnumUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/14
 */
public class EnumOrdinalTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
  private final Class<E> type;

  public EnumOrdinalTypeHandler(Class<E> type) {
    this.type = Objects.requireNonNull(type, "Enum's type cannot be null");
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
    if (Objects.isNull(jdbcType))
      ps.setObject(i, parameter.ordinal());
    else
      ps.setObject(i, parameter.ordinal(), jdbcType.TYPE_CODE);
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object ordinal = rs.getObject(columnName);
    return Objects.isNull(ordinal) ? null : EnumUtils.getByOridnal(type, (int) ordinal);
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object ordinal = rs.getObject(columnIndex);
    return Objects.isNull(ordinal) ? null : EnumUtils.getByOridnal(type, (int) ordinal);
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object ordinal = cs.getObject(columnIndex);
    return Objects.isNull(ordinal) ? null : EnumUtils.getByOridnal(type, (int) ordinal);
  }
}