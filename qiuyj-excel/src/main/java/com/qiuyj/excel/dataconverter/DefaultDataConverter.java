package com.qiuyj.excel.dataconverter;

/**
 * 数据转换器的默认实现，即将所有的数据全部当成字符串处理
 * @author qiuyj
 * @since 2017/12/28
 */
public class DefaultDataConverter implements DataConverter<Object> {
  private static final DefaultDataConverter INSTANCE = new DefaultDataConverter();

  public static DataConverter<Object> getInstance() {
    return INSTANCE;
  }

  @Override
  public String convertData(Object data, String pattern) {
    return data.toString();
  }

  @Override
  public Object convertString(String str, String pattern) {
    return str;
  }
}
