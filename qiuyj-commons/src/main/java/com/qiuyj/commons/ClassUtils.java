package com.qiuyj.commons;

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
      if (idx > -1) {
        return PRIMATIVE_TYPE_CLASS[idx];
      }
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
    if (Objects.nonNull(cls)) {
      return cls;
    }
    else if (className.endsWith(ARRAY_SUFFIX)) {
      cls = classForName(StringUtils.substringBefore(className, ARRAY_SUFFIX), cl);
      return Array.newInstance(cls, 0).getClass();
    } else if (className.startsWith("[L") && className.endsWith(";")) {
      cls = classForName(className.substring(2, className.length() - 1), cl);
      return Array.newInstance(cls, 0).getClass();
    } else {
      if (Objects.isNull(cl)) {
        cl = getDefaultClassLoader();
      }
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
        if (already.add(f.getName())) {
          fields.add(f);
        }
      }
      cls = cls.getSuperclass();
    }
    return fields;
  }

  /**
   * 得到一个Class的所有接口，包括父类的所有接口，以及当前Class的接口的父接口
   */
  public static Class<?>[] getAllInterfacesIncludingAncestorInterfaces(Class<?> cls) {
    Set<Class<?>> interfaces = getAllInterfacesIncludingAncestorInterfacesAsSet(cls);
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

  /**
   * 得到一个Class的所有接口，包括父类的所有接口，以及当前Class的接口的父接口
   */
  public static Set<Class<?>> getAllInterfacesIncludingAncestorInterfacesAsSet(Class<?> cls) {
    Objects.requireNonNull(cls);
    Set<Class<?>> interfaces = new LinkedHashSet<>();
    Class<?>[] superInterfaces = cls.getInterfaces();
    // 首先直接添加当前Class的所有接口
    interfaces.addAll(Arrays.asList(superInterfaces));
    // 遍历当前Class的所有接口，得到这些接口的所有接口
    for (Class<?> superInterface : superInterfaces) {
      interfaces.addAll(getAllInterfacesIncludingAncestorInterfacesAsSet(superInterface));
    }
    // 如果是类，那么遍历所有的父类的所有接口
    for (Class<?> superclass = cls.getSuperclass();
         superclassCondition(superclass);
         superclass = superclass.getSuperclass()) {
      interfaces.addAll(getAllInterfacesIncludingAncestorInterfacesAsSet(superclass));
    }
    return interfaces;
  }

  /**
   * 得到一个Object对象的所有接口，包括所有父类的接口，但是如果当前Class代表的是一个接口，那么直接返回
   * <note> 每一层的Class仅仅得到当前Class的一层接口，不会得到当前Class的接口的接口 </note>
   */
  public static Class<?>[] getAllInterfaces(Object obj) {
    Objects.requireNonNull(obj);
    return getAllInterfaces(obj.getClass());
  }

  /**
   * 得到一个Class的所有接口，包括所有父类的接口，但是如果当前Class代表的是一个接口，那么直接返回
   * <note> 每一层的Class仅仅得到当前Class的一层接口，不会得到当前Class的接口的接口 </note>
   */
  public static Class<?>[] getAllInterfaces(Class<?> cls) {
    Set<Class<?>> interfaces = getAllInterfacesAsSet(cls);
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

  /**
   * 得到一个Class的所有接口，包括所有父类的接口，但是如果当前Class代表的是一个接口，那么直接返回
   * <note> 每一层的Class仅仅得到当前Class的一层接口，不会得到当前Class的接口的接口 </note>
   */
  public static Set<Class<?>> getAllInterfacesAsSet(Class<?> cls) {
    Objects.requireNonNull(cls);
    if (cls.isInterface()) {
      return Collections.singleton(cls);
    }
    else {
      Set<Class<?>> interfaces = new LinkedHashSet<>();
      while (superclassCondition(cls)) {
        interfaces.addAll(Arrays.asList(cls.getInterfaces()));
        cls = cls.getSuperclass();
      }
      return interfaces;
    }
  }

  static boolean superclassCondition(Class<?> superclass) {
    return Optional.ofNullable(superclass).map(cls -> Object.class != cls).orElse(false);
  }

  /**
   * 处理集合类型的特殊接口
   */
  static Class<?> resolveCollectionInterfaces(Class<?> cls) {
    Class<?> clsToUse = cls;
    if (cls == List.class || cls == Collection.class) {
      clsToUse = ArrayList.class;
    }
    else if (cls == Map.class) {
      clsToUse = HashMap.class;
    }
    else if (cls == Set.class) {
      clsToUse = HashSet.class;
    }
    else if (cls == SortedMap.class) {
      clsToUse = TreeMap.class;
    }
    else if (cls == SortedSet.class) {
      clsToUse = TreeSet.class;
    }
    return clsToUse;
  }
}