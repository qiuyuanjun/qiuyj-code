package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import org.objectweb.asm.MethodVisitor;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class MethodMetadataVisitor extends MethodVisitor {

  public MethodMetadataVisitor() {
    super(ASMVersion.ASM_VERSION);
  }
}
