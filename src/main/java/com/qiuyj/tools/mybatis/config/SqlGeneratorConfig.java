package com.qiuyj.tools.mybatis.config;

import com.qiuyj.tools.ClassUtils;
import com.qiuyj.tools.mybatis.mapper.Mapper;

import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * @author qiuyj
 * @since 2017/11/11
 */
public final class SqlGeneratorConfig {
  private static final String BASE_MAPPER_CLASS_NAME = "com.qiuyj.commons.mybatis.mapper.Mapper";
  private Database databaseType;  // 默认mysql数据库
  private Class<? extends Mapper> baseMapperClass = Mapper.class;

  private SqlGeneratorConfig() {}

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
    return config;
  }

  public Database getDatabaseType() {
    return databaseType;
  }

  public Class<? extends Mapper> getBaseMapperClass() {
    return baseMapperClass;
  }
}