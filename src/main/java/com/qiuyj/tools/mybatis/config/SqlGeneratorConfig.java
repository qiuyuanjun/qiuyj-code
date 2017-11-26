package com.qiuyj.tools.mybatis.config;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.checker.ConditionChecker;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.util.*;

/**
 * 解析通用mapper配置的工具类
 * @author qiuyj
 * @since 2017/11/11
 */
public final class SqlGeneratorConfig {
  private static final String BASE_MAPPER_CLASS_NAME = "com.qiuyj.commons.mybatis.mapper.Mapper";
  private Database databaseType;  // 默认mysql数据库
  private Class<? extends Mapper> baseMapperClass = Mapper.class;
  private final CheckerChain chain = new CheckerChain();  // 检查器链
  private SqlProvider baseSqlProvider; // sql提供类

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
    parseSqlProvider(config);
    return config;
  }

  /**
   * 解析数据库类型
   */
  private static void parseDatabaseType(SqlGeneratorConfig config, Properties prop) {
    String db = prop.getProperty("databaseType");
    if (Objects.nonNull(db)) {
      try {
        config.databaseType = Database.valueOf(db.toUpperCase(Locale.US));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Unsupported database type yet", e);
      }
    } else
      config.databaseType = Database.MYSQL;
  }

  /**
   * 解析baseMapperClass
   */
  @SuppressWarnings("unchecked")
  private static void parseBaseMapperClass(SqlGeneratorConfig config, Properties prop) {
    String baseMapperClassName = prop.getProperty("baseMapperClass", BASE_MAPPER_CLASS_NAME);
    if (!BASE_MAPPER_CLASS_NAME.equals(baseMapperClassName))
      config.baseMapperClass = (Class<? extends Mapper>) ClassUtils.resolveClassName(baseMapperClassName, null);
  }

  /**
   * 解析检查器配置
   */
  private static void parseConditionCheckers(SqlGeneratorConfig config, Properties prop) {
    String checkers = prop.getProperty("conditionCheckers");
    if (Objects.nonNull(checkers)) {
      String[] checkerArr = StringUtils.delimiteToStringArray(checkers, ", \t:");
      if (checkerArr != StringUtils.EMPTY_STRING_ARRAY) {
        List<ConditionChecker> unsortedChecker = new ArrayList<>(checkerArr.length);
        for (String checker : checkerArr) {
          unsortedChecker.add(ReflectionUtils.instantiateClass(checker));
        }
        config.chain.addCheckerUnsorted(unsortedChecker);
      }
    }
  }

  /**
   * 解析sqlProvider
   */
  private static void parseSqlProvider(SqlGeneratorConfig config) {
    Class<?> baseClass = config.getBaseMapperClass();
    com.qiuyj.tools.mybatis.SqlProvider sqlProviderAnno = //
        AnnotationUtils.findAnnotation(baseClass, com.qiuyj.tools.mybatis.SqlProvider.class);
    String sqlProviderStr = "";
    if (Objects.nonNull(sqlProviderAnno))
      sqlProviderStr = sqlProviderAnno.value();
    if ("".equals(sqlProviderStr))
      sqlProviderStr = "com.qiuyj.tools.mybatis.build.SqlProvider";
    config.baseSqlProvider = ReflectionUtils.instantiateClass(sqlProviderStr);
  }

  public Database getDatabaseType() {
    return databaseType;
  }

  public Class<? extends Mapper> getBaseMapperClass() {
    return baseMapperClass;
  }

  public CheckerChain getCheckerChain() {
    return chain;
  }

  public SqlProvider getBaseSqlProvider() {
    return baseSqlProvider;
  }
}