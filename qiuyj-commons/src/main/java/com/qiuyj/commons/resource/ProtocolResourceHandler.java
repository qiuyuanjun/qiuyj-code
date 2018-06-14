package com.qiuyj.commons.resource;

import com.qiuyj.commons.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.qiuyj.commons.resource.ClassSeeker.JAR_PATH_SEPERATOR;
import static com.qiuyj.commons.resource.ClassSeeker.pathToUrl;

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
          else if (path.toString().endsWith(".class")) {
            fileConsumer.accept(path);
          }
        });
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while listing files in directory: " + basePath + ".\nCaused by: " + e, e);
      }
    }
  }

  /**
   * 处理{@code jar}协议
   */
  class JarProtocolResourceHandler implements ProtocolResourceHandler {

    @Override
    public void resolveProtocolResourse(URL url, Consumer<Path> fileConsumer) {
      JarFile jarFile = getJarFileFromUrl(url);
      String basePackage = StringUtils.substringAfter(url.getFile(), JAR_PATH_SEPERATOR);
      String jarPath = StringUtils.substringBefore(url.getFile(), JAR_PATH_SEPERATOR).substring(6);
      jarFile.stream()
          .filter(jarEntry -> jarEntry.getName().startsWith(basePackage) && jarEntry.getName().endsWith(".class") && !jarEntry.isDirectory())
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
