package com.qiuyj.excel.exporter;

import com.qiuyj.commons.StringUtils;
import com.qiuyj.excel.ExcelFilenameGenerator;
import com.qiuyj.excel.ExcelType;
import com.qiuyj.excel.config.ExcelExportConfig;
import com.qiuyj.excel.config.ExcelHeaderConfig;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-07-28
 */
public abstract class AbstractExcelExporter<T> implements ExcelExporter<T> {

  private static final ExcelExportConfig DEFAULT_EXPORT_CONFIG = new ExcelExportConfig();

  /** 要导出的头部信息 */
  private final ExcelHeaderConfig[] headers;

  /** excel导出配置 */
  private ExcelExportConfig exportConfig;

  protected AbstractExcelExporter(ExcelHeaderConfig[] headers) {
    this(headers, null);
  }

  protected AbstractExcelExporter(ExcelHeaderConfig[] headers, ExcelExportConfig exportConfig) {
    if (Objects.isNull(headers) || headers.length == 0) {
      throw new IllegalArgumentException("Specify at least one header information.");
    }
    else if (Objects.isNull(exportConfig)) {
      // 设置默认的导出配置信息
      exportConfig = DEFAULT_EXPORT_CONFIG;
    }
    this.headers = headers;
    this.exportConfig = exportConfig;
  }

  @Override
  public void exportExcel(List<T> exportData) {
    if (Objects.isNull(exportData) || exportData.size() == 0) {
      throw new IllegalArgumentException("Must have data for export.");
    }
    Workbook workbook = createExcel(exportConfig);
    if (exportConfig.needMultiSheet(exportData.size())) {
      // 导出多个sheet
      int nextIterateIndex = 0;
      while (exportConfig.hasNext(nextIterateIndex)) {
        int fromIndex = nextIterateIndex * exportConfig.getRowLimits();
        List<T> data = exportData.subList(fromIndex, fromIndex + exportConfig.next(nextIterateIndex, exportData.size()));
        fillDataIntoSheet(workbook, exportConfig.sheetNameAt(nextIterateIndex++), data);
      }
    }
    else {
      // 导出单个sheet
      fillDataIntoSheet(workbook, exportConfig.getFirstSheetName(), exportData);
    }
    String filename = ExcelFilenameGenerator.generateExportFilename(exportConfig);
    try {
      workbook.write(new FileOutputStream("E:\\" + filename));
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 填充数据到sheet
   * @param workbook workbook对象
   * @param sheetName sheet的名称
   * @param exportData 要填充的数据
   */
  private void fillDataIntoSheet(Workbook workbook, String sheetName, List<T> exportData) {
    Sheet sheet = workbook.createSheet(sheetName);
    // 通用sheet配置
    int next = customBeforeGenerateExcelFile(workbook, sheet, exportConfig, headers.length);
    // 生成头部信息
    next = generateHeaderRow(sheet, headers, workbook, exportConfig, next);
    // 填充数据
    for (T data : exportData) {
      Row currRow = sheet.createRow(next++);
      mappingToRow(data, currRow);
    }
  }

  private static int customBeforeGenerateExcelFile(Workbook workbook, Sheet sheet, ExcelExportConfig exportConfig, int headerLength) {
    return Objects.nonNull(exportConfig.getCustomConfig()) ? exportConfig.getCustomConfig().configExcel(workbook, sheet, headerLength) : 0;
  }

  /**
   * 将数据映射到{@code Row}里面
   * @param data 数据
   * @param currRow 要映射的当前{@code Row}对象
   */
  private void mappingToRow(T data, Row currRow) {
    int len = headers.length;
    for (int i = 0; i < len; i++) {
      Cell cell = currRow.createCell(i, CellType.STRING);
      String cellValue = getCellValue(data, headers[i].getTitle());
      if (StringUtils.isNotBlank(cellValue)) {
        cell.setCellValue(cellValue);
      }
    }
  }

  /**
   * 得到对应的{@code Cell}的值，交给对应的子类实现
   * @param data 数据
   * @param title 名称
   * @return 对应的值
   */
  protected abstract String getCellValue(T data, String title);

  /**
   * 创建对应的需要导出的{@code Workbook}
   * @param config 导出配置
   * @return {@code Workbook}对象
   */
  private static Workbook createExcel(ExcelExportConfig config) {
    return ExcelType.ofExtension(config.getExcelType()).createWorkbook();
  }

  /**
   * 生成excel头部信息
   * @param sheet 对应的{@code Sheet}
   * @param headers 所有的头部名称
   */
  private static int generateHeaderRow(Sheet sheet,
                                       ExcelHeaderConfig[] headers,
                                       Workbook workbook,
                                       ExcelExportConfig config,
                                       int idx) {
    Row header = sheet.createRow(idx);
    int len = headers.length;
    for (int i = 0; i < len; i++) {
      Cell cell = header.createCell(i, CellType.STRING);
      ExcelHeaderConfig headerConfig = headers[i];
      if (headerConfig.getColumnWidth() > 0) {
        sheet.setColumnWidth(i, headerConfig.getColumnWidth());
      }
      CellStyle style = workbook.createCellStyle();
      // 设置水平和垂直居中
      style.setAlignment(HorizontalAlignment.CENTER);
      style.setVerticalAlignment(VerticalAlignment.CENTER);
      // 如果有字体设置，那么就设置字体
      boolean hasFontName = StringUtils.isNotBlank(headerConfig.getFontName()),
              hasFontSize = headerConfig.getFontSize() > 0;
      if (hasFontName || hasFontSize) {
        Font font = workbook.createFont();
        if (hasFontName) {
          font.setFontName(headerConfig.getFontName());
        }
        if (hasFontSize) {
          font.setFontHeightInPoints((short) headerConfig.getFontSize());
        }
        style.setFont(font);
      }
      cell.setCellStyle(style);
      cell.setCellValue(headers[i].getTitle());
    }
    return ++idx;
  }
}