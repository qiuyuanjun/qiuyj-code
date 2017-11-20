package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.mybatis.SqlInfo;

import java.lang.reflect.Field;

/**
 * 属性检查器
 * @author qiuyj
 * @since 2017/11/20
 */
public interface ConditionChecker {

  /**
   * 检查对应的javabean的属性是否满足对应的关系
   * @param field 属性的Field对象
   * @param sqlInfo 当前bean的sqlInfo对象
   * @return {boolean} 如果返回true，那么当前属性会接着执行检查器链，如果返回false，那么就不会执行剩下的检查器
   */
  int doCheck(Field field, SqlInfo sqlInfo);

  /**
   * 将一个属性按照javabean规范转换成对应的getter方法名
   */
  default String fieldToGetterName(Field field) {
    char[] chs = field.getName().toCharArray();
    chs[0] = Character.toUpperCase(chs[0]);
    StringBuilder sb =
        (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)
            ? new StringBuilder("is") : new StringBuilder("get");
    sb.append(chs);
    return sb.toString();
  }
}
