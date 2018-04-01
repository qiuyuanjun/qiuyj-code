package com.qiuyj.mybatis.sqlbuild;

import com.qiuyj.mybatis.BeanExampleResolver;
import com.qiuyj.mybatis.PropertyColumnMapping;
import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.customer.BatchDeleteParameterObjectResolver;
import com.qiuyj.mybatis.sqlbuild.dialect.MySqlDialect;
import com.qiuyj.mybatis.sqlbuild.dialect.OracleDialect;
import com.qiuyj.mybatis.sqlbuild.dialect.SqlDialect;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author qiuyj
 * @since 2017/11/21
 */
@Deprecated
public class SqlProvider {
  public static final String PREPARE_FLAG = "?";
  private final SqlDialect sqlDialect;

  /**
   * 这里传入一个Database的参数，那么可以通过这个Database判断当前数据库的类型
   * 同时如果用户继承了SqlProvider实现自己的SqlProvider，那么该参数也可以给用户判断数据库
   */
  public SqlProvider(String dbType) {
    switch (dbType.toUpperCase(Locale.ENGLISH)) {
      case "ORACLE":
        sqlDialect = new OracleDialect();
        break;
      case "MYSQL":
      default:
        sqlDialect = new MySqlDialect();
    }
  }

  public ReturnValueWrapper insert(MappedStatement ms, SqlInfo sqlInfo) {
    // 不建议以下这种方式创建SQL，这样会增加编译之后的字节码文件数量
    // 因为这样会创建一个匿名内部类
    /*SQL sql = new SQL() {
      {
        INSERT_INTO(sqlInfo.getTableName());
        INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias());
        String[] prepareColumnValues = new String[sqlInfo.getFiledCount()];
        Arrays.fill(prepareColumnValues, PREPARE_FLAG);
        INTO_VALUES(prepareColumnValues);
      }
    };*/
    String[] prepareColumnValues = new String[sqlInfo.getFieldCount()];
    Arrays.fill(prepareColumnValues, PREPARE_FLAG);
    String sql = new SQL().INSERT_INTO(sqlInfo.getTableName())
                          .INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias())
                          .INTO_VALUES(prepareColumnValues)
                          .toString();
    List<ParameterMapping> parameterMappings = new ArrayList<>(sqlInfo.getFieldCount());
    TypeHandlerRegistry reg = ms.getConfiguration().getTypeHandlerRegistry();
    ParameterMapping.Builder parameterBuilder;
    for (PropertyColumnMapping mapping : sqlInfo.getPropertyColumnMappings()) {
      parameterBuilder =
          new ParameterMapping.Builder(ms.getConfiguration(),
                                       mapping.getJavaClassPropertyName(),
                                       mapping.getTypeHandler());
      parameterMappings.add(parameterBuilder.build());
    }
    return new ReturnValueWrapper(new StaticTextSqlNode(sql), parameterMappings);
  }

  public ReturnValueWrapper selectOne(MappedStatement ms, SqlInfo sqlInfo) {
    // 首先判断是否有主键，如果没有主键，那么抛出异常
    checkPrimaryKey(sqlInfo);
    String sql = new SQL().SELECT(sqlInfo.getAllColumnsWithAlias())
                          .FROM(sqlInfo.getTableName())
                          .WHERE(sqlInfo.getPrimaryKeyCondition())
                          .toString();
    return primaryKeyResolver(sqlInfo.getPrimaryKey(), ms.getConfiguration(), sql);
  }

  public ReturnValueWrapper delete(MappedStatement ms, SqlInfo sqlInfo) {
    checkPrimaryKey(sqlInfo);
    String sql = new SQL().DELETE_FROM(sqlInfo.getTableName())
                          .WHERE(sqlInfo.getPrimaryKeyCondition())
                          .toString();
    return primaryKeyResolver(sqlInfo.getPrimaryKey(), ms.getConfiguration(), sql);
  }

  /**
   * 生成以主键作为条件的sql的返回值
   */
  private ReturnValueWrapper primaryKeyResolver(PropertyColumnMapping primaryKey, Configuration config, String sql) {
    List<ParameterMapping> parameterMappings = new ArrayList<>(1);
    parameterMappings.add(new ParameterMapping.Builder(
            config,
            primaryKey.getJavaClassPropertyName(),
            primaryKey.getTypeHandler()
        ).build()
    );
    return new ReturnValueWrapper(new StaticTextSqlNode(sql), parameterMappings);
  }

  public ReturnValueWrapper batchDelete(MappedStatement ms, SqlInfo sqlInfo) {
    checkPrimaryKey(sqlInfo);
    String sql = new StringBuilder("DELETE FROM ")
        .append(sqlInfo.getTableName())
        .append(" WHERE ")
        .append(sqlInfo.getPrimaryKey().getDatabaseColumnName())
        .append(" IN ")
        .toString();
    // 这里生成的sql仅仅是 DELETE FROM table_name WHERE primary_key_name IN
    // 后面的条件交给BatchDeleteParameterObjectResolver去通过反射生成
    return new ReturnValueWrapper(new StaticTextSqlNode(sql), BatchDeleteParameterObjectResolver.getInstance());
  }

  public ReturnValueWrapper update(MappedStatement ms, SqlInfo sqlInfo, Object args) {
    checkPrimaryKey(sqlInfo);
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver exampleResolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    if (!exampleResolver.hasPrimaryKeyAndNotDefault()) {
      throw new NoPrimaryKeyException("primary key is default value");
    }
    else {
      List<PropertyColumnMapping> nonNullColumns = exampleResolver.getWithoutPrimaryKey();
      if (nonNullColumns.isEmpty()) {
        throw new IllegalStateException("Please update at least one column");
      }
      else {
        List<ParameterMapping> parameterMappings = new ArrayList<>(nonNullColumns.size() + 1);
        SQL sql = new SQL();
        sql.UPDATE(sqlInfo.getTableName());
        for (PropertyColumnMapping pcm : nonNullColumns) {
          sql.SET(pcm.getDatabaseColumnName() + " = ?");
          parameterMappings.add(new ParameterMapping.Builder(
              ms.getConfiguration(),
              pcm.getJavaClassPropertyName(),
              pcm.getValue().getClass()
          ).build());
        }
        sql.WHERE(sqlInfo.getPrimaryKey().getDatabaseColumnName() + " = ?");
        parameterMappings.add(new ParameterMapping.Builder(
            ms.getConfiguration(),
            sqlInfo.getPrimaryKey().getJavaClassPropertyName(),
            sqlInfo.getPrimaryKey().getTypeHandler()
        ).build());
        return new ReturnValueWrapper(new StaticTextSqlNode(sql.toString()), parameterMappings);
      }
    }
  }

  public ReturnValueWrapper selectList(MappedStatement ms, SqlInfo sqlInfo, Object args) {
    checkBeanType(sqlInfo.getBeanType(), args);
    BeanExampleResolver resolver = new BeanExampleResolver(args, sqlInfo.getJavaProperties(), sqlInfo.getDatabaseColumns());
    List<PropertyColumnMapping> exampleSelectList = resolver.selectExample();
    if (exampleSelectList.isEmpty()) {
      throw new IllegalStateException("Please specify at least one condition");
    }
    else {
      SQL sql = new SQL().SELECT(sqlInfo.getAllColumnsWithAlias())
                         .FROM(sqlInfo.getTableName());
      List<ParameterMapping> parameterMappings = new ArrayList<>(exampleSelectList.size());
      for (PropertyColumnMapping pcm : exampleSelectList) {
        parameterMappings.add(new ParameterMapping.Builder(
            ms.getConfiguration(),
            pcm.getJavaClassPropertyName(),
            pcm.getValue().getClass()
        ).build());
        sql.WHERE(pcm.getDatabaseColumnName() + " = ?");
      }
      return new ReturnValueWrapper(new StaticTextSqlNode(sql.toString()), parameterMappings);
    }
  }

  public ReturnValueWrapper batchInsert(MappedStatement ms, SqlInfo sqlInfo) {
    return sqlDialect.batchInsert(ms.getConfiguration(), sqlInfo);
  }

  /**
   * 检查如果没有主键，那么就抛出异常
   */
  private void checkPrimaryKey(SqlInfo sqlInfo) {
    if (!sqlInfo.hasPrimaryKey()) {
      throw new NoPrimaryKeyException();
    }
  }

  /**
   * 检查参数的类型是否和当前Mapper的实体类一致，如果不一致，那么抛出异常
   */
  private void checkBeanType(Class<?> beanType, Object args) {
    if (beanType != args.getClass()) {
      throw new IllegalArgumentException("Parameter must be " + beanType + " only");
    }

  }

}