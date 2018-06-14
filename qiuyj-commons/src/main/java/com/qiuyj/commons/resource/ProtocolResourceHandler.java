package com.qiuyj.commons.resource;

import com.qiuyj.commons.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;

import static com.qiuyj.commons.resource.ClassSeeker.CLASSPATH_SEPERATOR;
import static com.qiuyj.commons.resource.ClassSeeker.JAR_PATH_SEPERATOR;
import static com.qiuyj.commons.resource.ClassSeeker.pathToUrl;
import static com.qiuyj.commons.resource.ProtocolResourceHandler.FileProtocolResourceHandler.isModuleOrPackageInfoClassFile;

/**
 * @author qiuyj
 * @since 2018-06-14
 */
public interface ProtocolResourceHandler {

  void resolveProtocolResourse(URL url, Consumer<Path> fileConsumer);

  /**
   * 处理{@code file}协议
   */
  class FileProtocolResourceHandler implements ProtocolResourceHandler {

    private static final String[] EXCLUDE_FILE = {
        "module-info.class",
        "package-info.class"
    };

    @Override
    public void resolveProtocolResourse(URL url, Consumer<Path> fileConsumer) {
      Path basePath = new File(url.getFile()).toPath();
      try {
        Files.list(basePath).forEach(path -> {
          if (Files.isDirectory(path)) {
            // 目录，那么递归
            URL sub = pathToUrl(path);
            if (Objects.nonNull(sub)) {
              resolveProtocolResourse(sub, fileConsumer);
            }
          }
          // 不能是module-info.class，也不能是package-info.class
          else if (path.toString().endsWith(".class")
              && !isModuleOrPackageInfoClassFile(path.getFileName().toString())) {
            fileConsumer.accept(path);
          }
        });
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while listing files in directory: " + basePath + ".\nCaused by: " + e, e);
      }
    }

    /**
     * 判断一个文件是否是module-info.class文件或者是package-info.class文件
     * @param filename 文件名
     * @return {@code true}是，否则不是
     */
    public static boolean isModuleOrPackageInfoClassFile(String filename) {
      for (String s : EXCLUDE_FILE) {
        if (s.equals(filename)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * 处理{@code jar}协议
   */
  class JarProtocolResourceHandler implements ProtocolResourceHandler {

    @Override
    public void resolveProtocolResourse(URL url, Consumer<Path> fileConsumer) {
      JarFile jarFile = getJarFileFromUrl(url);
      String basePackage = StringUtils.substringAfter(url.getFile(), JAR_PATH_SEPERATOR),
             jarPath = StringUtils.substringBefore(url.getFile(), JAR_PATH_SEPERATOR).substring(6);
      jarFile.stream().filter(jarEntry -> {
            boolean firstCondition = jarEntry.getName().startsWith(basePackage)
                && jarEntry.getName().endsWith(".class")
                && !jarEntry.isDirectory();
            if (firstCondition) {
              int idx = jarEntry.getName().lastIndexOf(CLASSPATH_SEPERATOR);
              if (idx > -1) {
                String filename = jarEntry.getName().substring(idx + 1);
                return !isModuleOrPackageInfoClassFile(filename);
              }
              else {
                return true;
              }
            }
            else {
              return false;
            }
          })
          .forEach(jarEntry ->
            fileConsumer.accept(Paths.get(jarPath + JAR_PATH_SEPERATOR + jarEntry.getName())));
      try {
        jarFile.close();
      }
      catch (IOException e) {
        // ignore
      }
    }

    private static JarFile getJarFileFromUrl(URL url) {
      JarFile jarFile;
      try {
        jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while opening connection with jar.\nCaused by: " + e, e);
      }
      return jarFile;
    }
  }
}
