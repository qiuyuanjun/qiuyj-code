package com.qiuyj.asm.bean.metadata;

import com.qiuyj.asm.bean.MetadataUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class BeanMetadata extends AbstractMetadata {

  static final String JAVA_LANG_OBJECT = "java/lang/Object";
  static final String JAVALANGOBJECT = "java.lang.Object";

  /**
   * 当前bean的父类（可能是BeanMetadata类型，也肯能是String类型，还有可能是null），如果没有（Object）那么，此属性应当标志null
   */
  private Object parentSource;

  /**
   * 当前bean的所有自己声明的set和get方法
   */
  private MethodMetadata[] methods;

  /**
   * 当前bean的所有自己声明的非static final的属性
   */
  private FieldMetadata[] fields;

  /**
   * 当前bean的Class对象
   */
  private final Class<?> beanClass;

  public BeanMetadata(Class<?> beanCls) {
    beanClass = Objects.requireNonNull(beanCls);
  }

  public void setParent(Object parentSource) {
    if ((!(parentSource instanceof String))
        || (!JAVA_LANG_OBJECT.equals(parentSource) && !JAVALANGOBJECT.equals(parentSource))) {
      this.parentSource = parentSource;
    }
  }

  public BeanMetadata getParent(ClassLoader parentClassLoader) {
    if (parentSource instanceof String) {
      String parentClassName = ((String) parentSource).replace("/", ".");
      // 通过ASM得到对应的BeanMetadata对象
      try {
        parentSource = MetadataUtils.newBeanMetadata(parentClassName, parentClassLoader);
      } catch (IOException e) {
        throw new IllegalStateException("Error parsing class: " + parentClassName + ".\n Caused by: " + e, e);
      }
    }
    return (BeanMetadata) parentSource;
  }

  public BeanMetadata getParent() {
    return getParent(null);
  }

}