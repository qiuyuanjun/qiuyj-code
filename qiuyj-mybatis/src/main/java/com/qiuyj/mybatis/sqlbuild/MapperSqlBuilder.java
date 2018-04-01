package com.qiuyj.mybatis.sqlbuild;

import com.qiuyj.mybatis.SqlInfo;

/**
 * 生成sql的接口，如果用户需要扩展，那么只需要继承这个接口就可以了
 * 规定，该接口包括所有的子接口，返回值都是三者其一：{@code String}代表对应的sql，
 * {@code SqlNode}代表对应的SqlNode，{@code ReturnValueWrapper}代表对应的ReturnValueWrapper
 * 方法名一定要和对应Mapper里面的方法一致，并且不能重载，所有的方法参数都为
 * {@code SqlInfo}和{@code Object}
 * @author qiuyj
 * @since 2018/4/1
 */
public interface MapperSqlBuilder {

  Object insert(SqlInfo sqlInfo, Object args);

  Object selectOne(SqlInfo sqlInfo, Object args);

  Object selectList(SqlInfo sqlInfo, Object args);

  Object update(SqlInfo sqlInfo, Object args);

  Object delete(SqlInfo sqlInfo, Object args);

  Object batchInsert(SqlInfo sqlInfo, Object args);

  Object batchDelete(SqlInfo sqlInfo, Object args);
}
