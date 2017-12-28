package com.qiuyj.excel.dataconverter;

/**
 * 数据转换器
 * @author qiuyj
 * @since 2017/12/28
 */
public interface DataConverter<T> {

  /**
   * 如果不需要传入格式，那么可以将该参数传入满足语法要求
   */
  String NO_PATTERN = "";

  /**
   * 按照某种格式转换数据，主要用于导出excel
   * @param data 要转换的数据
   * @param pattern 格式，如果不需要，那么传入{@code NO_PATTERN}
   * @return 转换后的字符串
   */
  String convertData(T data, String pattern);

  /**
   * 将excel导入的字符串转成对应的数据类型
   * @param str excel对应列的字符串值
   * @param pattern 格式，如果不需要，那么传入{@code NO_PATTERN}
   * @return 对应类型的数据
   */
  T convertString(String str, String pattern);
}