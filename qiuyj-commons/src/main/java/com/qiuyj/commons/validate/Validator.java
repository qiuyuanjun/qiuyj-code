package com.qiuyj.commons.validate;

/**
 * 验证器顶级接口
 * @author qiuyj
 * @since 2018-05-29
 */
public interface Validator<T> {

  /**
   * 对对应类型的对象进行验证，如果验证不通过，那么抛出异常（只要有一个验证不通过，那么就抛出异常）
   * @param validatedObject 要验证的对象
   * @throws ValidationException 验证未通过的时候，抛出该异常
   */
  void validateWithException(T validatedObject) throws ValidationException;

  /**
   * 对对应类型的对象进行验证，返回验证结果
   * @param validatedObject 要验证的对象
   */
  ValidationResult validateWithResult(T validatedObject);

  /**
   * 得到被验证的对象的{@code Class}对象
   */
  Class<? extends T> getValidatedClass();
}
