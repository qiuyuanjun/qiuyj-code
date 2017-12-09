package com.qiuyj.tools.mybatis.build;

import com.qiuyj.tools.mybatis.MapperSqlSource;
import com.qiuyj.tools.mybatis.SqlInfo;
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

  public ReturnValueWrapper(SqlNode sqlNode) {
    this.sqlNode = sqlNode;
    this.generateStaticSqlSource = false;
  }

  public ReturnValueWrapper(SqlNode sqlNode, List<ParameterMapping> parameterMappings) {
    this.sqlNode = sqlNode;
    this.parameterMappings = parameterMappings;
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

  public boolean needParseParameter() {
    return generateStaticSqlSource && Objects.isNull(parameterMappings);
  }

  /**
   * 该方法目前暂时不支持
   */
  public void parseParameterMappings(SqlInfo sqlInfo, Object parameterObject) {
    throw new UnsupportedOperationException("Not support yet");
  }
}