package com.qiuyj.classreading;

import com.qiuyj.classreading.metadata.ClassMetadata;

/**
 * @author qiuyj
 * @since 2018/3/4
 */
public interface ClassMetadataReader {

  /**
   * 读取给定的类
   * @param className 类全名
   */
  void readClass(String className);

  /**
   * 读取给定的类
   * @param cls 类Class对象
   */
  void readClass(Class<?> cls);

  /**
   * 得到给定的类的元信息对象
   * @return ClassMetadata对象
   */
  ClassMetadata getMetadata();
}
