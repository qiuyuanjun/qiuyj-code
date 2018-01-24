package com.qiuyj.commons.bean.parse.visitor;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class FieldMetadataVisitor extends FieldVisitor {

  public FieldMetadataVisitor() {
    super(Opcodes.ASM5);
  }
}