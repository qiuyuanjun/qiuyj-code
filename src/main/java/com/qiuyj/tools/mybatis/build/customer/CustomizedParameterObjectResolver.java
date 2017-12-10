package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * @author qiuyj
 * @since 2017/12/10
 */
@FunctionalInterface
public interface CustomizedParameterObjectResolver {

  /**
   * 解析参数，生成对应的ParameterMapping对象
   * @param config mybatis全局配置
   * @param sqlInfo 对应的bean的sqlInfo对象
   * @param paramObj 当前执行的方法的参数
   * @param sqlNode sqlNode，允许最后一次修改sql（通过反射的方法）
   * @return ParameterMapping的List集合
   */
  List<ParameterMapping> resolveParameterObject(Configuration config,
                                                SqlInfo sqlInfo,
                                                Object paramObj,
                                                SqlNode sqlNode);
}