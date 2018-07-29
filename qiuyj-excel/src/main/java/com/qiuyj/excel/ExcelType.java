package com.qiuyj.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-07-29
 */
public enum ExcelType {

  XLS("xls", null),
  XLSX("xlsx", XSSFWorkbookType.XLSX),
  XLSM("xlsm", XSSFWorkbookType.XLSM);

  private final String extension;

  private final XSSFWorkbookType type;

  ExcelType(String extension, XSSFWorkbookType type) {
    this.extension = extension;
    this.type = type;
  }

  /**
   * 创建对应的Workbook对象
   */
  public Workbook createWorkbook() {
    return Objects.isNull(type) ? new HSSFWorkbook() : new XSSFWorkbook(type);
  }

  /**
   * 得到对应的excel类型的扩展名
   */
  public String getExcelExtension() {
    return extension;
  }

  /**
   * 根据扩展名得到对应的{@code ExcelType}对象
   * @param extension 扩展名
   * @return 对应的{{@code ExcelType}对象
   */
  public static ExcelType ofExtension(String extension) {
    for (ExcelType type : values()) {
      if (type.extension.equals(extension.toLowerCase())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unsupport excel type: " + extension + " yet.");
  }
}