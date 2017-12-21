package com.qiuyj.mybatis.sqlbuild.dialect;

import com.qiuyj.mybatis.SqlInfo;
import com.qiuyj.mybatis.sqlbuild.ReturnValueWrapper;
import org.apache.ibatis.session.Configuration;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public interface SqlDialect {

  ReturnValueWrapper batchInsert(Configuration configuration, SqlInfo sqlInfo);
}
