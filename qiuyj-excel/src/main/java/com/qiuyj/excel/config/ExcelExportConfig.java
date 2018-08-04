package com.qiuyj.excel.config;

import java.util.Objects;

/**
 * excel配置，主要用于导出
 * @author qiuyj
 * @since 2017/12/28
 */
public class ExcelExportConfig {

  private static final String DEFAULT_FIRST_SHEET_NAME = "sheet1";

  public ExcelExportConfig() {
  }

  public ExcelExportConfig(String exportFilename) {
    this.exportFilename = exportFilename;
  }

  /** 导出的文件名 */
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

  public int getSheetLength() {
    return Objects.isNull(sheetNames) || sheetNames.length == 0 ? 1 : sheetNames.length;
  }

  public String[] getSheetNames() {
    return sheetNames;
  }

  public String sheetNameAt(int idx) {
    return idx >= sheetNames.length ? "sheet" + ++idx : sheetNames[idx];
  }

  public String getFirstSheetName() {
    return Objects.isNull(sheetNames) || sheetNames.length == 0 ?
        DEFAULT_FIRST_SHEET_NAME :
        sheetNames[0];
  }

  /** 导出的excel的类型，默认xlsx */
  private String excelType = "xlsx";

  public String getExcelType() {
    return excelType;
  }

  public void setExcelType(String excelType) {
    this.excelType = excelType;
  }

  /** 每页sheet的行数限定值，如果为负数，表明没有限制，那么所有的数据都将导入到当前的sheet里面 */
  private int rowLimits = -1;

  public int getRowLimits() {
    return rowLimits;
  }

  public void setRowLimits(int rowLimits) {
    this.rowLimits = rowLimits;
  }

  public boolean hasNext(int nextIterateIndex) {
    return rowLimits > 0 && getSheetLength() > nextIterateIndex;
  }

  public Integer next(int nextIterateIndex, int size) {
    return (nextIterateIndex + 1) * rowLimits > size || nextIterateIndex == getSheetLength() - 1 ? size - nextIterateIndex * rowLimits : rowLimits;
  }

  /**
   * 判断是否需要导出多个{@code Sheet}
   * @param size 数据大小
   * @return {@code true}需要导出多个{@code Sheet}，{@code false}仅仅导出一页{@code Sheet}
   */
  public boolean needMultiSheet(int size) {
    return rowLimits > 0 && size > rowLimits;
  }

  /** 自定义配置器，可以为null */
  private ExcelCustomConfigurer customConfig;

  public ExcelCustomConfigurer getCustomConfig() {
    return customConfig;
  }

  public void setCustomConfig(ExcelCustomConfigurer customConfig) {
    this.customConfig = customConfig;
  }
}