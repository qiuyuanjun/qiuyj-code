package com.qiuyj.commons.bean.parse.visitor;

import org.objectweb.asm.*;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class BeanMetadataVisitor extends ClassVisitor {

  private String className;

  private boolean isAbstract;

  private boolean isClass;

  public BeanMetadataVisitor() {
    super(Opcodes.ASM5);
  }

  @Override
  public void visit(int version, // jdk版本
                    int access,  // 访问标志
                    String name, // 类名
                    String signature, // 类的签名
                    String superName, // 父类名
                    String[] interfaces) {
    // 首先判断当前解析的是否是接口，如果是接口，那么直接抛出异常
    if ((access & Opcodes.ACC_INTERFACE) != 0) {
      throw new IllegalStateException("Interface not supported yet");
    }
    else {

    }
  }

  @Override
  public void visitSource(String source, String debug) {
    super.visitSource(source, debug);
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    super.visitOuterClass(owner, name, desc);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return super.visitAnnotation(desc, visible);
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public void visitAttribute(Attribute attr) {
    super.visitAttribute(attr);
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    super.visitInnerClass(name, outerName, innerName, access);
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    return super.visitField(access, name, desc, signature, value);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  @Override
  public void visitEnd() {
    super.visitEnd();
  }
}