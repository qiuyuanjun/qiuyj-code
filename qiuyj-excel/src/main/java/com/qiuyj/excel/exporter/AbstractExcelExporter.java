package com.qiuyj.excel.exporter;

import com.qiuyj.excel.ExcelFilenameGenerator;
import com.qiuyj.excel.ExcelType;
import com.qiuyj.excel.config.ExcelExportConfig;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-07-28
 */
public abstract class AbstractExcelExporter implements ExcelExporter {

  /** 要导出的头部信息 */
  private final String[] headers;

  /** excel导出配置 */
  private ExcelExportConfig exportConfig;

  protected AbstractExcelExporter(String[] headers) {
    this(headers, null);
  }

  protected AbstractExcelExporter(String[] headers, ExcelExportConfig exportConfig) {
    if (Objects.isNull(headers) || headers.length == 0) {
      throw new IllegalArgumentException("Specify at least one header information.");
    }
    else {
      if (Objects.isNull(exportConfig)) {
        // 设置默认的导出配置信息
        exportConfig = new ExcelExportConfig();
      }
      this.headers = headers;
      this.exportConfig = exportConfig;
    }
  }

  @Override
  public void exportExcel(List<?> exportData) {
    if (Objects.isNull(exportData) || exportData.size() == 0) {
      throw new IllegalArgumentException("Must have data for export.");
    }
    else if (exportConfig.needMultiSheet(exportData.size())) {
      Workbook workbook = createExcel(exportConfig);
      // 导出多个sheet
      int nextIterateIndex = 0;
      while (exportConfig.hasNext(nextIterateIndex)) {
        int fromIndex = nextIterateIndex * exportConfig.getRowLimits();
        List<?> data = exportData.subList(fromIndex, fromIndex + exportConfig.next(nextIterateIndex, exportData.size()));
        fillDataIntoSheet(workbook, exportConfig.sheetNameAt(nextIterateIndex++), data);
      }

    }
    else {
      Workbook workbook = createExcel(exportConfig);
      // 导出单个sheet
      fillDataIntoSheet(workbook, exportConfig.getFirstSheetName(), exportData);
      String filename = ExcelFilenameGenerator.generateExportFilename(exportConfig);
      try {
        workbook.write(new FileOutputStream("E:\\" + filename));
        workbook.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 填充数据到sheet
   * @param workbook workbook对象
   * @param sheetName sheet的名称
   * @param exportData 要填充的数据
   */
  private void fillDataIntoSheet(Workbook workbook, String sheetName, List<?> exportData) {
    Sheet sheet = workbook.createSheet(sheetName);
    // 通用sheet配置
    configSheetIfNecessary(sheet, exportConfig, headers);
    // 生成头部信息
    int next = generateHeaderRow(sheet, headers, workbook, exportConfig);
    // 填充数据
    for (Object data : exportData) {
      Row currRow = sheet.createRow(next++);
      mappingToRow(data, currRow);
    }
  }

  /**
   * 将数据映射到{@code Row}里面，交给具体的子类实现
   * @param data 数据
   * @param currRow 要映射的当前{@code Row}对象
   */
  protected abstract void mappingToRow(Object data, Row currRow);

  /**
   * 创建对应的需要导出的{@code Workbook}
   * @param config 导出配置
   * @return {@code Workbook}对象
   */
  private static Workbook createExcel(ExcelExportConfig config) {
    return ExcelType.ofExtension(config.getExcelType()).createWorkbook();
  }

  /**
   * 根据配置选项配置{@code Sheet}
   * @param sheet 要配置的{@code Sheet}对象
   * @param exportConfig 配置提供类
   */
  private static void configSheetIfNecessary(Sheet sheet, ExcelExportConfig exportConfig, String[] headers) {
    if (exportConfig.isGrowWidthWithCharacterLength()) {
      int len = headers.length;
      for (int i = 0; i < len; i++) {
        String headerName = headers[i];
        if (hasChineseContent(headerName)) {
          sheet.setColumnWidth(i, headerName.length() * 1500);
        }
        else if (headerName.length() < 5) {
          sheet.setColumnWidth(i, headerName.length() * 1000);
        }
        else {
          sheet.setColumnWidth(i, headerName.length() * 500);
        }
      }
    }
    if (Objects.nonNull(exportConfig.getSheetConfig())) {
       exportConfig.getSheetConfig().customConfigureSheet(sheet);
    }
  }

  /**
   * 检测是否包含中文字符
   * @param str 要检测的字符串
   * @return 如果包含中文字符，那么返回{@code true}，否则返回{@code false}
   */
  private static boolean hasChineseContent(String str) {
    return str.length() != str.getBytes().length;
  }

  /**
   * 生成excel头部信息
   * @param sheet 对应的{@code Sheet}
   * @param headers 所有的头部名称
   */
  private static int generateHeaderRow(Sheet sheet,
                                       String[] headers,
                                       Workbook workbook,
                                       ExcelExportConfig config) {
    Row header = sheet.createRow(0);
    Font headerFont = workbook.createFont();
    headerFont.setFontName(config.getCellFontName());
    headerFont.setFontHeightInPoints((short) 12);
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(headerFont);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    int len = headers.length;
    for (int i = 0; i < len; i++) {
      Cell cell = header.createCell(i, CellType.STRING);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(headers[i]);
    }
    return 1;
  }
}