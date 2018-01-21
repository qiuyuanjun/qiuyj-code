package com.qiuyj.commons.bean;

import com.qiuyj.commons.bean.wrapper.BeanWrapper;
import com.qiuyj.commons.bean.wrapper.BeanWrapperImpl;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/21
 */
@SuppressWarnings("unchecked")
public abstract class BeanUtils {

  public static <T> T objectArrayValueMapToBean(Map<String, Object[]> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach((propertyName, propertyArrayValue) -> {
      if (Objects.nonNull(propertyArrayValue) && propertyArrayValue.length == 1) {
        beanWrapper.setPropertyValue(propertyName, propertyArrayValue[0]);
      }
      else {
        beanWrapper.setPropertyValue(propertyName, propertyArrayValue);
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T objectValueMapToBean(Map<String, Object> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanWrapper.setPropertyValues(beanData);
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T stringArrayValueMapToBean(Map<String, String[]> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach((propertyName, propertyArrayValue) -> {
      if (Objects.nonNull(propertyArrayValue) && propertyArrayValue.length == 1) {
        beanWrapper.convertAndSetPropertyValueString(propertyName, propertyArrayValue[0]);
      }
      else {
        beanWrapper.setPropertyValue(propertyName, propertyArrayValue);
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T stringValueMapToBean(Map<String, String> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach(beanWrapper::convertAndSetPropertyValueString);
    return (T) beanWrapper.getWrappedInstance();
  }
}