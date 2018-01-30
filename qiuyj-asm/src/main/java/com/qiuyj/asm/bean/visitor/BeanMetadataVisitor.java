package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import com.qiuyj.asm.bean.metadata.BeanMetadata;
import com.qiuyj.commons.ClassUtils;
import jdk.internal.org.objectweb.asm.*;

import java.beans.Introspector;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class BeanMetadataVisitor extends ClassVisitor {

  private final ClassLoader classLoader;

  private BeanMetadata beanMetadata;

  public BeanMetadataVisitor(ClassLoader classLoader) {
    super(ASMVersion.ASM_VERSION);
    this.classLoader = Objects.isNull(classLoader) ? ClassUtils.getDefaultClassLoader() : classLoader;
  }

  @Override
  public void visit(int version, // jdk主版本，jdk版本从45开始
                    int access,  // 访问标志
                    String name, // 类名，形如java/lang/String
                    String signature, // 泛型签名
                    String superName, // 父类名，形如java/lang/Object
                    String[] interfaces) {
    // 判断当前解析的是否是接口，如果是接口，那么直接抛出异常
    if ((access & Opcodes.ACC_INTERFACE) != 0) {
      throw new IllegalStateException("Interface not supported yet");
    }
    else {
      String className = name.replace("/", ".");
      try {
        beanMetadata = new BeanMetadata(classLoader.loadClass(className));
      }
      catch (ClassNotFoundException e) {
        throw new IllegalStateException("Class: " + className + " is invisible for class loader: " + classLoader);
      }
      beanMetadata.setParent(superName);
    }
  }

  @Override
  public void visitSource(String source, String debug) {
    // no-op
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    // no-op
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return visible ? new AnnotationMetadataVisitor(beanMetadata, Type.getType(desc).getClassName()) : null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public void visitAttribute(Attribute attr) {
    // no-op
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    // non-op
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    // 如果当前访问的field的类型是static或者static final的，那么直接跳过
    boolean isStaticFinal = (access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) != 0;
    return isStaticFinal ? null : new FieldMetadataVisitor(beanMetadata, access, name);
  }

  @Override
  public MethodVisitor visitMethod(int access,
                                   String name,
                                   String desc,
                                   String signature,
                                   String[] exceptions) {
    // 只处理getter方法
    String methodMappingFieldName = null;
    if (name.startsWith("get")) {
      methodMappingFieldName = Introspector.decapitalize(name.substring(3));
    }
    else if (name.startsWith("is")) {
      methodMappingFieldName = Introspector.decapitalize(name.substring(2));
    }
    return Objects.isNull(methodMappingFieldName) ? null : new MethodMetadataVisitor(beanMetadata, access, name, methodMappingFieldName);
  }

  @Override
  public void visitEnd() {
    // no-op
  }

  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
}