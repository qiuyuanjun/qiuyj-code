package com.qiuyj.excel.importer;

import com.qiuyj.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
    Map<Integer, String> headInfo = getExcelHeadInfo();
    Map<String, String> result = new LinkedHashMap<>(headInfo.size());
    headInfo.forEach((idx, title) -> {
      Cell cell = currRow.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      if (Objects.nonNull(cell)) {
        result.put(title, ExcelUtils.readExcelCellValueAsString(cell));
      }
      else {
//        result.put(title, null);
        // no-op
      }
    });
    return result;
  }
}