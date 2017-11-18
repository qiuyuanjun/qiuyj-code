package com.qiuyj.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * 字符串的工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class StringUtils {
  public static final String[] EMPTY_STRING_ARRAY = {};
  private static final String PATH_SEPERATORs = "\\/";

  /**
   * 清理路径信息，即将路径中的..和.转换成实际路径
   */
  public static String cleanPath(String originPath) {
    if (Objects.isNull(originPath))
      return null;
    else {
      int protocolIdx = originPath.indexOf(":");
      String protocol = "", pathToUse = originPath;
      if (protocolIdx > -1) {
        int substringIdx = protocolIdx + 1;
        protocol = originPath.substring(0, substringIdx);
        pathToUse = originPath.substring(substringIdx);
      }
      String[] pathContents = delimiteToStringArray(pathToUse, PATH_SEPERATORs);
      int len = pathContents.length,
          lastInsertIdx = 0;
      String[] pathBuilder = new String[len];
      String content;
      for (int i = 0; i < len; i++) {
        content = pathContents[i];
        if ("..".equals(content))
          lastInsertIdx--;
        else if (!".".equals(content)) {
          lastInsertIdx = lastInsertIdx < 0 ? 0 : lastInsertIdx;
          pathBuilder[lastInsertIdx++] = content;
        }
      }
      if (lastInsertIdx != len)
        pathBuilder = Arrays.copyOf(pathBuilder, lastInsertIdx);
      return protocol + File.separator + String.join(File.separator, pathBuilder);
    }
  }

  /**
   * 将一个字符串按照分隔符分割成一个字符串数组
   */
  public static String[] delimiteToStringArray(String str, String delimiter) {
    if (Objects.isNull(str))
      return EMPTY_STRING_ARRAY;
    else if (Objects.isNull(delimiter))
      return new String[] {str};
    else {
      StringTokenizer token = new StringTokenizer(str, delimiter);
      int count = token.countTokens();
      String[] rt = new String[count];
      for (int i = 0; i < count; i++) {
        rt[i] = token.nextToken();
      }
      return rt;
    }
  }

  /**
   * 截取一个字符串所匹配的要截取的字符串的前面部分
   */
  public static String substringBefore(String target, String substring) {
    Objects.requireNonNull(target);
    if (Objects.isNull(substring))
      return target;
    else {
      int idx = target.indexOf(substring);
      return idx > -1 ? target.substring(0, idx) : null;
    }
  }

  /**
   * 截取一个字符串匹配的要截取的字符串的后面部分
   */
  public static String substringAfter(String target, String substring) {
    Objects.requireNonNull(target);
    if (Objects.isNull(substring))
      return target;
    else {
      int idx = target.lastIndexOf(substring);
      return idx > -1 ? target.substring(idx + substring.length()) : null;
    }
  }

  /**
   * 判断一个字符串是否为null或者全是空格
   */
  public static boolean isBlank(String str) {
    return Objects.isNull(str) || "".equals(str.trim());
  }

  /**
   * 判断一个字符串既不为null也不全是空格
   */
  public static boolean isNotBlank(String str) {
    return Objects.nonNull(str) && !"".equals(str.trim());
  }
}