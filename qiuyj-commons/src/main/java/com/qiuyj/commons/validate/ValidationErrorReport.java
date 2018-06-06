package com.qiuyj.commons.validate;

/**
 * @author qiuyj
 * @since 2018-05-31
 */
public interface ValidationErrorReport {

  /**
   * 验证通过，默认
   */
  ValidationErrorReport HAS_NO_ERROR = new ValidationErrorReport() {

    @Override
    public String toString() {
      return "HAS_NO_ERROR";
    }

    @Override
    public void forEachError(ErrorConsumer errorConsumer) {
      throw new IllegalStateException("Has no error, cannot invoke this method");
    }
  };

  /**
   * 输出对应的错误信息
   * @return 错误信息
   */
  @Override
  String toString();

  /**
   * 遍历所有的错误的信息，至于怎么消费这些错误信息，由具体的子类实现
   * @param errorConsumer 错误信息的消费者
   */
  void forEachError(ErrorConsumer errorConsumer);

  /**
   * 错误消费的顶级接口，如果子类需要自定义消费方法，那么一定要继承该接口
   * 然后在用户自定义的消费接口里面声明对应的消费方法
   */
  interface ErrorConsumer {
  }
}
