package com.qiuyj.excel.exporter;

import com.qiuyj.excel.config.ExcelExportConfig;
import com.qiuyj.excel.config.ExcelHeaderConfig;

import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018-08-04
 */
public class MapExcelExporter extends AbstractExcelExporter<Map<String, Object>> {

  public MapExcelExporter(ExcelHeaderConfig[] headers) {
    super(headers);
  }

  public MapExcelExporter(ExcelHeaderConfig[] headers, ExcelExportConfig exportConfig) {
    super(headers, exportConfig);
  }

  @Override
  protected String getCellValue(Map<String, Object> data, String title) {
    Object obj = data.get(title);
    return Objects.nonNull(obj) ? obj.toString() : null;
  }
}
