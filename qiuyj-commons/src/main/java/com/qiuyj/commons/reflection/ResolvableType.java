package com.qiuyj.commons.reflection;

import com.qiuyj.commons.TypeResolver;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyj
 * @since 2018/1/10
 */
public final class ResolvableType {

  private final Type type;

  private final Class<?> rawClass;

  private ResolvableType[] generics;

  private ResolvableType superclass;

  private ResolvableType[] interfaces;

  private ResolvableType(Type type, Class<?> rawClass) {
    this.type = type;
    this.rawClass = rawClass;
  }

  public static ResolvableType forField(Field field) {
    if (Objects.isNull(field)) {
      return new ResolvableType(null, null);
    }
    else {
      Type genericType = field.getGenericType();
      Class<?> rawClass;
      if (genericType instanceof Class) {
        rawClass = (Class<?>) genericType;
      }
      else {
        rawClass = field.getType();
      }
      return new ResolvableType(genericType, rawClass);
    }
  }

  public static ResolvableType forType(Type type) {
    Class<?> rawClass = TypeResolver.erase(type);
    return new ResolvableType(type, rawClass);
  }

  public ResolvableType genericAt(int idx) {
    if (idx < 0) {
      throw new IllegalArgumentException("idx < 0");
    }
    else if (Objects.isNull(generics) && type instanceof ParameterizedType) {
      Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
      int len = genericTypes.length;
      generics = new ResolvableType[len];
      for (int i = 0; i < len; i++) {
        generics[i] = forType(genericTypes[i]);
      }
    }
    if (Objects.isNull(generics)) {
      return null;
    }
    else if (idx >= generics.length) {
      throw new IllegalArgumentException("idx >= " + generics.length);
    }
    else {
      return generics[idx];
    }
  }

  public Class<?> resolve() {
    if (Objects.isNull(rawClass)) {
      if (Objects.isNull(type)) {
        return null;
      }
      else {
        return TypeResolver.erase(type);
      }
    }
    else {
      return rawClass;
    }
  }

  public Class<?> resolveGenericAt(int idx) {
    return Optional.ofNullable(genericAt(idx))
                   .map(ResolvableType::resolve)
                   .orElse(null);
  }

  /**
   * 得到对应的父类的ResolvableType
   */
  public ResolvableType getSuperclass() {
    if (Objects.isNull(superclass)) {
      if (Objects.isNull(rawClass)) {
        if (Objects.isNull(type)) {
          superclass = null;
        }
        else {
          Class<?> rawClass = TypeResolver.erase(type);
          superclass = forType(rawClass.getGenericSuperclass());
        }
      }
      else {
        if (!rawClass.isInterface()) {
          superclass = forType(rawClass.getGenericSuperclass());
        }
        else {
          superclass = null;
        }
      }
    }
    if (Objects.isNull(superclass)) {
      superclass = new ResolvableType(null, null);
    }
    return superclass;
  }

  /**
   * 判断是否是Collection集合
   */
  public boolean isCollection() {
    return Optional.ofNullable(resolve())
                   .map(Collection.class::isAssignableFrom)
                   .orElse(false);
  }

  /**
   * 判断是否是数组
   */
  public boolean isArray() {
    return Optional.ofNullable(resolve())
                   .map(Class::isArray)
                   .orElse(false);
  }

  /**
   * 判断是否是Map
   */
  public boolean isMap() {
    return Optional.ofNullable(resolve())
                   .map(Map.class::isAssignableFrom)
                   .orElse(false);
  }

  public ResolvableType getInterface(int idx) {
    ResolvableType[] interfaces = getInterfaces();
    if (Objects.nonNull(interfaces)) {
      if (idx < 0 || idx > interfaces.length) {
        throw new ArrayIndexOutOfBoundsException();
      }
      else {
        return interfaces[idx];
      }
    }
    else {
      return null;
    }
  }

  public ResolvableType[] getInterfaces() {
    if (Objects.isNull(interfaces)) {
      Class<?> cls = resolve();
      if (Objects.nonNull(cls)) {
        Type[] types = cls.getGenericInterfaces();
        int len = types.length;
        interfaces = new ResolvableType[len];
        for (int i = 0; i < len; i++) {
          interfaces[i] = forType(types[i]);
        }
      }
    }
    if (Objects.isNull(interfaces)) {
      interfaces = new ResolvableType[0];
    }
    return interfaces;
  }
}