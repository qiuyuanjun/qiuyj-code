package com.qiuyj.excel;

import com.qiuyj.commons.DateUtils;
import com.qiuyj.commons.StringUtils;
import com.qiuyj.excel.config.ExcelExportConfig;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public class ExcelFilenameGenerator {

  /**
   * 生成对应的导出excel的名称
   */
  public static String generateExportFilename(ExcelExportConfig config) {
    String exportFilename = config.getExportFilename();
    exportFilename = StringUtils.isBlank(exportFilename) ? DateUtils.getDateTimeString() : exportFilename;
    return exportFilename + "." + config.getExcelType();
  }
}
