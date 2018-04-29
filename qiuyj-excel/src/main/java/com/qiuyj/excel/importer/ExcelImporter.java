package com.qiuyj.excel.importer;

import java.util.List;

/**
 * excel导入主接口
 * @author qiuyj
 * @since 2017/12/28
 */
public interface ExcelImporter {

  /**
   * 导入excel，返回一个List
   */
  List importExcel();

  List importExcel(boolean closeWorkbook);
}
