package com.qiuyj.commons;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 注解的所有属性（参考spring-core的AnnotationAttributes）
 * @author qiuyj
 * @since 2018/1/29
 */
public final class AnnotationAttributes extends LinkedHashMap<String, Object> {

  private Class<? extends Annotation> annotationClass;

  public AnnotationAttributes(int capacity) {
    super(capacity);
  }

  public AnnotationAttributes(String annotationClassName, ClassLoader classLoader) {
    annotationClass = getAnnotationClass(annotationClassName, classLoader);
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends Annotation> getAnnotationClass(String annotationClassName, ClassLoader classLoader) {
    Class<? extends Annotation> annoCls = null;
    if (Objects.nonNull(classLoader)) {
      try {
        annoCls = (Class<? extends Annotation>) ClassUtils.classForName(annotationClassName, classLoader);
      }
      catch (ClassNotFoundException e) {
        // ignore
      }
    }
    return annoCls;
  }

}