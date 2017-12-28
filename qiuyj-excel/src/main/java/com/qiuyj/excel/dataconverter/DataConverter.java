package com.qiuyj.excel.dataconverter;

/**
 * 数据转换器
 * @author qiuyj
 * @since 2017/12/28
 */
public interface DataConverter<T> {

  /**
   * 将对应的类型转换成字符串类型，主要用于导出excel
   * @param data 要转换的数据
   * @return 转换后的字符串
   */
  String convertData(T data);

  /**
   * 将excel导入的字符串转成对应的数据类型
   * @param str excel对应列的字符串值
   * @return 对应类型的数据
   */
  T convertString(String str);
}