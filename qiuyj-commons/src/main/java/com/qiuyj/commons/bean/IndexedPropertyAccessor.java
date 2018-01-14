package com.qiuyj.commons.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/1/14
 */
public abstract class IndexedPropertyAccessor extends NestedPropertyAccessor {

  private final Map<String, IndexedProperty> indexedPropertyMap = new HashMap<>();

  @Override
  protected String resolveIndexedPropertyExpression(String indexedPropertyExpression) {
    int indexedBegin = indexedPropertyExpression.indexOf(PropertyAccessor.INDEXED_PROPERTY_PREFIX);
    if (indexedBegin > 0) {
      int indexedEnd = indexedPropertyExpression.lastIndexOf(PropertyAccessor.INDEXED_RPOPERTY_SUFFIX);
      if (indexedEnd <= 0) {
        throw new ReflectionException("Must specify end flag ']' of indexed property");
      }
      else {
        String indexedPropertyPart = indexedPropertyExpression.substring(indexedBegin + 1, indexedEnd);
        indexedPropertyExpression = indexedPropertyExpression.substring(0, indexedBegin);
        resolveIndexedPropertyExpressionRelationShip(indexedPropertyExpression, indexedPropertyPart);
      }
    }
    else if (indexedBegin == 0) {
      throw new ReflectionException("Indexed property can not be empty. May caused by the indexed property flag '[' at the first location of the property string");
    }
    return indexedPropertyExpression;
  }

  private void resolveIndexedPropertyExpressionRelationShip(String indexedPropertyName, String indexedPropertyPart) {
    IndexedProperty ip = indexedPropertyMap.get(indexedPropertyName);
    if (Objects.nonNull(ip)) {

    }
    else {
      Object indexedObject = getProperty(indexedPropertyName);
      if (Objects.isNull(indexedObject)) {

      }
    }
  }
}