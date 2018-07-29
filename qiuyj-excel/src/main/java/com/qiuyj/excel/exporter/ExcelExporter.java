package com.qiuyj.excel.exporter;

import java.util.List;

/**
 * excel导出主接口
 * @author qiuyj
 * @since 2017/12/28
 */
public interface ExcelExporter {

  /**
   * 导出excel
   * @param exportData 待导出的数据
   */
  void exportExcel(List<?> exportData);
}
