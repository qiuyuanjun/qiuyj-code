package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/28
 */
public class BeanExampleResolver {
  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUE = new HashMap<Class<?>, Object>() {{
    put(Boolean.TYPE, false);
    put(Byte.TYPE, (byte) 0);
    put(Character.TYPE, '\u0000');
    put(Double.TYPE, 0.0);
    put(Float.TYPE, 0.0F);
    put(Integer.TYPE, 0);
    put(Long.TYPE, 0L);
    put(Short.TYPE, (short) 0);
  }};
  private final List<String> attributes;
  private final List<String> aliases;
  private final List<Object> values;

  public BeanExampleResolver(Object bean, List<String> attributes, List<String> aliases) {
    Class<?> beanType = bean.getClass();
    int idx = 0;
    List<String> list1 = new ArrayList<>(),
                 list2 = new ArrayList<>();
    List<Object> list3 = new ArrayList<>();
    // 首先得到PrimaryKey
    String primaryKey = attributes.get(idx++);
    resolveField(bean, primaryKey, fieldValue -> {
      list1.add(primaryKey);
      list2.add(aliases.get(0));
      list3.add(fieldValue);
    });
    if (list1.isEmpty())
      throw new IllegalStateException("Primary key must not be an default value");
    for (; idx < attributes.size(); idx++) {
      String attr = attributes.get(idx),
             aliase = aliases.get(idx);
      resolveField(bean, attr, fieldValue -> {
        list1.add(attr);
        list2.add(aliase);
        list3.add(fieldValue);
      });
    }
    this.attributes = Collections.unmodifiableList(list1);
    this.aliases = Collections.unmodifiableList(list2);
    this.values = Collections.unmodifiableList(list3);
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
    } else if (Objects.isNull(fieldValue)) {
      // 表明是引用类型，但是其值是null，也就是默认值，也不做任何处理
    } else {
      // 不是默认值，字段有值
      callback.resolveNonNullField(fieldValue);
    }
  }
  private static Object invokeFieldGetter(Object obj, Field f) {
    if (!f.isAccessible())
      f.setAccessible(true);
    try {
      return f.get(obj);
    } catch (IllegalAccessException e) {
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
    if (t instanceof Class<?> && PRIMITIVE_DEFAULT_VALUE.containsKey(t))
      return PRIMITIVE_DEFAULT_VALUE.get(t);
    return null;
  }

  /**
   * 得到所有不为默认值的java属性名称
   */
  public List<String> getNonNullJavaProperties() {
    return attributes;
  }

  /**
   * 得到所有不为默认值的java属性对应数据库里面的字段名
   */
  public List<String> getNonNullDatabaseColumns() {
    return aliases;
  }

  /**
   * 得到所有不为默认值的java属性的值
   */
  public List<Object> getNonNullValues() {
    return values;
  }
}