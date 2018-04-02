package com.qiuyj.mybatis.config;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qiuyj
 * @since 2018/4/2
 */
public class MetaInfExtensionConfigLoader {

  static final String META_INF_PREFIX = "META-INF/";

  private static final ConcurrentMap<Class<?>, ExtensionConfig<?>> EXTENSION_CONFIG_MAP = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> ExtensionConfig<T> loadExtensionConfig(Class<T> extensionConfigCls) {
    if (Objects.isNull(extensionConfigCls) || !extensionConfigCls.isInterface()) {
      throw new IllegalArgumentException("Extension config is null or not an interface: " + extensionConfigCls);
    }
    else {
      ExtensionConfig<?> rs;
      if (EXTENSION_CONFIG_MAP.containsKey(extensionConfigCls)) {
        rs = EXTENSION_CONFIG_MAP.get(extensionConfigCls);
      }
      else {
        EXTENSION_CONFIG_MAP.putIfAbsent(extensionConfigCls, new ExtensionConfig<>(extensionConfigCls));
        rs = EXTENSION_CONFIG_MAP.get(extensionConfigCls);
      }
      return (ExtensionConfig<T>) rs;
    }
  }
}