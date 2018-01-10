package com.qiuyj.commons.reflection;

import java.util.*;

/**
 * @author qiuyj
 * @since 2018/1/9
 */
public abstract class IndexedPropertyAccessor extends NestedPropertyAccessor {

  private final Map<String, IndexedProperty> indexedRootProperty;

  protected IndexedPropertyAccessor() {
    indexedRootProperty = new HashMap<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected String resolveIndexedPropertyName(String indexedPropertyName) {
    int indexedIdx = indexedPropertyName.indexOf(PropertyAccessor.INDEXED_PROPERTY_PREFIX_STRING);
    if (indexedIdx > 0) {
      String realPropertyName = indexedPropertyName.substring(0, indexedIdx);
      Object realPropertyValue = getOrInitPropertyValue(realPropertyName);
      // 判断是否是支持索引的数据结构（Map，Collection，Array）
      validateThatIndexingIsSupported(realPropertyName, realPropertyValue);
      IndexedProperty nestedProperty = indexedRootProperty.get(realPropertyName);
      indexedPropertyName = indexedPropertyName.substring(indexedIdx + 1);
      if (Objects.isNull(nestedProperty)) {
        PropertyAccessor pa;
        if (realPropertyValue instanceof Map) {
          pa = new MapWrapperImpl((Map<String, Object>) realPropertyValue);
        }
        else if (realPropertyValue instanceof List || realPropertyValue.getClass().isArray()) {
          pa = new ListArrayWrapperImpl(realPropertyValue);
        }
        else {
          throw new ReflectionException("Unsupport type: " + realPropertyValue.getClass());
        }
        nestedProperty = new IndexedProperty(pa, indexedPropertyName);
        setNestedProperty(realPropertyName, nestedProperty);
        indexedRootProperty.put(realPropertyName, nestedProperty);
      }
      else {
        nestedProperty.setIndexedPropertyName(indexedPropertyName);
      }
      indexedPropertyName = realPropertyName;
    }
    else if (indexedIdx == 0) {
      throw new ReflectionException("Indexed property can not be empty. May caused by the indexed property flag '[' at the first location of the property string");
    }
    return indexedPropertyName;
  }

  private static void validateThatIndexingIsSupported(String realPropertyName, Object realPropertyValue) {
    Class<?> cls = realPropertyValue.getClass();
    if (!cls.isArray() && !List.class.isAssignableFrom(cls) && !Map.class.isAssignableFrom(cls)) {
      throw new ReflectionException("The current property '" + realPropertyName + "' dose not support indexing");
    }
  }
}