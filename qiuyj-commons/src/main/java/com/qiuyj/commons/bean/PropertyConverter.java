package com.qiuyj.commons.bean;

import com.qiuyj.commons.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * 属性转换器，主要是各个类型到字符串之间的转换
 * @author qiuyj
 * @since 2018/1/4
 */
public interface PropertyConverter {

  /**
   * 将字符串转换成对应实际的类型
   * @param strValue 要转换的字符串，必须不能为null
   * @return 转换后的值
   */
  Object getConvertedPropertyValue(String strValue);

  /**
   * 将一个值转换成字符串
   * @param value 值，必须不能为null
   * @return 转换后的字符串
   */
  default String asString(Object value) {
    return value.toString();
  }

  static PropertyConverter getProxy(Object objToProxy) {
    if (!(objToProxy instanceof PropertyConverter)) {
      throw new IllegalArgumentException("Only support PropertyConverter interface's sub class");
    }
    else if (Proxy.isProxyClass(objToProxy.getClass())) {
      return (PropertyConverter) objToProxy;
    }
    else {
      return (PropertyConverter) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
          new Class<?>[] {PropertyConverter.class},
          new PropertyConverterMethodParameterNullChecker(objToProxy));
    }
  }

  final class PropertyConverterMethodParameterNullChecker implements InvocationHandler {
    private final Object origin;

    public PropertyConverterMethodParameterNullChecker(Object propertyConverter) {
      origin = propertyConverter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // 判断参数是否是null，如果是null，那么抛出异常
      if (Objects.isNull(args[0])) {
        throw new IllegalArgumentException("PropertyConverter interface's method's parameter must not be null");
      }
      else {
        return method.invoke(origin, args);
      }
    }
  }
}