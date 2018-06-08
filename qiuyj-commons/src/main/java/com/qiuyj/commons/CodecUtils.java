package com.qiuyj.commons;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-06-08
 */
public abstract class CodecUtils {

  private static final String UNICODE_STRING_PREFIX = "\\u";

  /**
   * 得到一个字符串的{@code unicode}表示，这种方法可以有效的编码中文
   */
  public static String toUnicodeString(String src) {
    if (Objects.isNull(src)) {
      return null;
    }
    char[] srcCh = src.toCharArray();
    StringBuilder unicodeStringBuilder  = new StringBuilder(srcCh.length * 6);
    for (char c : srcCh) {
      if (c < 256) { // 如果在byte范围，那么无需转换
        unicodeStringBuilder.append(c);
      }
      else {
        byte h = (byte) (c >>> 8); // 高8位
        byte l = (byte) c;         // 低8位
        unicodeStringBuilder.append(UNICODE_STRING_PREFIX);
        byte2hexWithUnicodeString(unicodeStringBuilder, h);
        byte2hexWithUnicodeString(unicodeStringBuilder, l);
//        unicodeStringBuilder.append(Integer.toHexString(c));
      }
    }
    return unicodeStringBuilder.toString();
  }

  /**
   * 将{@code unicode}字符串转换成{@code java}的本地字符串
   */
  public static String parseUnicodeString(String unicodeString) {
    if (Objects.isNull(unicodeString)) {
      return null;
    }
    char[] chs = unicodeString.toCharArray();
    StringBuilder sb = new StringBuilder(chs.length >>> 2);
    for (int i = 0; i < chs.length;) {
      if (chs[i] == '\\' && chs[i + 1] == 'u') {
        // 得到当前位置的hexString
        String hex = unicodeString.substring(i + 2, i += 6);
        // 将对应的hexString转成10进制并强转为char范围
        sb.append((char) Integer.parseInt(hex, 16));
      }
      else {
        sb.append(chs[i++]);
      }
    }
    return sb.toString();
  }

  private static void byte2hexWithUnicodeString(StringBuilder sb, byte b) {
    String hex = Integer.toHexString(Byte.toUnsignedInt(b));
    if (hex.length() == 1) {
      sb.append("0");
    }
    sb.append(hex);
  }
}
