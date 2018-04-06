package com.qiuyj.mybatis.config;

import com.qiuyj.commons.ClassUtils;
import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.StringUtils;
import com.qiuyj.mybatis.checker.CheckerChain;
import com.qiuyj.mybatis.checker.ConditionChecker;
import com.qiuyj.mybatis.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 解析通用mapper配置的工具类
 * @author qiuyj
 * @since 2017/11/11
 */
public final class SqlGeneratorConfig {

  /**
   * 数据库类型
   */
  private static final String DATABASE_TYPE_KEY = "databaseType";

  /**
   * mapper基类，用户可以自定义mapper基类，然后通过这个key指定就可以了
   */
  private static final String BASE_MAPPER_CLASS_NAME_KEY = "baseMapperClass";

  /**
   * 通用mapper接口包路径
   */
  private static final String MAPPER_PACKAGE_SCAN_PATH_KEY = "mapperPackageScanPath";

  /**
   * 用户自定义的检查器
   */
  private static final String CONDITION_CHECKERS_KEY = "onditionCheckers";

  private String databaseType;  // 默认mysql数据库

  private Class<? extends Mapper> baseMapperClass; // 默认com.qiuyj.mybatis.mapper.Mapper

  private final CheckerChain chain = new CheckerChain();  // 检查器链

  /**
   * mapper类扫描路径
   */
  private String mapperPackageScanPath;

  private SqlGeneratorConfig() {
  }

  /**
   * 初始化配置信息
   */
  public static SqlGeneratorConfig init(Properties prop) {
    SqlGeneratorConfig config = new SqlGeneratorConfig();
    parseDatabaseType(config, prop);
    parseBaseMapperClass(config, prop);
    parseConditionCheckers(config, prop);
    parseEntityPackageScanPath(config, prop);
    return config;
  }

  private static void parseEntityPackageScanPath(SqlGeneratorConfig config, Properties prop) {
    config.mapperPackageScanPath = prop.getProperty(MAPPER_PACKAGE_SCAN_PATH_KEY);
  }

  /**
   * 解析数据库类型
   */
  private static void parseDatabaseType(SqlGeneratorConfig config, Properties prop) {
    String db = prop.getProperty(DATABASE_TYPE_KEY);
    // 默认是mysql数据库
    config.databaseType = StringUtils.isBlank(db) ? "MYSQL" : db;
  }

  /**
   * 解析baseMapperClass
   */
  @SuppressWarnings("unchecked")
  private static void parseBaseMapperClass(SqlGeneratorConfig config, Properties prop) {
    String baseMapperClassName = prop.getProperty(BASE_MAPPER_CLASS_NAME_KEY);
    if (StringUtils.isNotBlank(baseMapperClassName)
        && !"com.qiuyj.mybatis.mapper.Mapper".equals(baseMapperClassName)) {
      Class<?> cls = ClassUtils.resolveClassName(baseMapperClassName, Thread.currentThread().getContextClassLoader());
      if (!Mapper.class.isAssignableFrom(cls)) {
        throw new IllegalStateException("Only support Mapper's subclass");
      }
      else {
        config.baseMapperClass = (Class<? extends Mapper>) cls;
      }
    }
    else {
      config.baseMapperClass = Mapper.class;
    }
  }

  /**
   * 解析检查器配置
   */
  private static void parseConditionCheckers(SqlGeneratorConfig config, Properties prop) {
    String checkers = prop.getProperty(CONDITION_CHECKERS_KEY);
    if (StringUtils.isNotBlank(checkers)) {
      String[] checkerArr = StringUtils.delimiteToStringArray(checkers, ", \t:");
      List<ConditionChecker> unsortedChecker = new ArrayList<>(checkerArr.length);
      for (String checker : checkerArr) {
        unsortedChecker.add(ReflectionUtils.instantiateClass(checker));
      }
      config.chain.addAndSortCheckers(unsortedChecker);
    }
  }

  public String getDatabaseType() {
    return databaseType;
  }

  public Class<? extends Mapper> getBaseMapperClass() {
    return baseMapperClass;
  }

  public CheckerChain getCheckerChain() {
    return chain;
  }

  public String getMapperPackageScanPath() {
    return mapperPackageScanPath;
  }
}