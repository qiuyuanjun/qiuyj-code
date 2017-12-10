package com.qiuyj.tools.mybatis.build;

import com.qiuyj.tools.mybatis.MapperSqlSource;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.customer.CustomizedParameterObjectResolver;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * SqlProvider里面的方法的返回值
 * @author qiuyj
 * @since 2017/12/9
 */
public class ReturnValueWrapper {
  private SqlNode sqlNode;
  private List<ParameterMapping> parameterMappings;
  private boolean generateStaticSqlSource = true;
  private CustomizedParameterObjectResolver parameterObjectResolver;

  public ReturnValueWrapper(SqlNode sqlNode) {
    this.sqlNode = sqlNode;
    this.generateStaticSqlSource = false;
  }

  public ReturnValueWrapper(SqlNode sqlNode, List<ParameterMapping> parameterMappings) {
    this.sqlNode = sqlNode;
    this.parameterMappings = parameterMappings;
  }

  public ReturnValueWrapper(SqlNode sqlNode, CustomizedParameterObjectResolver parameterObjectResolver) {
    this.sqlNode = sqlNode;
    this.parameterObjectResolver = parameterObjectResolver;
  }

  /**
   * 生成对应的SqlSource，这里仅仅会生成DynamicSqlSource和StaticSqlSource
   */
  public SqlSource generateSqlSource(Configuration configuration) {
    if (Objects.isNull(sqlNode))
      throw new IllegalArgumentException("SqlNode can not be null");
    SqlSource sqlSource;
    if (generateStaticSqlSource) {
      DynamicContext context = new DynamicContext(configuration, null);
      sqlNode.apply(context);
      sqlSource = new StaticSqlSource(configuration, context.getSql(), parameterMappings);
    } else
      sqlSource = new DynamicSqlSource(configuration, sqlNode);
    return new MapperSqlSource(sqlSource);
  }

  private boolean needParseParameter() {
    return generateStaticSqlSource && Objects.isNull(parameterMappings);
  }

  /**
   * 处理用户自定义的生成ParameterMapping的方法接口
   */
  public void customizedResolveParameterObject(SqlInfo sqlInfo, Object parameterObject, Configuration config) {
    if (needParseParameter()) {
      if (Objects.isNull(parameterObjectResolver))
        throw new IllegalStateException("Please specify parameter 'parameterMappings' or 'parameterObjectResolver' in constructor method");
      else
        parameterMappings = parameterObjectResolver.resolveParameterObject(config, sqlInfo, parameterObject, sqlNode);
    }
    // 最后再一次验证
    if (needParseParameter())
      throw new IllegalStateException("Parameter 'parameterMappings' cannot be null with generate static sql source mode");
  }

}