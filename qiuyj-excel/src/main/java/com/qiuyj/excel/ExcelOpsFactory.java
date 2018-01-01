package com.qiuyj.excel;

import com.qiuyj.commons.nio.StreamUtils;
import com.qiuyj.excel.importer.BeanExcelImporter;
import com.qiuyj.excel.importer.ExcelImporter;
import com.qiuyj.excel.importer.MapExcelImporter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public abstract class ExcelOpsFactory {

  public static ExcelImporter getExcelImporter(InputStream in) {
    return getExcelImporter(in, null);
  }

  public static ExcelImporter getExcelImporter(InputStream in, Class<?> beanCls) {
    Objects.requireNonNull(in);
    Workbook wb;
    try {
      wb = WorkbookFactory.create(in);
    }
    catch (InvalidFormatException | IOException e) {
      // 关闭流
      StreamUtils.closeQuietly(in);
      throw new IllegalStateException("Error parsing excel file.\n Caused by: " + e, e);
    }
    if (Objects.isNull(beanCls)) {
      return new MapExcelImporter(wb);
    }
    else {
      return new BeanExcelImporter(wb, beanCls);
    }
  }
}