package com.qiuyj.commons;

import java.lang.reflect.*;

/**
 * @author qiuyj
 * @since 2018/1/10
 */
public abstract class TypeResolver {

  /**
   * 擦除泛型，得到原始类型
   */
  public static Class<?> erase(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    else if (type instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) type;
      return erase(pt.getRawType());
    }
    else if (type instanceof GenericArrayType) {
      GenericArrayType gat = (GenericArrayType) type;
      return Array.newInstance(erase(gat.getGenericComponentType()), 0).getClass();
    }
    else {
      Type[] types;
      if (type instanceof TypeVariable) {
        TypeVariable tv = (TypeVariable) type;
        types = tv.getBounds();
        return 0 < types.length ? erase(types[0]) : Object.class;
      }
      else if (type instanceof WildcardType) {
        WildcardType wt = (WildcardType) type;
        types = wt.getUpperBounds();
        return 0 < types.length ? erase(types[0]) : Object.class;
      }
    }
    throw new IllegalArgumentException("Bad type kind: " + type);
  }
}