package com.qiuyj.commons;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/14
 */
public abstract class EnumUtils {

  public static <E extends Enum> E getByOridnal(Class<E> type, int ordinal) {
    Objects.requireNonNull(type);
    if (ordinal < 0) {
      throw new IllegalArgumentException("Negative ordinal: " + ordinal);
    }
    else {
      E[] enumConstants = type.getEnumConstants();
      if (ordinal >= enumConstants.length) {
        throw new IllegalArgumentException("Index bound of enum type: " + type);
      }
      else {
        return enumConstants[ordinal];
      }
    }
  }
}
