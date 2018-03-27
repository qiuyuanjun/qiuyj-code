package com.qiuyj.classreading.metadata;

import com.qiuyj.classreading.Metadata;
import com.qiuyj.commons.AnnotationAttributes;
import com.qiuyj.commons.StringUtils;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/3/4
 */
public class ClassMetadata implements Metadata {

  /**
   * 代表class类全名或者Class对象
   */
  private final Object classSource;

  /**
   * 代表父class类全名或者Class对象
   */
  private Object parentSource;

  /*
   * for asm
   */
  public ClassMetadata(String className) {
    if (StringUtils.isBlank(className)) {
      throw new IllegalArgumentException("Class name can not be null or empty string.");
    }
    else {
      classSource = className;
    }
  }

  /*
   * for jdk reflection
   */
  public ClassMetadata(Class<?> cls) {
    classSource = Objects.requireNonNull(cls);
  }

  @Override
  public String getName() {
    String name;
    if (classSource instanceof Class<?>) {
      name = ((Class<?>) classSource).getName();
    }
    else {
      name = (String) classSource;
    }
    return name;
  }

  @Override
  public boolean isAnnotated(String annotationName) {
    return false;
  }

  @Override
  public AnnotationAttributes annotationAttributesFor(String annotationName) {
    return null;
  }

  @Override
  public String[] getAnnotationNames() {
    return new String[0];
  }
}