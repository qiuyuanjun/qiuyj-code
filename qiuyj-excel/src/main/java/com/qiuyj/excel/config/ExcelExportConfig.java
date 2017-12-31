package com.qiuyj.excel.config;

import java.util.Objects;

/**
 * excel配置，主要用于导出
 * @author qiuyj
 * @since 2017/12/28
 */
public class ExcelExportConfig {

  /**
   * 导出的文件名
   */
  private String exportFilename;

  public void setExportFilename(String exportFilename) {
    this.exportFilename = exportFilename;
  }

  public String getExportFilename() {
    return exportFilename;
  }

  /**
   * sheet名，可以为null
   */
  private String[] sheetNames;

  public void setSheetNames(String firstSheetName, String... more) {
    Objects.requireNonNull(firstSheetName);
    int moreLen = more.length,
        len = 1;
    if (moreLen > 0) {
      len += moreLen;
    }
    sheetNames = new String[len];
    sheetNames[0] = firstSheetName;
    System.arraycopy(more, 0, sheetNames, 1, moreLen);
  }

  public String[] getSheetNames() {
    return sheetNames;
  }

  public String getFirstSheetName() {
    return sheetNames[0];
  }
}