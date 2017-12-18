package com.qiuyj.mybatis;

import com.qiuyj.commons.AnnotationUtils;
import com.qiuyj.commons.ClassUtils;
import com.qiuyj.mybatis.build.ParameterResolver;
import com.qiuyj.mybatis.mapper.Mapper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiuyj
 * @since 2017/11/13
 */
public class MapperMethodResolver {
  private final Class<? extends Mapper> baseMapperClass;
  private final Set<String> mapperMethodSignatures;
  private final Set<Method> exampleMapperMethods = Collections.newSetFromMap(new HashMap<>());

  MapperMethodResolver(Class<? extends Mapper> baseMapperClass) {
    if (!baseMapperClass.isInterface())
      throw new IllegalArgumentException("interface only");
    this.baseMapperClass = baseMapperClass;
    Class<?>[] allMapperInterfaces = getAllMapperInterfaces(baseMapperClass);
    Set<String> sets = new HashSet<>(32);
    for (Class<?> mapperInterface : allMapperInterfaces) {
      Method[] methods = mapperInterface.getDeclaredMethods();
      for (Method method : methods) {
        if (hasProviderAnnotation(method)) {
          sets.add(buildUniqueName(mapperInterface, getMethodSignature(method)));
          resolveExampleParameter(method);
        }
      }
    }
    this.mapperMethodSignatures = Collections.unmodifiableSet(sets);
  }

  private void resolveExampleParameter(Method method) {
    Parameter[] parameters = method.getParameters();
    boolean exampleMethod = false;
    for (Parameter parameter : parameters) {
      if (AnnotationUtils.hasAnnotation(parameter, Example.class)) {
        exampleMethod = true;
        break;
      }
    }
    if (exampleMethod)
      exampleMapperMethods.add(method);
  }

  /**
   * 得到所有的Mapper接口的Class对象
   */
  private Class<?>[] getAllMapperInterfaces(Class<? extends Mapper> baseMapperClass) {
    Class<?>[] inters = ClassUtils.getAllInterfacesIncludingAncestorInterfaces(baseMapperClass);
    Class<?>[] allMapperInterfaces = new Class<?>[inters.length + 1];
    System.arraycopy(inters, 0, allMapperInterfaces, 1, inters.length);
    allMapperInterfaces[0] = baseMapperClass;
    return allMapperInterfaces;
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
    // 如果当前执行的方法不是baseMapperClass的父接口或者就是baseMapperClass，那么表明当前执行的方法不是通用Mapper提供的方法
    if (!currMethod.getDeclaringClass().isAssignableFrom(baseMapperClass))
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
  public Method getMapperDeclaredMethod(Class<?> cls, String methodStr, Object args) {
    // 根据mybatis的规则，这里只要比较名字一样即可
    // mybatis定义的一个Mapper里面的同名方法只能有一个
    Method mapperMethod = null;
    // 如果当前mapper接口没有继承baseMapperClass（通常是Mapper.class）
    // 那么表明当前方法不是通用Mapper定义的方法
    if (baseMapperClass.isAssignableFrom(cls)) {
      // 首先遍历所有的方法，查找出同名的方法
      List<Method> targetMethods = Arrays.stream(cls.getMethods())
                                         .filter(m -> methodStr.equals(m.getName()))
                                         .collect(Collectors.toList());
      if (targetMethods.size() == 1)
        mapperMethod = targetMethods.get(0);
      else if (targetMethods.size() > 1) {
        // 然后对方法遍历，得到最符合要求的方法
        // 首先解析参数对象
        Class<?>[] parameterTypeMeta = ParameterResolver.resolveParameter(args).getParameterTypes();
        int paramLen = parameterTypeMeta.length;
        for (Method m : targetMethods) {
          int paramCount = m.getParameterCount();
          // 参数个数必须一致
          if (paramLen == paramCount) {
            Class<?>[] paramTypes = m.getParameterTypes();
            boolean isSame = true;
            for (int i = 0; i < paramCount; i++) {
              if (!paramTypes[i].isAssignableFrom(parameterTypeMeta[i])) {
                isSame = false;
                break;
              }
            }
            if (isSame) {
              mapperMethod = m;
              break;
            }
          }
        }
      }
    }
    return mapperMethod;
  }

  public boolean isExampleMethod(Method method) {
    return exampleMapperMethods.contains(method);
  }
}