package com.qiuyj.mybatis.sqlbuild;

import com.qiuyj.mybatis.SqlInfo;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;

import static com.qiuyj.mybatis.sqlbuild.SqlProvider.PREPARE_FLAG;

/**
 * @author qiuyj
 * @since 2018/4/1
 */
public class DefaultMapperSqlBuilder implements MapperSqlBuilder {

  @Override
  public Object insert(SqlInfo sqlInfo, Object args) {
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
    return new SQL().INSERT_INTO(sqlInfo.getTableName())
                    .INTO_COLUMNS(sqlInfo.getAllColumnsWithoutAlias())
                    .INTO_VALUES(prepareColumnValues)
                    .toString();
  }

  @Override
  public Object selectOne(SqlInfo sqlInfo, Object args) {
    return null;
  }

  @Override
  public Object selectList(SqlInfo sqlInfo, Object args) {
    return null;
  }

  @Override
  public Object update(SqlInfo sqlInfo, Object args) {
    return null;
  }

  @Override
  public Object delete(SqlInfo sqlInfo, Object args) {
    return null;
  }

  @Override
  public Object batchInsert(SqlInfo sqlInfo, Object args) {
    return null;
  }

  @Override
  public Object batchDelete(SqlInfo sqlInfo, Object args) {
    return null;
  }
}