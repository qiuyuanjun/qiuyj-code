package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.mybatis.mapper.Mapper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/13
 */
class MapperMethodResolver {
  private final Set<String> mapperMethodSignatures;
  private final Map<String, Method> mapperMethods = new HashMap<>();

  MapperMethodResolver(Class<? extends Mapper> baseMapperClass) {
    if (!baseMapperClass.isInterface())
      throw new IllegalArgumentException("interface only");
    Class<?>[] mapperMethods = ClassUtils.getAllInterfacesIncludingAncestorInterfaces(baseMapperClass);
    Class<?>[] allMapperMethods = new Class<?>[mapperMethods.length + 1];
    System.arraycopy(mapperMethods, 0, allMapperMethods, 1, mapperMethods.length);
    allMapperMethods[0] = baseMapperClass;
    Set<String> sets = new HashSet<>(32);
    for (Class<?> mapperMethod : allMapperMethods) {
      Method[] methods = mapperMethod.getDeclaredMethods();
      for (Method method : methods) {
        if (hasProviderAnnotation(method)) {
          sets.add(buildUniqueName(mapperMethod, getMethodSignature(method)));
          this.mapperMethods.put("__BASE_MAPPER__" + method.getName(), method);
        }
      }
    }
    this.mapperMethodSignatures = Collections.unmodifiableSet(sets);
  }

  private String getMethodSignature(Method method) {
    StringBuilder sb = new StringBuilder(method.getReturnType().getName());
    sb.append(" ");
    sb.append(method.getName());
    sb.append("(");
    int paramCount = method.getParameterCount();
    if (paramCount > 0) {
      Class<?>[] params = method.getParameterTypes();
      for (int i = 0; i < paramCount - 1; i++) {
        sb.append(params[i].getName());
        sb.append(",");
      }
      sb.append(params[paramCount - 1].getName());
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * 判断当前的方法是否是通用mapper的所定义的方法
   */
  public boolean isMapperMethod(Method currMethod) {
    if (Objects.isNull(currMethod))
      return false;
    if (!hasProviderAnnotation(currMethod))
      return false;
    Class<?> mapperClass = currMethod.getDeclaringClass();
    return mapperMethodSignatures.contains(buildUniqueName(mapperClass, getMethodSignature(currMethod)));
  }

  private String buildUniqueName(Class<?> mapperClass, String methodSign) {
    return new StringBuilder(mapperClass.getName())
        .append("-")
        .append(methodSign)
        .toString();
  }

  private boolean hasProviderAnnotation(Method method) {
    return AnnotationUtils.hasAnnotation(method, InsertProvider.class) ||
        AnnotationUtils.hasAnnotation(method, UpdateProvider.class) ||
        AnnotationUtils.hasAnnotation(method, DeleteProvider.class) ||
        AnnotationUtils.hasAnnotation(method, SelectProvider.class);
  }

  /**
   * 根据方法名得到对应的Mapper里面的方法
   * @param cls Mapper的Class对象
   * @param methodStr 方法名
   * @return 如果有，那么得到对应的Method对象，否则返回null
   */
  public Method getMapperDeclaredMethod(Class<?> cls, String methodStr) {
    // 首先从缓存中获取，如果缓存没有查到，那么就遍历当前接口的所有方法
    // 根据mybatis的规则，这里只要比较名字一样即可
    // mybatis定义的一个Mapper里面的同名方法只能有一个
    Method mapperMethod = null;
    if (Mapper.class.isAssignableFrom(cls)) {
      mapperMethod = mapperMethods.get("__BASE_MAPPER__" + methodStr);
      if (Objects.isNull(mapperMethod)) {
        mapperMethod
            = Arrays.stream(cls.getMethods())
                    .filter(m -> methodStr.equals(m.getName()) && hasProviderAnnotation(m))
                    .findFirst()
                    .orElse(null);
      }
    }
    return mapperMethod;
  }
}