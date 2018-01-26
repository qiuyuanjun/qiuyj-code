package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import org.objectweb.asm.FieldVisitor;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class FieldMetadataVisitor extends FieldVisitor {

  public FieldMetadataVisitor() {
    super(ASMVersion.ASM_VERSION);
  }
}