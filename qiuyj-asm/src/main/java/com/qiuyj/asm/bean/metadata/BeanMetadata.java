package com.qiuyj.asm.bean.metadata;

import com.qiuyj.asm.bean.MetadataUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
   * 当前bean的所有自己声明的get方法
   */
  private Map<String, MethodMetadata> getterMethods = new LinkedHashMap<>();

  /**
   * 当前bean的所有自己声明的非static final的属性
   */
  private Map<String, FieldMetadata> fields = new LinkedHashMap<>();

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
      }
      catch (IOException e) {
        throw new IllegalStateException("Error parsing class: " + parentClassName + ".\n Caused by: " + e, e);
      }
    }
    return (BeanMetadata) parentSource;
  }

  public BeanMetadata getParent() {
    return getParent(null);
  }

  public AnnotationMetadata getAnnotation(String propertyName, String annotationName) {
    return null;
  }

  public AnnotationMetadata getAnnotation(String propertyName) {
    return null;
  }

  public FieldMetadata getField(String fieldName) throws NoSuchFieldException {
    return Optional.ofNullable(fields.get(fieldName)).orElseThrow(NoSuchFieldException::new);
  }

  /**
   * 添加字段，内部方法，用户请勿使用
   * @param fieldName 字段名
   * @param fieldMetadata FieldMetadata对象
   */
  public void addFieldInternal(String fieldName, FieldMetadata fieldMetadata) {
    fields.put(fieldName, fieldMetadata);
  }

  /**
   * 添加getter方法，内部方法，用户请勿使用
   * @param methodName 方法名
   * @param methodMetadata MethodMetadata对象
   */
  public void addMethodInternal(String methodName, MethodMetadata methodMetadata) {
    getterMethods.put(methodName, methodMetadata);
  }
}