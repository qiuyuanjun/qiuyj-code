package com.qiuyj.commons.bean;

/**
 * 反射异常
 * @author qiuyj
 * @since 2018/1/3
 */
public class ReflectionException extends RuntimeException {

  public ReflectionException() {
    super();
  }

  public ReflectionException(String message) {
    super(message);
  }

  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionException(Throwable cause) {
    super(cause);
  }

  protected ReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
