package com.qiuyj.tools;

import java.beans.Introspector;
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
      for (String content : pathContents) {
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

  /**
   * 将驼峰名称转换成对应的下划线名称（符合java规范）
   */
  public static String camelCaseToUnderscore(String name) {
    Objects.requireNonNull(name);
    char[] chs = name.toCharArray();
    int len = chs.length;
    if (len > 1) {
      int start = 0;
      char c = chs[start];
      StringBuilder sb = new StringBuilder();
      // 处理头部
      if (Character.isUpperCase(c) && Character.isUpperCase(chs[start + 1])) {
        // 循环跳过所有的大写字母
        for (start = 2; start < len && Character.isUpperCase(chs[start]); start++);
        if (start == len)
          return new String(chs);
        sb.append(chs, 0, --start);
        sb.append("_");
        chs[start] = Character.toLowerCase(chs[start]);
      } else {
        sb.append(Character.toLowerCase(c));
        start++;
      }
      // 处理尾部
      int end = len - 1;
      // 循环跳过所有的大写字母
      if (Character.isUpperCase(chs[end]) && Character.isUpperCase(chs[end - 1]))
        for (end -= 2; end > 0 && Character.isUpperCase(chs[end]); end--);
      for (; start <= end; start++) {
        c = chs[start];
        if (Character.isUpperCase(c) && Character.isLowerCase(chs[start - 1])) {
          sb.append("_");
          sb.append(Character.toLowerCase(c));
        } else
          sb.append(c);
      }
      int suffixLen = len - 1 - end;
      if (suffixLen > 0) {
        sb.append("_");
        sb.append(chs, end + 1, suffixLen);
      }
      return sb.toString();
    } else
      return Introspector.decapitalize(name);
  }

}