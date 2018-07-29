package com.qiuyj.excel.importer;

import java.util.List;

/**
 * excel导入主接口
 * @author qiuyj
 * @since 2017/12/28
 */
public interface ExcelImporter<T> {

  /**
   * 导入excel，返回一个List
   */
  List<T> importExcel();

  List<T> importExcel(boolean closeWorkbook);
}
