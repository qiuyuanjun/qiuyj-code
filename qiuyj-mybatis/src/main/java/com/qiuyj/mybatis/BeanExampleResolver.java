package com.qiuyj.mybatis;

import com.qiuyj.commons.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/28
 */
public class BeanExampleResolver {
  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUE;
  static {
    PRIMITIVE_DEFAULT_VALUE = new HashMap<>();
    PRIMITIVE_DEFAULT_VALUE.put(Boolean.TYPE, false);
    PRIMITIVE_DEFAULT_VALUE.put(Byte.TYPE, (byte) 0);
    PRIMITIVE_DEFAULT_VALUE.put(Character.TYPE, '\u0000');
    PRIMITIVE_DEFAULT_VALUE.put(Double.TYPE, 0.0);
    PRIMITIVE_DEFAULT_VALUE.put(Float.TYPE, 0.0F);
    PRIMITIVE_DEFAULT_VALUE.put(Integer.TYPE, 0);
    PRIMITIVE_DEFAULT_VALUE.put(Long.TYPE, 0L);
    PRIMITIVE_DEFAULT_VALUE.put(Short.TYPE, (short) 0);
  }
  private List<PropertyColumnMapping> withoutPrimaryKey = new ArrayList<>();
  private PropertyColumnMapping primaryKey;

  public BeanExampleResolver(Object bean, List<String> attributes, List<String> aliases) {
    // 首先得到PrimaryKey
    String primaryKeyName = attributes.get(0);
    resolveField(bean, primaryKeyName, fieldValue ->
      BeanExampleResolver.this.primaryKey = new PropertyColumnMapping(
          primaryKeyName,
          aliases.get(0),
          fieldValue
      ));
    for (int idx = 1; idx < attributes.size(); idx++) {
      final String attr = attributes.get(idx),
                   aliase = aliases.get(idx);
      resolveField(bean, attr, fieldValue ->
        BeanExampleResolver.this.withoutPrimaryKey.add(
            new PropertyColumnMapping(
                attr,
                aliase,
                fieldValue
            )
        ));
    }
  }

  /**
   * 处理每一个字段
   * @param bean 对象
   * @param fieldName 字段名
   * @param callback 字段不是默认值的时候的回调方法
   */
  private void resolveField(Object bean, String fieldName, NonNullFieldCallback callback) {
    Class<?> beanType = bean.getClass();
    Field field = ReflectionUtils.getDeclaredField(beanType, fieldName);
    Object defaultValue = primitiveFieldDefaultValue(field);
    Object fieldValue = invokeFieldGetter(bean, field);
    if (Objects.nonNull(defaultValue) && defaultValue == fieldValue) {
      // 表明当前是基本数据类型，并且值是默认值，不做任何处理
    }
    else if (Objects.isNull(fieldValue)) {
      // 表明是引用类型，但是其值是null，也就是默认值，也不做任何处理
    }
    else {
      // 不是默认值，字段有值
      callback.resolveNonNullField(fieldValue);
    }
  }

  @SuppressWarnings("deprecation")
  private static Object invokeFieldGetter(Object obj, Field f) {
    if (!f.isAccessible()) {
      f.setAccessible(true);
    }
    try {
      return f.get(obj);
    }
    catch (IllegalAccessException e) {
      // ignore;
    }
    throw new IllegalStateException("Should never get here");
  }

  @FunctionalInterface
  private interface NonNullFieldCallback {

    /**
     * 处理不是null并且不是默认值（基本数据类型）的字段
     * @param fieldValue 字段的值
     */
    void resolveNonNullField(Object fieldValue);
  }

  /**
   * 如果是基本数据类型，那么得到基本数据类型的默认值
   */
  private Object primitiveFieldDefaultValue(Field f) {
    Type t = f.getGenericType();
    Object value = null;
    if (t instanceof Class<?>) {
      String name = ((Class) t).getName();
      if (name.length() < 8) {
        value = PRIMITIVE_DEFAULT_VALUE.get(t);
      }
    }
    return value;
  }

  /**
   * 该方法主要是给selectList方法用的
   * 由于selectList方法不需要一定指定主键
   */
  public List<PropertyColumnMapping> selectExample() {
    List<PropertyColumnMapping> rt = new ArrayList<>(withoutPrimaryKey);
    if (Objects.nonNull(primaryKey)) {
      rt.add(0, primaryKey);
    }
    return rt;
  }

  /**
   * 判断主键是否是默认值，如果是默认值，返回false，否则返回true
   * 该方法主要是给update方法用
   */
  public boolean hasPrimaryKeyAndNotDefault() {
    return Objects.nonNull(primaryKey);
  }

  /**
   * 得到所有的非主键并且不是默认值的字段
   */
  public List<PropertyColumnMapping> getWithoutPrimaryKey() {
    return withoutPrimaryKey;
  }

  public PropertyColumnMapping getPrimaryKey() {
    return primaryKey;
  }
}