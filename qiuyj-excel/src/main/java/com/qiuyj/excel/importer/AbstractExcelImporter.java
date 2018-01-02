package com.qiuyj.excel.importer;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public abstract class AbstractExcelImporter implements ExcelImporter {
  private final Workbook workbook;

  /**
   * 导入excel的表头信息，按顺序
   */
  private final List<String> excelHeadInfo;

  protected AbstractExcelImporter(Workbook wb) {
    this.workbook = Objects.requireNonNull(wb);
    excelHeadInfo = Collections.unmodifiableList(getExcelHeadInfo(wb));
  }

  @Override
  public List importExcel() {
    return importExcel(true);
  }

  @SuppressWarnings("unchecked")
  public List importExcel(boolean closeWorkbook) {
    List excelContent = new ArrayList();
    for (Sheet sheet : workbook) {
      if (ExcelUtils.isEmptySheet(sheet)) {
        continue;
      }
      // 跳过头部
      Iterator<Row> rowIt = skipTopRow(sheet);
      while (rowIt.hasNext()) {
        Object mappingResult = excelRowMapping(rowIt.next());
        if (Objects.nonNull(mappingResult)) {
          excelContent.add(mappingResult);
        }
        else {
          throw new IllegalArgumentException("Don't accept null element");
        }
      }
    }
    if (closeWorkbook) {
      ExcelUtils.closeExcelWorkbookQuietly(workbook);
    }
    return excelContent;
  }

  private Iterator<Row> skipTopRow(Sheet sheet) {
    Iterator<Row> rowIt = sheet.rowIterator();
    rowIt.next();
    return rowIt;
  }

  protected List<String> getExcelHeadInfo() {
    return excelHeadInfo;
  }

  /**
   * 映射excel的每一行，交给对应的子类实现
   */
  protected abstract Object excelRowMapping(Row currRow);

  /**
   * 读取excel的头信息
   */
  private static List<String> getExcelHeadInfo(Workbook wb) {
    Sheet firstSheet = wb.getSheetAt(0);
    Row row = firstSheet.getRow(firstSheet.getFirstRowNum());
    List<String> excelHeadInfo = new ArrayList<>(row.getLastCellNum() + 1);
    for (Cell cell : row) {
      String cellValue = ExcelUtils.readExcelCellValueAsString(cell);
      if (StringUtils.isNotBlank(cellValue)) {
        excelHeadInfo.add(cellValue);
      }
      else {
        excelHeadInfo.add(ExcelImporter.EMPTY_HEAD_INFO);
      }
    }
    return excelHeadInfo;
  }
}