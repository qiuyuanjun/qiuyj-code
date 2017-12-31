package com.qiuyj.excel.importer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public abstract class AbstractExcelImporter implements ExcelImporter {
  private static final int DEFAULT_EXCEL_PARSED_CONTENT_SIZE = 32;
  private final Workbook wb;

  private List excelContent;

  protected AbstractExcelImporter(Workbook wb) {
    this.wb = Objects.requireNonNull(wb);
    excelContent = new ArrayList(DEFAULT_EXCEL_PARSED_CONTENT_SIZE);
  }

  @Override
  public List importExcel() {
    return excelContent;
  }

  /**
   * 映射excel的每一行，交给对应的子类实现
   */
  protected abstract Object excelRowMapping(Row currRow);

}