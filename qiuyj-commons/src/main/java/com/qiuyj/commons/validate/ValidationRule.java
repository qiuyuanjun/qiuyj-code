package com.qiuyj.commons.validate;

/**
 * 验证规则
 * @author qiuyj
 * @since 2018-05-29
 */
public interface ValidationRule {

  /**
   * 所给的值是否和验证规则匹配（只要有任意一个没有通过匹配，就返回{@code false}）
   * @param value 要验证的值
   * @return 如果匹配，返回{@code true}，否则返回{@code false}
   */
  boolean matchAny(Object value);

  /**
   * 匹配所有的值，如果途中有值不匹配，也会继续匹配剩下的值，如果有一个值没有匹配成功，那么返回{@code false}
   * @param value 要匹配的值，如果这个值是一个单一的值，那么和{@link #matchAny(Object)}效果一样
   * @return 如果匹配，返回{@code true}，否则返回{@code false}
   */
  boolean matchAll(Object value);
}
