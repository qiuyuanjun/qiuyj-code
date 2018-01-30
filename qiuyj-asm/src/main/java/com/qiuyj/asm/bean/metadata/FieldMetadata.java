package com.qiuyj.asm.bean.metadata;

import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class FieldMetadata extends AbstractMetadata {

  private final BeanMetadata declaredBean;

  private final boolean accessible;

  private final String fieldName;

  public FieldMetadata(BeanMetadata declaredBean, int access, String fieldName) {
    this.declaredBean = declaredBean;
    accessible = (access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) == 0;
    this.fieldName = fieldName;
  }

  public BeanMetadata getDeclaredBean() {
    return declaredBean;
  }

  public boolean isAccessible() {
    return accessible;
  }

  public String getName() {
    return fieldName;
  }
}