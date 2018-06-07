package com.qiuyj.excel.importer;

import com.qiuyj.commons.validate.ValidationException;
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
  private Map<Integer, String> excelHeadInfo;

  protected AbstractExcelImporter(Workbook wb) {
    this.workbook = Objects.requireNonNull(wb);
    setExcelHeadInfo(getExcelHeadInfo(wb));
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
      try {
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
      catch (ValidationException e) {
        // 如果验证未通过，那么直接抛出异常
        ExcelUtils.closeExcelWorkbookQuietly(workbook);
        throw new IllegalStateException(e);
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
   * @param currRow 当前的{@code Row}对象
   * @return 对应的解析结果
   * @throws ValidationException 每个解析结果都需要对其做验证，如果验证不通过，则抛出该异常
   */
  protected abstract Object excelRowMapping(Row currRow) throws ValidationException;

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

  void setExcelHeadInfo(Map<Integer, String> excelHeadInfo) {
    this.excelHeadInfo = Collections.unmodifiableMap(excelHeadInfo);
  }
}