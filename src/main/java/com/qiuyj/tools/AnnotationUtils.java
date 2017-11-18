package com.qiuyj.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Set;

/**
 * 注解工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class AnnotationUtils {

  public static <A> A findAnnotation(AnnotatedElement ae, Class<A> anno) {
    Objects.requireNonNull(ae);
    return null;
  }

  private static <A> A findAnnotation(AnnotatedElement ae, Class<A> anno, Set<Annotation> visited) {
    return null;
  }
}
