package com.qiuyj.excel.importer;

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
//  private final List<String> excelHeadInfo;

  private final Map<Integer, String> excelHeadInfo;

  protected AbstractExcelImporter(Workbook wb) {
    this.workbook = Objects.requireNonNull(wb);
    excelHeadInfo = Collections.unmodifiableMap(getExcelHeadInfo(wb));
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
          if (closeWorkbook) {
            ExcelUtils.closeExcelWorkbookQuietly(workbook);
          }
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

  protected Map<Integer, String> getExcelHeadInfo() {
    return excelHeadInfo;
  }

  /**
   * 映射excel的每一行，交给对应的子类实现
   */
  protected abstract Object excelRowMapping(Row currRow);

  /**
   * 读取excel的头信息
   */
  private static Map<Integer, String> getExcelHeadInfo(Workbook wb) {
    Sheet firstSheet = wb.getSheetAt(0);
    Row row = firstSheet.getRow(firstSheet.getFirstRowNum());
    int physicalNum = row.getPhysicalNumberOfCells(),
        lastCellNo = row.getLastCellNum(),
        idx = 0;
    TreeMap<Integer, String> headInfos = new TreeMap<>();
    if (lastCellNo >= physicalNum) {
      // 表明列不连续
      do {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (Objects.nonNull(cell)) {
          headInfos.put(idx, ExcelUtils.readExcelCellValueAsString(cell));
        }
      }
      while (++idx < physicalNum);
    }
    else {
      // 表明所有列都是连续的
      for (Cell cell : row) {
        headInfos.put(idx++, ExcelUtils.readExcelCellValueAsString(cell));
      }
    }
    return headInfos;
  }
}