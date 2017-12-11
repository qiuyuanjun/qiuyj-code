package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.SqlInfo;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
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

  /**
   * 重新设置StaticTextSqlNode的text属性的值
   * @param staticNode 要设置的StaticTextSqlNode对象
   * @param append 要追加的sql值
   */
  default void resetStaticSqlNode(StaticTextSqlNode staticNode, String append) {
    // 最后通过反射修改SqlNode里面的sql
    Field textField = ReflectionUtils.getDeclaredField(staticNode.getClass(), "text");
    // 由于StaticTextSqlNode里面的text属性是final类型的，所以这里需要设置accessible
    textField.setAccessible(true);
    try {
      String origin = (String) textField.get(staticNode);
      textField.set(staticNode, origin + append);
    } catch (IllegalAccessException e) {
      // ignore
    }
  }
}