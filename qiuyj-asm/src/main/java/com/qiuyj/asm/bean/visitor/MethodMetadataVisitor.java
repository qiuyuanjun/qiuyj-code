package com.qiuyj.asm.bean.visitor;

import com.qiuyj.asm.ASMVersion;
import com.qiuyj.asm.bean.metadata.BeanMetadata;
import com.qiuyj.asm.bean.metadata.MethodMetadata;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/24
 */
public class MethodMetadataVisitor extends MethodVisitor {

  private final MethodMetadata methodMetadata;

  private final String methodName;

  private boolean ignore;

  public MethodMetadataVisitor(BeanMetadata declaredBean, int access, String name, String mappingFieldName) {
    super(ASMVersion.ASM_VERSION);
    methodMetadata = new MethodMetadata(declaredBean, access, name, mappingFieldName);
    methodName = name;
  }

  @Override
  public void visitParameter(String parameterDesc, int i) {
    ignore = true;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String annotationDesc, boolean visible) {
    return super.visitAnnotation(annotationDesc, visible);
  }

  @Override
  public void visitEnd() {
    if (!ignore) {
      BeanMetadata bean = methodMetadata.getDeclaredBean();
      bean.addMethodInternal(methodName, methodMetadata);
    }
  }
}