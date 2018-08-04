package com.qiuyj.excel.config;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author qiuyj
 * @since 2018-07-29
 */
public interface ExcelCustomConfigurer {

  /**
   * 配置{@code Sheet}的模版方法，用户可以重写该方法，实现完全的自定义{@code Sheet}
   * @param workbook {@code Workbook}对象，方便生成{@code CellStyle}和{@code Font}对象
   * @param sheet 要配置的{@code Sheet}
   * @param headerLength 标题数量
   * @return 生成excel中{{@code Row}的下标
   */
  default int configExcel(Workbook workbook, Sheet sheet, int headerLength) {
    return 0;
  }
}
