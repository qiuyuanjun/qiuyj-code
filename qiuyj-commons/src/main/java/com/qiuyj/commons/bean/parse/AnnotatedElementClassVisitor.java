package com.qiuyj.commons.bean.parse;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/21
 */
public class AnnotatedElementClassVisitor extends ClassVisitor {

  private final Class<? extends Annotation> annotationClass;

  private final AnnotatedElementEnum annotatedElementEnum;

  public AnnotatedElementClassVisitor(Class<?> beanCls, Class<? extends Annotation> annoCls, AnnotatedElementEnum aee) {
    super(Opcodes.ASM5);
    annotationClass = Objects.requireNonNull(annoCls);
    annotatedElementEnum = Objects.requireNonNull(aee);
  }


}