package com.qiuyj.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 注解工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class AnnotationUtils {

  /**
   * 判断给定的AnnotatedElement（可以使Class，Method，Field和Constructor）是否有指定的注解
   * @return 如果有指定的注解则返回true，否则返回false
   */
  public static boolean hasAnnotation(AnnotatedElement ae, Class<? extends Annotation> anno) {
    return Objects.nonNull(findAnnotation(ae, anno));
  }

  /**
   * 递归查找对应的注解的Annotation对象
   * 查找算法：首先判断本类上是否直接由想要查找的注解，如果有，那么直接返回
   * 如果没有，那么就递归查找注解上是否有对应的注解，如果有，那么返回
   * 如果还是没有找到，那么就会判断，如果给定的是一个Class，那么就首先查找该Class的所有的
   * 接口，判断接口上是否有对应的注解，（注意：这里仅仅判断一层接口），如果没有，那么就判断
   * 该Class的父类是否有对应的注解，递归所有的父类，直到Object，如果还是没有找到，那么返回null
   * @return null 如果按照查找算法没有找到，否则返回找到的注解的Annotation对象
   */
  public static <A extends Annotation> A findAnnotation(AnnotatedElement ae, Class<A> anno) {
    Objects.requireNonNull(ae);
    return Optional.ofNullable(anno).map(ano -> findAnnotation(ae, ano, new HashSet<>())).orElse(null);
  }

  @SuppressWarnings("unchecked")
  private static <A extends Annotation> A findAnnotation(AnnotatedElement ae, Class<A> anno, Set<Annotation> visited) {
    Annotation[] annos = ae.getDeclaredAnnotations();
    for (Annotation a : annos) {
      if (a.annotationType() == anno)
        return (A) a;
    }
    A finded;
    for (Annotation a : annos) {
      if (!isJavaLangAnnotationPackage(a) && visited.add(a)) {
        finded = findAnnotation(a.annotationType(), anno, visited);
        if (Objects.nonNull(finded))
          return finded;
      }
    }
    if (ae instanceof Class<?>) {
      Class<?> cls = (Class<?>) ae;
      Class<?>[] inters = cls.getInterfaces();
      for (Class<?> inter : inters) {
        finded = findAnnotation(inter, anno, visited);
        if (Objects.nonNull(finded))
          return finded;
      }
      for (Class<?> superclass = cls.getSuperclass();
           ClassUtils.superclassCondition(superclass);
           superclass = superclass.getSuperclass()) {
        finded = findAnnotation(cls, anno, visited);
        if (Objects.nonNull(finded))
          return finded;
      }
    }
    return null;
  }

  private static boolean isJavaLangAnnotationPackage(Annotation a) {
    return "java.lang.annotation".equals(a.annotationType().getPackage().getName());
  }
}