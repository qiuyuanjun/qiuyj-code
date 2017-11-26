package com.qiuyj.tools.mybatis.build;

/**
 * 当一个SqlInfo解析没有主键但是却调用了根据主键查询或者删除的方法时候抛出
 * @author qiuyj
 * @since 2017/11/26
 */
public class NoPrimaryKeyException extends RuntimeException {
  public NoPrimaryKeyException() {
    super();
  }

  public NoPrimaryKeyException(String message) {
    super(message);
  }

  public NoPrimaryKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoPrimaryKeyException(Throwable cause) {
    super(cause);
  }

  protected NoPrimaryKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
