package com.qiuyj.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class ClassUtils {
  private static final String[] PRIMATIVE_TYPE_NAME = {
      "boolean",
      "byte",
      "char",
      "double",
      "float",
      "int",
      "long",
      "short",
      "void"
  };
  private static final Class<?>[] PRIMATIVE_TYPE_CLASS = {
      Boolean.TYPE,
      Byte.TYPE,
      Character.TYPE,
      Double.TYPE,
      Float.TYPE,
      Integer.TYPE,
      Long.TYPE,
      Short.TYPE,
      Void.TYPE
  };

  /**
   * 得到基本数据类型的Class对象
   */
  public static Class<?> primativeClassForName(String className) {
    Objects.requireNonNull(className);
    if (className.length() < 8) {
      int idx = Arrays.binarySearch(PRIMATIVE_TYPE_NAME, className);
      if (idx > -1)
        return PRIMATIVE_TYPE_CLASS[idx];
    }
    return null;
  }

  public static Class<?> resolveClassName(String className, ClassLoader cl) {
    try {
      return classForName(className, cl);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Can not find: " + className);
    }
  }

  private static final String ARRAY_SUFFIX = "[]";
  private static final String INNER_CLASS_SEPERATOR = "$";

  /**
   * 根据类名得到对应的Class对象
   * @throws ClassNotFoundException 当类名不存在的时候
   */
  public static Class<?> classForName(String className, ClassLoader cl) throws ClassNotFoundException {
    Class<?> cls = primativeClassForName(className);
    if (Objects.nonNull(cls))
      return cls;
    else if (className.endsWith(ARRAY_SUFFIX)) {
      cls = classForName(StringUtils.substringBefore(className, ARRAY_SUFFIX), cl);
      return Array.newInstance(cls, 0).getClass();
    } else if (className.startsWith("[L") && className.endsWith(";")) {
      cls = classForName(className.substring(2, className.length() - 1), cl);
      return Array.newInstance(cls, 0).getClass();
    } else {
      try {
        return Objects.isNull(cl) ? Class.forName(className) : cl.loadClass(className);
      } catch (ClassNotFoundException e) {
        // 有可能是内部类
        int idx = className.lastIndexOf(".");
        String innerClassName =
            className.substring(0, idx) + INNER_CLASS_SEPERATOR + className.substring(idx + 1);
        try {
          return Objects.isNull(cl) ? Class.forName(innerClassName) : cl.loadClass(innerClassName);
        } catch (ClassNotFoundException e2) {
          // ignore;
        }
        throw e;
      }
    }
  }

  /**
   * 得到默认的类加载器，可能为null
   */
  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Exception e) {
      // ignore
    }
    if (Objects.isNull(cl)) {
      try {
        cl = ClassUtils.class.getClassLoader();
      } catch (Exception e) {
        // ignore
      }
      if (Objects.isNull(cl)) {
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable t) {
          // ignore
        }
      }
    }
    return cl;
  }

  /**
   * 得到给定的Class对象的所有属性，包括父类的属性，返回一个Field类型的数组对象
   * <note> 如果子类和父类都有一个同名的属性，那么父类的将被抛弃 </note>
   */
  public static Field[] getAllDeclaredFields(Class<?> cls) {
    List<Field> fields = getAllDeclaredFieldsAsList(cls);
    return fields.toArray(new Field[fields.size()]);
  }

  /**
   * 得到给定的Class对象的所有属性，包括父类的属性，返回一个List集合
   * <note> 如果子类和父类都有一个同名的属性，那么父类的将被抛弃 </note>
   */
  public static List<Field> getAllDeclaredFieldsAsList(Class<?> cls) {
    Objects.requireNonNull(cls);
    List<Field> fields = new ArrayList<>();
    Set<String> already = new HashSet<>();
    while (Objects.nonNull(cls) && Object.class != cls) {
      Field[] fs = cls.getDeclaredFields();
      for (Field f : fs) {
        if (already.add(f.getName()))
          fields.add(f);
      }
      cls = cls.getSuperclass();
    }
    return fields;
  }

}