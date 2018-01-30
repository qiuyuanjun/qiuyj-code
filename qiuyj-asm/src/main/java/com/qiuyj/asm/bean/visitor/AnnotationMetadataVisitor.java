package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import com.qiuyj.asm.bean.metadata.AbstractMetadata;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class AnnotationMetadataVisitor extends AnnotationVisitor {

  private final AbstractMetadata belongTo;

  private final String annotationClassName;

  public AnnotationMetadataVisitor(AbstractMetadata belongTo, String annotationClassName) {
    super(ASMVersion.ASM_VERSION);
    this.belongTo = belongTo;
    this.annotationClassName = annotationClassName;
  }

  @Override
  public void visit(String attributeName, Object attributeValue) {

  }

  @Override
  public void visitEnum(String s, String s1, String s2) {
    super.visitEnum(s, s1, s2);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String s, String s1) {
    return super.visitAnnotation(s, s1);
  }

  @Override
  public AnnotationVisitor visitArray(String s) {
    return super.visitArray(s);
  }

  @Override
  public void visitEnd() {
    super.visitEnd();
  }
}