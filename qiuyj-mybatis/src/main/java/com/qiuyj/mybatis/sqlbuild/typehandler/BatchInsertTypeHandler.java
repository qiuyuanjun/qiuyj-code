package com.qiuyj.mybatis.sqlbuild.typehandler;

import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author qiuyj
 * @since 2018/4/6
 */
public class BatchInsertTypeHandler extends BaseTypeHandler<List> {

  private final SqlInfo sqlInfo;

  public BatchInsertTypeHandler(SqlInfo sqlInfo) {
    this.sqlInfo = sqlInfo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
    // 首先判断当前占位符是属于List集合的第几个元素
    int idx = i - 1;
    int instanceIdx = idx / sqlInfo.getFieldCount();
    // 得到当前设置的值是实体类里面属性的第几个位置
    int parameterIdx = idx % sqlInfo.getFieldCount();
    // 得到当前设置的实体类
    Object obj = parameter.get(instanceIdx);
    // 得到对应实体类的反射对象（MetaObject）方便后面获取值
    MetaObject objMeta = sqlInfo.getConfiguration().newMetaObject(obj);
    PropertyColumnMapping pcm = sqlInfo.getPropertyColumnMappings().get(parameterIdx);
    Object value = objMeta.getValue(pcm.getJavaClassPropertyName());
    // 如果是枚举类型，那么需要使用枚举类型的typeHandler设置值
    /*
     * 由于构建这个框架的mysql驱动版本较高，直接使用的是java8构建的
     * 所以mysql驱动原生支持java8的时间日期api的setObject方法参数
     * 但是考虑到有些人的mysql驱动不会这么高，所以这里也一样
     * 对于java8的时间日期的值显示调用对应的TypeHandler处理
     */
    if (pcm.getTypeHandler() instanceof EnumOrdinalTypeHandler
        || pcm.getTypeHandler() instanceof EnumTypeHandler
        || isJava8DateTimeApi(value)) {
      pcm.getTypeHandler().setParameter(ps, i, value, pcm.getJdbcType());
    }
    // 否则直接setObject即可
    else {
      ps.setObject(i, value);
    }
  }

  /**
   * 判断给定的参数是否是java8的时间日期api
   */
  private boolean isJava8DateTimeApi(Object value) {
    return TemporalAccessor.class.isInstance(value);
  }

  @Override
  public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
    throw new SQLException("Never get here.");
  }

  @Override
  public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    throw new SQLException("Never get here.");
  }

  @Override
  public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    throw new SQLException("Never get here.");
  }
}