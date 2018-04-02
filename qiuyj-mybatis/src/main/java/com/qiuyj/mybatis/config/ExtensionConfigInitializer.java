package com.qiuyj.mybatis.config;

/**
 * @author qiuyj
 * @since 2018/4/2
 */
@FunctionalInterface
public interface ExtensionConfigInitializer<T> {

  void initExtensionConfig(T extensionConfig);
}
