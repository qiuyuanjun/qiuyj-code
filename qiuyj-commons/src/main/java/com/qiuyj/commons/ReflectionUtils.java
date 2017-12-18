package com.qiuyj.commons;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @author qiuyj
 * @since 2017/11/18
 */
@SuppressWarnings("unchecked")
public abstract class ReflectionUtils {

  /**
   * 得到泛型的实际所代表的类型
   */
  public static Class<?>[] getParameterizedTypesAsClass(Class<?> cls) {
    Set<Class<?>> sets = getParameterizedTypesAsClassAsSet(cls);
    return sets.toArray(new Class<?>[sets.size()]);
  }

  /**
   * 得到泛型的实际所代表的类型
   */
  public static Set<Class<?>> getParameterizedTypesAsClassAsSet(Class<?> cls) {
    Objects.requireNonNull(cls);
    Set<Class<?>> sets = new LinkedHashSet<>();
    addParameterizedTypes(cls.getGenericSuperclass(), sets);
    Type[] inters = cls.getGenericInterfaces();
    for (Type inter : inters) {
      addParameterizedTypes(inter, sets);
    }
    return sets;
  }

  private static void addParameterizedTypes(Type type, Set<Class<?>> sets) {
    if (type instanceof ParameterizedType) {
      sets.addAll(
          Arrays.stream(((ParameterizedType) type).getActualTypeArguments())
                .filter(t -> t instanceof Class<?>)
                .<Class<?>>map(t -> (Class<?>) t)
                .collect(Collectors.toList())
      );
    }
  }

  /**
   * 判断是否有给定参数类型的构造函数，如果有返回true，否则返回false
   */
  public static boolean hasConstructor(Class<?> cls, Class<?>... paramTypes) {
    return Objects.nonNull(getConstructorIfAvaliable(cls, paramTypes));
  }

  /**
   * 得到给定参数类型的构造函数，如果该构造函数不存在，那么返回null
   */
  public static Constructor<?> getConstructorIfAvaliable(Class<?> cls, Class<?>... paramTypes) {
    Objects.requireNonNull(cls);
    Constructor<?> ctor = null;
    try {
      ctor = cls.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      // ignore
    }
    return ctor;
  }

  /**
   * 得到给定参数类型的构造函数，如果该构造函数不存在，那么抛出异常
   */
  public static Constructor<?> getConstructor(Class<?> cls, Class<?>... paramTypes) {
    Objects.requireNonNull(cls);
    Constructor<?> ctor;
    try {
      ctor = cls.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Can not find constructor with parameter types: " + Arrays.toString(paramTypes));
    }
    return ctor;
  }

  /**
   * 得到默认的无参构造函数，如果没有，那么返回null
   */
  public static Constructor<?> getDefaultConstructorIfAvaliable(Class<?> cls) {
    return getConstructorIfAvaliable(cls);
  }

  /**
   * 得到默认的无参构造函数，如果没有，那么抛出异常
   */
  public static Constructor<?> getDefaultConstructor(Class<?> cls) {
    try {
      return getConstructor(cls);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("There are no default constructor in: " + cls, e);
    }
  }

