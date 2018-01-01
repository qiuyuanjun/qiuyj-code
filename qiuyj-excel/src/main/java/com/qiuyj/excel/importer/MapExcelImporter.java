package com.qiuyj.excel.importer;

import com.qiuyj.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public class MapExcelImporter extends AbstractExcelImporter {

  public MapExcelImporter(Workbook workbook) {
    super(workbook);
  }

  @Override
  protected Object excelRowMapping(Row currRow) {
    Map<String, String> map = new LinkedHashMap<>();
    List<String> headInfo = getExcelHeadInfo();
    int idx = 0;
    for (Cell cell : currRow) {
      String headTitle = headInfo.get(idx++);
      if (headTitle == ExcelImporter.EMPTY_HEAD_INFO) {
        continue;
      }
      map.put(headTitle, ExcelUtils.readExcelCellValueAsString(cell));
    }
    return map;
  }
}