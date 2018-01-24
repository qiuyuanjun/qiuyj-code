package com.qiuyj.commons.bean.parse.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class AnnotationMetadataVisitor extends AnnotationVisitor {

  public AnnotationMetadataVisitor() {
    super(Opcodes.ASM5);
  }
}