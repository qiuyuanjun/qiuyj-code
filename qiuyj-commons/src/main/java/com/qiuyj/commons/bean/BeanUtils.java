package com.qiuyj.commons.bean;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.wrapper.BeanWrapper;
import com.qiuyj.commons.bean.wrapper.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
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
        try {
          beanWrapper.setPropertyValue(propertyName, propertyArrayValue[0]);
        }
        catch (Exception e) {
          // ignore
        }
      }
      else {
        try {
          beanWrapper.setPropertyValue(propertyName, propertyArrayValue);
        }
        catch (Exception e) {
          // ignore
        }
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T objectValueMapToBean(Map<String, Object> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach((propertyName, propertyValue) -> {
      try {
        beanWrapper.setPropertyValue(propertyName, propertyValue);
      }
      catch (Exception e) {
        // ignore
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T stringArrayValueMapToBean(Map<String, String[]> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach((propertyName, propertyArrayValue) -> {
      if (Objects.nonNull(propertyArrayValue) && propertyArrayValue.length == 1) {
        try {
          beanWrapper.convertAndSetPropertyValueString(propertyName, propertyArrayValue[0]);
        }
        catch (Exception e) {
          // ignore
        }
      }
      else {
        try {
          beanWrapper.setPropertyValue(propertyName, propertyArrayValue);
        }
        catch (Exception e) {
          // ignore
        }
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static <T> T stringValueMapToBean(Map<String, String> beanData, Class<T> beanCls) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    beanData.forEach((propertyName, propertyValue) ->{
      try {
        beanWrapper.convertAndSetPropertyValueString(propertyName, propertyValue);
      }
      catch (Exception e) {
        // ignore
      }
    });
    return (T) beanWrapper.getWrappedInstance();
  }

  public static void copyProperties(Object src, Object dest) {
    if (Objects.isNull(src) || Objects.isNull(dest)) {
      throw new NullPointerException();
    }
    else {
      CachedIntrospectorResults srcIntrospectorResults = CachedIntrospectorResults.forClass(src.getClass());
      CachedIntrospectorResults destIntrospectorResults = CachedIntrospectorResults.forClass(dest.getClass());
      PropertyDescriptor[] pds = srcIntrospectorResults.getBeanInfo().getPropertyDescriptors();
      if (Objects.nonNull(pds)) {
        Method readMethod, writeMethod;
        PropertyDescriptor destPd;
        for (PropertyDescriptor srcPd : pds) {
          readMethod = srcPd.getReadMethod();
          if (Objects.nonNull(readMethod) && readMethod.getDeclaringClass() != Object.class) {
            destPd = destIntrospectorResults.getPropertyDescriptor(srcPd.getName());
            if (Objects.nonNull(destPd)) {
              writeMethod = destPd.getWriteMethod();
              if (Objects.nonNull(writeMethod) &&
                  (readMethod.getReturnType() == writeMethod.getParameterTypes()[0]
                  || writeMethod.getParameterTypes()[0].isAssignableFrom(readMethod.getReturnType()))) {
                ReflectionUtils.makeAccessible(readMethod);
                ReflectionUtils.makeAccessible(writeMethod);
                try {
                  ReflectionUtils.invokeMethod(dest, writeMethod, ReflectionUtils.invokeMethod(src, readMethod));
                }
                catch (Exception e) {
                  // ignore
                }
              }
            }
          }
        }
      }
    }
  }
}