  /**
   * 得到给定的方法Method对象，该方法会遍历所有的父类的方法
   * 如果在父类里面仍然没有得到Method对象，那么抛出异常
   */
  public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... paramTypes) {
    while (Objects.nonNull(cls)) {
      try {
        return cls.getDeclaredMethod(methodName, paramTypes);
      } catch (NoSuchMethodException e) {
        // ignore;
      }
      cls = cls.getSuperclass();
    }
    throw new IllegalStateException("Cannot find method: " + methodName + " with parameter: " + Arrays.toString(paramTypes));
  }

  /**
   * 执行方法
   * @param objectTarget 方法所属对象
   * @param methodToInvoke 要执行的方法
   * @param args 方法的参数对象
   * @return 执行方法的结果
   */
  public static Object invokeMethod(Object objectTarget, Method methodToInvoke, Object... args) {
    if (Objects.isNull(objectTarget) || Objects.isNull(methodToInvoke))
      throw new NullPointerException();
    try {
      return methodToInvoke.invoke(objectTarget, args);
    } catch (Exception e) {
      handleReflectionException(e);
    }
    throw new IllegalStateException("Should never get here");
  }

  /**
   * 处理反射的异常
   */
  public static void handleReflectionException(Exception e) {
    // 根据异常发生的频率做判断的顺序
    if (e instanceof InvocationTargetException)
      handleInvocationTargetException((InvocationTargetException) e);
    else if (e instanceof IllegalAccessException)
      throw new IllegalStateException("Private or protected?");
    else if (e instanceof NoSuchMethodException)
      throw new IllegalStateException("Can not find specificate method to execute");
    else if (e instanceof InstantiationException)
      throw new IllegalStateException("Can not instanitate class");
    else if (e instanceof NoSuchFieldException)
      throw new IllegalStateException("Can not find specificate field to execute");
    else
      throw new UndeclaredThrowableException(e);
  }

  /**
   * 处理InvacationTargetException异常，由于该异常发生频率较大，故单独封装一个函数
   */
  public static void handleInvocationTargetException(InvocationTargetException e) {
    Throwable t = e.getTargetException();
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    else if (t instanceof Error)
      throw (Error) t;
    else
      throw new UndeclaredThrowableException(t);
  }

  /**
   * 通过默认构造函数实例化给定的类全名
   * @param className 要实例化的类全名
   * @param <T> 类类型
   * @return 实例化的对象
   */
  public static<T> T instantiateClass(String className) {
    return instantiateClass(
        (Class<T>) ClassUtils.resolveClassName(className, Thread.currentThread().getContextClassLoader()), null, null
    );
  }

  /**
   * 实例化给定参数个数的构造函数
   * @param cls 要实例化的Class对象
   * @return 实例化的对象
   */
  public static<T> T instantiateClass(Class<T> cls) {
    return instantiateClass(cls, null, null);
  }

  /**
   * 实例化给定参数个数的构造函数
   * @param cls 要实例化的Class对象
   * @param ctorArgs 构造函数参数列表
   * @param ctorArgsType 构造函数参数类型列表
   * @return 实例化的对象
   */
  public static<T> T instantiateClass(Class<T> cls, Object[] ctorArgs, Class<?>[] ctorArgsType) {
    Objects.requireNonNull(cls);
    // 如果是数组，那么先处理数组
    if (cls.isArray()) {
      if (Objects.isNull(ctorArgs) || ctorArgs.length == 0)
        return (T) Array.newInstance(cls.getComponentType(), 0);
      else {
        int len = ctorArgs.length;
        T arr = (T) Array.newInstance(cls.getComponentType(), len);
        for (int i = 0; i < len; i++) {
          Array.set(arr, i, ctorArgs[i]);
        }
        return arr;
      }
    }
    cls = (Class<T>) ClassUtils.resolveCollectionInterfaces(cls);
    Constructor<T> ctor;
    if (Objects.isNull(ctorArgsType)) {
      // 如果使用默认构造函数但是却传入了参数，那么抛出异常
      if (Objects.nonNull(ctorArgs))
        throw new IllegalArgumentException("Wrong number of arguments");
      // 使用默认构造函数创建对象
      ctor = (Constructor<T>) getDefaultConstructor(cls);
    } else {
      if (Objects.isNull(ctorArgs))
        throw new IllegalArgumentException("Wrong number of arguments");
      int argsTypeLen = ctorArgsType.length,
          argsLen = ctorArgs.length;
      // 调整参数个数
      if (argsTypeLen > argsLen)
        ctorArgsType = Arrays.copyOf(ctorArgsType, argsLen);
      else if (argsTypeLen < argsLen)
        ctorArgs = Arrays.copyOf(ctorArgs, argsTypeLen);
      // 否则就根据构造函数的参数列表的Class类型获取对应的构造函数
      ctor = (Constructor<T>) getConstructor(cls, ctorArgsType);
    }
    try {
      return ctor.newInstance(ctorArgs);
    } catch (Exception e) {
      handleReflectionException(e);
    }
    throw new IllegalStateException("Should never get here");
  }

  /**
   * 得到指定的字段，该方法会递归所有的父类，如果到Objecgt还没有找到，那么抛出异常
   * @param cls 要查找字段的类
   * @param fieldName 字段名
   * @return 找到的Field字段对象
   */
  public static Field getDeclaredField(Class<?> cls, String fieldName) {
    while (Objects.nonNull(cls) && cls != Object.class) {
      try {
        return cls.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        // ignore
      }
      cls = cls.getSuperclass();
    }
    throw new IllegalStateException("Can not find field: " + fieldName + " in: " + cls + " and all its superclass");
  }

}