package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class OracleBatchInsertParameterObjectResolver implements CustomizedParameterObjectResolver {

  @Override
  public List<ParameterMapping> resolveParameterObject(Configuration config, SqlInfo sqlInfo, Object paramObj, SqlNode sqlNode) {
    return null;
  }
}
