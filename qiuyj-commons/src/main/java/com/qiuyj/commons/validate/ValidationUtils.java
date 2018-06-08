package com.qiuyj.commons.validate;

import com.qiuyj.commons.cache.Cache;
import com.qiuyj.commons.cache.impl.SoftReferenceCache;
import com.qiuyj.commons.validate.impl.BeanValidator;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-01
 */
public abstract class ValidationUtils {

  private static final Cache<Class<?>, Validator<?>> VALIDATOR_CACHE = new SoftReferenceCache<>();

  @SuppressWarnings("unchecked")
  public static <T> Validator<T> getBeanValidator(Class<T> cls) {
    Validator<T> validator = (Validator<T>) VALIDATOR_CACHE.getValue(cls);
    if (Objects.isNull(validator)) {
      validator = new BeanValidator<>(cls);
      VALIDATOR_CACHE.setValue(cls, validator);
    }
    return validator;
  }
}
