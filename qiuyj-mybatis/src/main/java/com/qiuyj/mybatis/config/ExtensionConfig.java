package com.qiuyj.mybatis.config;

import com.qiuyj.commons.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author qiuyj
 * @since 2018/4/2
 */
public class ExtensionConfig<T> {

  private static Map<String, String> allSupportedExtensionNames;

  private T activatedInstance;

  private Class<T> extensionConfigInterface;

  private volatile boolean initialized;

  public ExtensionConfig(Class<T> extensionConfigInterface) {
    this.extensionConfigInterface = extensionConfigInterface;
  }

  public T getActivatedInstance(String dbType) {
    return getActivatedInstance(dbType, null);
  }

  public T getActivatedInstance(String dbType, ExtensionConfigInitializer<T> initializer) {
    if (Objects.nonNull(activatedInstance)) {
      return activatedInstance;
    }
    boolean needInitialization = Objects.nonNull(initializer) && !initialized;
    if (Objects.nonNull(allSupportedExtensionNames)) {
      String clsStr = allSupportedExtensionNames.get(dbType.toUpperCase(Locale.ENGLISH));
      initInstance(clsStr, needInitialization, initializer, dbType);
    }
    else {
      String resourceLocation = MetaInfExtensionConfigLoader.META_INF_PREFIX + extensionConfigInterface.getName();
      try {
        parse(resourceLocation, extensionConfigInterface.getClassLoader());
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while parsing file: " + resourceLocation + ".\nCaused by: " + e, e);
      }
      String clsStr = allSupportedExtensionNames.get(dbType.toUpperCase(Locale.ENGLISH));
      initInstance(clsStr, needInitialization, initializer, dbType);
    }
    return activatedInstance;
  }

  public Class<T> getExtensionConfigInterface() {
    return extensionConfigInterface;
  }

  @SuppressWarnings("unchecked")
  private void initInstance(String clsStr, boolean needInitialization, ExtensionConfigInitializer<T> initializer, String dbType) {
    if (Objects.isNull(clsStr)) {
      throw new IllegalStateException("Unsupported database type: " + dbType.toUpperCase(Locale.ENGLISH) + "\nThe currently supported database type is: " + allSupportedExtensionNames.keySet());
    }
    else {
      ClassLoader clsToUse = extensionConfigInterface.getClassLoader();
      Class<T> cls;
      try {
        cls = (Class<T>) (Objects.isNull(clsToUse) ? Class.forName(clsStr) : clsToUse.loadClass(clsStr));
      }
      catch (ClassNotFoundException e) {
        throw new IllegalStateException("Can not find extension config interface: " + extensionConfigInterface + "'s subclass: " + clsStr);
      }
      activatedInstance = ReflectionUtils.instantiateClass(cls);
      if (needInitialization) {
        initializer.initExtensionConfig(activatedInstance);
        initialized = true;
      }
    }
  }

  private static void parse(String resourceLocation, ClassLoader clsToUse) throws IOException {
    Properties prop = new Properties();
    Enumeration<URL> resourceUrls = Objects.isNull(clsToUse) ? ClassLoader.getSystemResources(resourceLocation) : clsToUse.getResources(resourceLocation);
    while (resourceUrls.hasMoreElements()) {
      URL resourceUrl = resourceUrls.nextElement();
      if (Objects.nonNull(resourceUrl)) {
        InputStream in = null;
        try {
          in = resourceUrl.openStream();
          prop.load(in);
        }
        finally {
          if (Objects.nonNull(in)) {
            in.close();
          }
        }
      }
    }
    allSupportedExtensionNames = new HashMap<>(prop.size());
    for (String s : prop.stringPropertyNames()) {
      allSupportedExtensionNames.put(s.toUpperCase(Locale.ENGLISH), prop.getProperty(s));
    }
  }
}