package com.qiuyj.tools.mybatis.config;

import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.StringUtils;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.checker.ConditionChecker;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/11
 */
public final class SqlGeneratorConfig {
  private static final String BASE_MAPPER_CLASS_NAME = "com.qiuyj.commons.mybatis.mapper.Mapper";
  private Database databaseType;  // 默认mysql数据库
  private Class<? extends Mapper> baseMapperClass = Mapper.class;
  private final CheckerChain chain = new CheckerChain();

  private SqlGeneratorConfig() {
  }

  @SuppressWarnings("unchecked")
  public static SqlGeneratorConfig init(Properties prop) {
    SqlGeneratorConfig config = new SqlGeneratorConfig();
    String db = prop.getProperty("databaseType");
    if (Objects.nonNull(db)) {
      try {
        config.databaseType = Database.valueOf(db.toUpperCase(Locale.US));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Unsupported database type yet", e);
      }
    }
    String baseMapperClassName = prop.getProperty("baseMapperClass", BASE_MAPPER_CLASS_NAME);
    if (!BASE_MAPPER_CLASS_NAME.equals(baseMapperClassName))
      config.baseMapperClass = (Class<? extends Mapper>) ClassUtils.resolveClassName(baseMapperClassName, null);
    String checkers = prop.getProperty("conditionCheckers");
    if (Objects.nonNull(checkers)) {
      String[] checkerArr = StringUtils.delimiteToStringArray(checkers, ", \t:");
      if (checkerArr != StringUtils.EMPTY_STRING_ARRAY) {
        List<ConditionChecker> unsortedChecker = new ArrayList<>(checkerArr.length);
        for (String checker : checkerArr) {
          unsortedChecker.add(ReflectionUtils.instantiate(checker));
        }
        config.chain.addCheckerUnsorted(unsortedChecker);
      }
    }
    return config;
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
}