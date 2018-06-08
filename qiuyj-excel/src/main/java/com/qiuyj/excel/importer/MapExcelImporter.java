package com.qiuyj.excel.importer;

import com.qiuyj.commons.validate.ValidationException;
import com.qiuyj.commons.validate.impl.MapValidator;
import com.qiuyj.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public class MapExcelImporter extends AbstractExcelImporter {

  private static final String VALIDATE_FIELD_PREFIX = "*";

  private MapValidator mapValidator;

  public MapExcelImporter(Workbook workbook) {
    super(workbook);
    TreeMap<Integer, String> excelHeadInfo = new TreeMap<>();
    // 需要验证的字段
    List<String> mustbeValidate = new ArrayList<>();
    boolean resetExcelHeadInfo = false;
    // 遍历所有的head字段
    for (Map.Entry<Integer, String> me : getExcelHeadInfo().entrySet()) {
      if (me.getValue().startsWith(VALIDATE_FIELD_PREFIX)) {
        resetExcelHeadInfo = true;
        String name = canonicName(me.getValue());
        excelHeadInfo.put(me.getKey(), name);
        mustbeValidate.add(name);
      }
      else {
        excelHeadInfo.put(me.getKey(), me.getValue());
      }
    }
    if (resetExcelHeadInfo) {
      setExcelHeadInfo(excelHeadInfo);
    }

    // 生成MapValidator
    if (!mustbeValidate.isEmpty()) {
      mapValidator = new MapValidator(mustbeValidate);
    }
  }

  @Override
  protected Object excelRowMapping(Row currRow) throws ValidationException {
    Map<Integer, String> headInfo = getExcelHeadInfo();
    Map<String, String> result = new LinkedHashMap<>(headInfo.size());
    headInfo.forEach((idx, title) -> {
      Cell cell = currRow.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      if (Objects.nonNull(cell)) {
        result.put(title, ExcelUtils.readExcelCellValueAsString(cell));
      }
    });
    // 对这个map进行验证
    if (Objects.nonNull(mapValidator)) {
      mapValidator.validateWithException(result);
    }
    return result;
  }

  private static String canonicName(String name) {
    while (name.startsWith(VALIDATE_FIELD_PREFIX)) {
      name = name.substring(1);
    }
    return name;
  }
}