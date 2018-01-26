package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import org.objectweb.asm.AnnotationVisitor;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class AnnotationMetadataVisitor extends AnnotationVisitor {

  public AnnotationMetadataVisitor() {
    super(ASMVersion.ASM_VERSION);
  }
}