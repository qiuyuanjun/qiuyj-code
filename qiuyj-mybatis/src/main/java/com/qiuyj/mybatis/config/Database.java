package com.qiuyj.mybatis.config;

/**
 * 数据库类型，目前仅仅支持MYSQL和ORACLE两种数据库
 * @author qiuyj
 * @since 2017/11/11
 */

/*
 * 已经移除，不再使用，这样限制了用户自己扩展数据库支持
 */
@Deprecated
public enum Database {
  MYSQL, ORACLE
}