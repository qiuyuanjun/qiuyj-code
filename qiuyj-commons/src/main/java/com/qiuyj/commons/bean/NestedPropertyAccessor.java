package com.qiuyj.commons.bean;

import com.qiuyj.commons.ReflectionUtils;
import com.qiuyj.commons.bean.wrapper.BeanWrapperImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public abstract class NestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiateNestedPropertyNullValue = true;

  private Map<String, NestedProperty> nestedPropertyMap = new HashMap<>();

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    this.autoInstantiateNestedPropertyNullValue = autoInstantiateNestedPropertyNullValue;
  }

  @Override
  protected String resolvePropertyExpression(String propertyExpression) {
    String realPropertyName = super.resolvePropertyExpression(propertyExpression);
    int nestedIdx = propertyExpression.indexOf(PropertyAccessor.NESTED_PROPERTY_SEPARATOR);
    if (nestedIdx > 0) {
      realPropertyName = realPropertyName.substring(0, nestedIdx);
      if (realPropertyName.contains(PropertyAccessor.INDEXED_PROPERTY_PREFIX)) {
        // 表明此时是索引属性，索引属性里面又嵌套内嵌属性，此时交给处理索引属性的子类处理
        realPropertyName = resolveIndexedPropertyExpression(propertyExpression);
      }
      else {
        String nestedPropertyPart = propertyExpression.substring(nestedIdx + 1);
        resolveNestedPropertyExpressionRelationShip(realPropertyName, nestedPropertyPart);
      }
    }
    else if (nestedIdx == 0) {
      throw new ReflectionException("Nested property can not be empty. May caused by the nested property flag '.' at the first location of the property string");
    }
    else {
      // 处理索引属性
      realPropertyName = resolveIndexedPropertyExpression(propertyExpression);
    }
    return realPropertyName;
  }

  private void resolveNestedPropertyExpressionRelationShip(String realPropertyName, String nestedPropertyPart) {
    NestedProperty np = nestedPropertyMap.get(realPropertyName);
    if (Objects.nonNull(np)) {
      np.setNestedPropertyName(nestedPropertyPart);
    }
    else {
      Object nestedValue = getProperty(realPropertyName);
      if (Objects.isNull(nestedValue)) {
        if (!autoInstantiateNestedPropertyNullValue) {
          throw new ReflectionException("Does not support null property value instantiation");
        }
        else {
          nestedValue = ReflectionUtils.instantiateClass(getPropertyValueType(realPropertyName));
          setProperty(realPropertyName, nestedValue);
        }
      }
      /*
       * 这里直接new BeanWrapperImpl，因为如果是索引属性的话，子类IndexedPropertyAccessor会创建
       */
      np = new NestedProperty(new BeanWrapperImpl<>(nestedValue), nestedPropertyPart);
      nestedPropertyMap.put(realPropertyName, np);
    }
  }

  @Override
  protected Object doGetProperty(String realPropertyName) {
    NestedProperty np = nestedPropertyMap.get(realPropertyName);
    Object realValue;
    if (Objects.nonNull(np)) {
      realValue = np.getCurrentObject().getProperty(realPropertyName);
    }
    else {
      realValue = getDirectProperty(realPropertyName);
    }
    return realValue;
  }

  @Override
  protected boolean doSetProperty(String realPropertyName, Object value) {
    boolean realSetValue = false,
        cacheResult = true;
    if (PropertyAccessorSupport.NULL_VALUE == value) {
      nestedPropertyMap.remove(realPropertyName);
      realSetValue = true;
      cacheResult = false;
    }
    else {
      NestedProperty np = nestedPropertyMap.get(realPropertyName);
      if (Objects.nonNull(np)) {
        np.getCurrentObject().setProperty(getNestedOrIndexedPropertyName(np), value);
      }
      else {
        realSetValue = true;
      }
    }
    if (realSetValue) {
      setDirectPropertyValue(realPropertyName, value);
    }
    return cacheResult;
  }

  /**
   * 直接设置被包装对象的对应属性的值
   * @param realPropertyName 要设置的属性名称
   * @param value 属性值
   */
  protected abstract void setDirectPropertyValue(String realPropertyName, Object value);

  private String getNestedOrIndexedPropertyName(NestedProperty nestedProperty) {
    String indexedOrNestedPropertyName;
    if (nestedProperty instanceof IndexedProperty) {
      indexedOrNestedPropertyName = ((IndexedProperty) nestedProperty).getIndexedPropertyName();
    }
    else {
      indexedOrNestedPropertyName = nestedProperty.getNestedPropertyName();
    }
    return indexedOrNestedPropertyName;
  }

  /**
   * 直接从被包装的对象里面获取结果，交给子类重写
   */
  protected abstract Object getDirectProperty(String directPropertName);

  /**
   * 处理索引属性的表达式，交给子类重写
   */
  protected String resolveIndexedPropertyExpression(String indexedPropertyExpression) {
    return indexedPropertyExpression;
  }
}