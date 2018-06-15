module qiuyj.mybatis {
  requires qiuyj.commons;
  requires mybatis;
  requires java.sql;

  exports com.qiuyj.mybatis;
  exports com.qiuyj.mybatis.checker;
  exports com.qiuyj.mybatis.annotation;
  exports com.qiuyj.mybatis.config;
  exports com.qiuyj.mybatis.engine;
  exports com.qiuyj.mybatis.key;
  exports com.qiuyj.mybatis.mapper;
  exports com.qiuyj.mybatis.sqlbuild;
  exports com.qiuyj.mybatis.sqlbuild.typehandler;
}