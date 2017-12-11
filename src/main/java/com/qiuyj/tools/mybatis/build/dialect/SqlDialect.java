package com.qiuyj.tools.mybatis.build.dialect;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ReturnValueWrapper;
import org.apache.ibatis.session.Configuration;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public interface SqlDialect {

  ReturnValueWrapper batchInsert(Configuration configuration, SqlInfo sqlInfo);
}
