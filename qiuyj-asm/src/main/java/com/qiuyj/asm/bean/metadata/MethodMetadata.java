package com.qiuyj.asm.bean.metadata;

import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class MethodMetadata extends AbstractMetadata {

  private final BeanMetadata declaredBean;

  private final boolean accessible;

  private final String methodName;

  private final String mappingFieldName;

  public MethodMetadata(BeanMetadata declaredBean, int access, String methodName, String mappingFieldName) {
    this.declaredBean = declaredBean;
    accessible = (access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) == 0;
    this.methodName = methodName;
    this.mappingFieldName = mappingFieldName;
  }

  public BeanMetadata getDeclaredBean() {
    return declaredBean;
  }
}
