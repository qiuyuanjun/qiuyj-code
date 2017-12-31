package com.qiuyj.excel.importer;

import com.qiuyj.commons.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public class MapExcelImporter extends AbstractExcelImporter {
  /**
   * 导入excel的表头信息，按顺序
   */
  private final List<String> excelHeadInfo;

  public MapExcelImporter(Workbook workbook) {
    super(workbook);
    excelHeadInfo = Collections.unmodifiableList(getExcelHeadInfo(workbook));
  }

  @Override
  protected Object excelRowMapping(Row currRow) {
    return new LinkedHashMap();
  }

  private static final List<String> getExcelHeadInfo(Workbook wb) {
    Sheet firstSheet = wb.getSheetAt(0);
    Row row = firstSheet.getRow(firstSheet.getFirstRowNum());
    List<String> excelHeadInfo = new ArrayList<>(row.getLastCellNum() + 1);
    Iterator<Cell> cellIt = row.cellIterator();
    while (cellIt.hasNext()) {
      String cellValue = cellIt.next().getStringCellValue();
      if (StringUtils.isNotBlank(cellValue)) {
        excelHeadInfo.add(cellValue);
      }
    }
    return excelHeadInfo;
  }
}