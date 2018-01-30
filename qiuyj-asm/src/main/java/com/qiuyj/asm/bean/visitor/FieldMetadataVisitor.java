package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import com.qiuyj.asm.bean.metadata.BeanMetadata;
import com.qiuyj.asm.bean.metadata.FieldMetadata;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class FieldMetadataVisitor extends FieldVisitor {

  private final FieldMetadata fieldMetadata;

  private final String name;

  public FieldMetadataVisitor(BeanMetadata belongTo, int access, String name) {
    super(ASMVersion.ASM_VERSION);
    this.fieldMetadata = new FieldMetadata(belongTo, access, name);
    this.name = name;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String annotationDesc, boolean visible) {
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int i, TypePath typePath, String s, boolean b) {
    return super.visitTypeAnnotation(i, typePath, s, b);
  }

  @Override
  public void visitAttribute(Attribute attribute) {
    // no-op
  }

  @Override
  public void visitEnd() {
    BeanMetadata bean = fieldMetadata.getDeclaredBean();
    bean.addFieldInternal(name, fieldMetadata);
  }
}