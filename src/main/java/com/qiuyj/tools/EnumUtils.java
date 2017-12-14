package com.qiuyj.tools;

/**
 * @author qiuyj
 * @since 2017/12/14
 */
public abstract class EnumUtils {

  public static <E extends Enum> E getByOridnal(Class<E> type, int ordinal) {
    E[] enumConstants = type.getEnumConstants();
    if (ordinal < 0 || ordinal >= enumConstants.length)
      throw new IllegalArgumentException("Index bound of enum type: " + type);
    return enumConstants[ordinal];
  }
}
