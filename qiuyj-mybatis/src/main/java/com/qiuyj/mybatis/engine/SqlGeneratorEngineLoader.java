package com.qiuyj.mybatis.engine;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.mybatis.MapperMethodResolver;
import com.qiuyj.mybatis.checker.CheckerChain;
import com.qiuyj.mybatis.sqlbuild.SqlProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author qiuyj
 * @since 2018/4/1
 */
public final class SqlGeneratorEngineLoader {

  private static final String LOAD_FILE_NAME = "META-INF/com.qiuyj.mybatis.engine.SqlGeneratorEngine";

  private static final Class<?>[] CONSTRUCTOR_ARGS_TYPE = {
      CheckerChain.class,
      SqlProvider.class,
      MapperMethodResolver.class
  };

  private static Map<String, String> allSupportedSqlGeneratorEngine;

  private static volatile SqlGeneratorEngine sqlGeneratorEngineInstance;

  public static SqlGeneratorEngine load(String dbType, ClassLoader clsToUse, Object... args) {
    if (Objects.nonNull(allSupportedSqlGeneratorEngine)) {
      if (Objects.isNull(sqlGeneratorEngineInstance)) {
        String clsStr = allSupportedSqlGeneratorEngine.get(dbType.toUpperCase(Locale.ENGLISH));
        initSqlGeneratorEngineInstance0(clsStr, dbType, clsToUse, args);
        return sqlGeneratorEngineInstance;
      }
      else {
        return sqlGeneratorEngineInstance;
      }
    }
    else {
      Properties prop = new Properties();
      try {
        parse(prop, clsToUse);
      }
      catch (IOException e) {
        throw new IllegalStateException("Error while parsing file: " + LOAD_FILE_NAME + "\nCaused by: " + e, e);
      }
      initSqlGeneratorEngine(prop, dbType, clsToUse, args);
      return sqlGeneratorEngineInstance;
    }
  }

  public static SqlGeneratorEngine load(String dbType, Object... args) {
    return load(dbType, SqlGeneratorEngineLoader.class.getClassLoader(), args);
  }

  private static void initSqlGeneratorEngine(Properties prop, String dbType, ClassLoader clsToUse, Object... args) {
    allSupportedSqlGeneratorEngine = new HashMap<>(prop.size());
    for (String s : prop.stringPropertyNames()) {
      allSupportedSqlGeneratorEngine.put(s.toUpperCase(Locale.ENGLISH), prop.getProperty(s));
    }
    String sqlGeneratorEngineStr = allSupportedSqlGeneratorEngine.get(dbType.toUpperCase(Locale.ENGLISH));
    initSqlGeneratorEngineInstance0(sqlGeneratorEngineStr, dbType, clsToUse, args);
  }

  @SuppressWarnings("unchecked")
  private static void initSqlGeneratorEngineInstance0(String sqlGeneratorEngineStr, String dbType, ClassLoader clsToUse, Object... args) {
    if (Objects.isNull(sqlGeneratorEngineStr)) {
      throw new IllegalStateException("Unsupported database types: " + dbType.toUpperCase(Locale.ENGLISH) + "\nThe currently supported database type is: " + allSupportedSqlGeneratorEngine.keySet());
    } else {
      Class<?> cls;
      try {
        cls = Objects.isNull(clsToUse) ? Class.forName(sqlGeneratorEngineStr) : clsToUse.loadClass(sqlGeneratorEngineStr);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Can not found class: " + sqlGeneratorEngineStr);
      }
      if (!SqlGeneratorEngine.class.isAssignableFrom(cls)) {
        throw new IllegalStateException("Only support SqlGeneratorEngine's subclass");
      } else {
        sqlGeneratorEngineInstance = ReflectionUtils.instantiateClass((Class<? extends SqlGeneratorEngine>) cls, args, CONSTRUCTOR_ARGS_TYPE);
      }
    }
  }
  private static void parse(Properties prop, ClassLoader clsToUse) throws IOException {
    Enumeration<URL> resources = Objects.isNull(clsToUse) ?
        ClassLoader.getSystemResources(LOAD_FILE_NAME) :
        clsToUse.getResources(LOAD_FILE_NAME);
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      if (Objects.nonNull(resource)) {
        InputStream in = null;
        try {
          in = resource.openStream();
          prop.load(in);
        }
        finally {
          if (Objects.nonNull(in)) {
            in.close();
          }
        }
      }
    }
  }
}