package com.qiuyj.commons.resource;

import com.qiuyj.commons.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author qiuyj
 * @since 2018-06-14
 */
public final class ClassSeeker {

  private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

  public static final String CLASSPATH_SEPERATOR = "/";

  public static final String JAR_PATH_SEPERATOR = "!/";

  /** 类加载器，用于加载找到的类文件，允许为null */
  private ClassLoader classLoader;

  private Predicate<Class<?>> ifCondition;

  public ClassSeeker() {
    this(ClassUtils.getDefaultClassLoader());
  }

  public ClassSeeker(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * 查找给定包名下的所有的类，包括jar包中的
   * @param packageName 包名
   * @return 找到的{{@code Class}数组
   */
  public Class<?>[] seekClasses(String packageName) {
    String resolvedPath = convertPackageNameToClasspath(packageName);
    if (Objects.isNull(resolvedPath)) {
      throw new IllegalArgumentException("Package name cannot be null.");
    }
    else {
      ClassLoader loader = Objects.isNull(classLoader) ? ClassLoader.getSystemClassLoader() : classLoader;
      Enumeration<URL> resourceUrls;
      try {
        resourceUrls = loader.getResources(resolvedPath);
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while getting resources in classpath: " + resolvedPath + ".\nCaused by: " + e, e);
      }
      String basePackage = "".equals(resolvedPath) ? "" : resolvedPath.replace(CLASSPATH_SEPERATOR, ".") + ".";
      Set<Class<?>> findedClasses = findClasses(basePackage, resourceUrls, loader, ifCondition);
      return findedClasses.isEmpty() ? EMPTY_CLASSES : findedClasses.toArray(EMPTY_CLASSES);
    }
  }

  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public void setIfCondition(Predicate<Class<?>> ifCondition) {
    this.ifCondition = ifCondition;
  }

  /**
   * 将包名转换成{@code ClassLoader}可以识别的类路径
   * @param packageName 包名
   * @return 对应的类路径
   */
  private static String convertPackageNameToClasspath(String packageName) {
    if (Objects.isNull(packageName)) {
      return null;
    }
    else {
      while (packageName.startsWith(CLASSPATH_SEPERATOR)) {
        packageName = packageName.substring(1);
      }
      return packageName.replace(".", CLASSPATH_SEPERATOR);
    }
  }

  /**
   * 根据url所代表的目录递归查找{@code Class}对象
   * @param urls 资源路径
   * @param ifCondition {@code Class}匹配表达式
   * @return 满足要求的{{@code Class}集合
   */
  private static Set<Class<?>> findClasses(String basePackage, Enumeration<URL> urls, ClassLoader loader, Predicate<Class<?>> ifCondition) {
    Set<Class<?>> classes = new HashSet<>();
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      ProtocolResourceHandler handler = ProtocolResourceHandlerFactory.getProtocolResourceHandler(url.getProtocol());
      if (Objects.nonNull(handler)) {
        Path basePath = urlToPath(url);
        handler.resolveProtocolResourse(url, path -> {
          String classPathStr = basePath.relativize(path).toString().replace(File.separator, ".");
          String className = basePackage + classPathStr.substring(0, classPathStr.length() - 6);
          Class<?> beanClass = ClassUtils.resolveClassName(className, loader);
          if (Objects.isNull(ifCondition) || ifCondition.test(beanClass)) {
            classes.add(beanClass);
          }
        });
      }
      else {
        throw new IllegalStateException("Unknow protocol to handle: " + url.getProtocol());
      }
    }
    return classes;
  }

  private static Path urlToPath(URL url) {
    Path path = null;
    URI uri = null;
    try {
      uri = url.toURI();
    }
    catch (URISyntaxException e) {
      // ignore, never happened
    }
    if (Objects.nonNull(uri)) {
      try {
        path = Paths.get(uri);
      }
      catch (FileSystemNotFoundException e) {
        // 有可能是jar
        path = Paths.get(url.getFile().substring(6));
      }
    }
    return path;
  }

  static URL pathToUrl(Path path) {
    URL url = null;
    try {
      url = path.toUri().toURL();
    }
    catch (MalformedURLException e) {
      // ignore, never happened
    }
    return url;
  }
}
