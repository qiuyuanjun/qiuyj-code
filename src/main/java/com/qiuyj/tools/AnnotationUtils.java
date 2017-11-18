package com.qiuyj.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * 注解工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class AnnotationUtils {

  public static <A> A findAnnotation(AnnotatedElement ae, Class<? extends Annotation> anno) {
    return null;
  }
}
