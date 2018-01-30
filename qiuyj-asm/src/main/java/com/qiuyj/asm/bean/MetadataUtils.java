package com.qiuyj.asm.bean;

import com.qiuyj.asm.bean.metadata.BeanMetadata;
import com.qiuyj.asm.bean.visitor.BeanMetadataVisitor;
import com.qiuyj.commons.cache.Cache;
import com.qiuyj.commons.cache.impl.SoftReferenceCache;
import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/26
 */
public abstract class MetadataUtils {

  private static final Cache<ClassNameClassLoader, BeanMetadataVisitor> CACHED_BEAN_METADATA_VISITOR = new SoftReferenceCache<>();

  public static BeanMetadata newBeanMetadata(String className, ClassLoader loader) throws IOException {
    ClassNameClassLoader cncl = new ClassNameClassLoader(className, loader);
    BeanMetadataVisitor visitor = CACHED_BEAN_METADATA_VISITOR.getValue(cncl);
    if (Objects.isNull(visitor)) {
      ClassReader cr = new ClassReader(className);
      visitor = new BeanMetadataVisitor(loader);
      cr.accept(visitor, 0);
      CACHED_BEAN_METADATA_VISITOR.setValue(cncl, visitor);
    }
    return visitor.getBeanMetadata();
  }

  private static final class ClassNameClassLoader {

    String className;

    ClassLoader classLoader;

    ClassNameClassLoader(String className, ClassLoader classLoader) {
      this.className = className;
      this.classLoader = classLoader;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ClassNameClassLoader that = (ClassNameClassLoader) o;
      return Objects.equals(className, that.className) &&
          Objects.equals(classLoader, that.classLoader);
    }

    @Override
    public int hashCode() {
      return Objects.hash(className, classLoader);
    }
  }
}