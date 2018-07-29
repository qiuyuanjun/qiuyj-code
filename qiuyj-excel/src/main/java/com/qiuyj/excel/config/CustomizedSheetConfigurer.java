package com.qiuyj.excel.config;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author qiuyj
 * @since 2018-07-29
 */
public interface CustomizedSheetConfigurer {

  /**
   * 配置{@code Sheet}的模版方法，用户可以重写该方法，实现完全的自定义{@code Sheet}
   * @param sheet 要配置的{@code Sheet}
   */
  default void customConfigureSheet(Sheet sheet) {
  }
}
