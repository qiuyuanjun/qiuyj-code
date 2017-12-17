package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ParameterResolver;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class MysqlBatchInsertParameterObjectResolver implements CustomizedParameterObjectResolver {
  private static MysqlBatchInsertParameterObjectResolver INSTANCE = new MysqlBatchInsertParameterObjectResolver();
  private static volatile SqlInfo sqlInfo;
  private static volatile Configuration config;

  private final MysqlBatchInsertTypeHandler batchInsertTypeHandler;

  private MysqlBatchInsertParameterObjectResolver() {
    batchInsertTypeHandler = new MysqlBatchInsertTypeHandler();
  }

  public static MysqlBatchInsertParameterObjectResolver getInstance() {
    return INSTANCE;
  }

  @Override
  public List<ParameterMapping> resolveParameterObject(Configuration config, SqlInfo sqlInfo, Object paramObj, SqlNode sqlNode) {
    List list = (List) ParameterResolver.resolveParameter(paramObj).getParameterValues()[0];
    if (list.isEmpty())
      throw new IllegalArgumentException("Method batchInsert parameter can not be an empty list");
    else {
      this.sqlInfo = sqlInfo;
      this.config = config;
      StringJoiner grammaJoiner = new StringJoiner(",");
      StringJoiner valueJoiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> singleInstanceParameterMapping = new ArrayList<>(sqlInfo.getFieldCount());
      ParameterMapping canonic = new ParameterMapping.Builder(
          config,
          "list",
          batchInsertTypeHandler
      ).build();
      for (PropertyColumnMapping propertyColumnMapping : sqlInfo.getPropertyColumnMappings()) {
        valueJoiner.add(SqlProvider.PREPARE_FLAG);
        singleInstanceParameterMapping.add(canonic);
      }
      List<ParameterMapping> allParameterMappings = new ArrayList<>(sqlInfo.getFieldCount() * list.size());
      String value = valueJoiner.toString();
      for (int i = 0; i < list.size(); i++) {
        grammaJoiner.add(value);
        allParameterMappings.addAll(singleInstanceParameterMapping);
      }
      // 重新设置StaticTextSqlNode的text属性的值
      resetStaticSqlNode((StaticTextSqlNode) sqlNode, grammaJoiner.toString());
      return allParameterMappings;
    }
  }

  private boolean isJsr330DateTimeApi(Object value) {
    return TemporalAccessor.class.isInstance(value);
  }

  private static final class MysqlBatchInsertTypeHandler extends BaseTypeHandler<List> {

    @Override
    @SuppressWarnings("unchecked")
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
      // 首先判断当前占位符是属于List集合的第几个元素
      int idx = i - 1;
      int instanceIdx = idx / MysqlBatchInsertParameterObjectResolver.sqlInfo.getFieldCount();
      // 得到当前设置的值是实体类里面属性的第几个位置
      int parameterIdx = idx % MysqlBatchInsertParameterObjectResolver.sqlInfo.getFieldCount();
      // 得到当前设置的实体类
      Object obj = parameter.get(instanceIdx);
      // 得到对应实体类的反射对象（MetaObject）方便后面获取值
      MetaObject objMeta = MysqlBatchInsertParameterObjectResolver.config.newMetaObject(obj);
      PropertyColumnMapping pcm = MysqlBatchInsertParameterObjectResolver.sqlInfo.getPropertyColumnMappings().get(parameterIdx);
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
            || MysqlBatchInsertParameterObjectResolver.getInstance().isJsr330DateTimeApi(value))
        pcm.getTypeHandler().setParameter(ps, i, value, pcm.getJdbcType());
      // 否则直接setObject即可
      else
        ps.setObject(i, value);
    }

    /**
     * 该方法没有作用
     */
    @Override
    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
      return null;
    }

    /**
     * 该方法没有作用
     */
    @Override
    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      return null;
    }

    /**
     * 该方法没有作用
     */
    @Override
    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      return null;
    }
  }
}