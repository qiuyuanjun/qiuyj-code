package com.qiuyj.commons.reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        PropertyAccessor pa = null;
        if (realPropertyValue instanceof Map) {
          pa = new MapWrapperImpl((Map<?, ?>) realPropertyValue);
        }
        else if (realPropertyValue instanceof Collection || realPropertyValue.getClass().isArray()) {
          pa = new CollectionArrayWrapperImpl(realPropertyValue);
        }
        nestedProperty = new IndexedProperty(pa, indexedPropertyName);
        setNestedProperty(realPropertyName, nestedProperty);
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
    if (!cls.isArray() && !Collection.class.isAssignableFrom(cls) && !Map.class.isAssignableFrom(cls)) {
      throw new ReflectionException("The current property '" + realPropertyName + "' dose not support indexing");
    }
  }
}