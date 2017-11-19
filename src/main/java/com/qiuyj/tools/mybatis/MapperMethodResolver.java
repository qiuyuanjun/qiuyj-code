package com.qiuyj.tools.mybatis;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.mybatis.mapper.Mapper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author qiuyj
 * @since 2017/11/13
 */
class MapperMethodResolver {
  private final Set<String> mapperMethodSignatures;

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
        if (hasProviderAnnotation(method))
          sets.add(getMethodSignature(method));
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
    return hasProviderAnnotation(currMethod) && mapperMethodSignatures.contains(getMethodSignature(currMethod));
  }

  private boolean hasProviderAnnotation(Method method) {
    return AnnotationUtils.hasAnnotation(method, InsertProvider.class) ||
        AnnotationUtils.hasAnnotation(method, UpdateProvider.class) ||
        AnnotationUtils.hasAnnotation(method, DeleteProvider.class) ||
        AnnotationUtils.hasAnnotation(method, SelectProvider.class);
  }
}