package com.qiuyj.commons.bean.parse.visitor;

import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class MethodMetadataVisitor extends MethodVisitor {

  public MethodMetadataVisitor() {
    super(Opcodes.ASM5);
  }
}
