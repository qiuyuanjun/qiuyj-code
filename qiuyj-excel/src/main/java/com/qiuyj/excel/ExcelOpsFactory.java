package com.qiuyj.excel;

import com.qiuyj.excel.importer.ExcelImporter;
import com.qiuyj.excel.importer.MapExcelImporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public abstract class ExcelOpsFactory {

  public static final ExcelImporter getExcelImporter(InputStream in) {
    Objects.requireNonNull(in);
    Workbook wb = getExcelImportWorkbook(in);
    if (Objects.isNull(wb)) {
      throw new IllegalStateException("Error handling excel input stream");
    }
    else {
      return new MapExcelImporter(wb);
    }
  }

  /**
   * 得到导入的Excel对应的Workbook对象
   * @param in excel的输入流
   */
  private static Workbook getExcelImportWorkbook(InputStream in) {
    Workbook wb = null;
    try {
      wb = new XSSFWorkbook(in);
    }
    catch (IOException e) {
      // ignore
    }
    catch (Exception e1) {
      // 这里需要用HSSFWorkbook创建
      try {
        wb = new HSSFWorkbook(in);
      }
      catch (IOException e2) {
        // ignore
      }
      catch (Exception e3) {
        throw new IllegalArgumentException("Illegal excel type");
      }
    }
    return wb;
  }
}