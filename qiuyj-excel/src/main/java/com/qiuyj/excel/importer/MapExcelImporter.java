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
public class MapExcelImporter extends AbstractExcelImporter<Map> {

  private static final String VALIDATE_FIELD_PREFIX = "*";

  private MapValidator mapValidator;

  public MapExcelImporter(Workbook workbook) {
    super(workbook);
    TreeMap<Integer, String> excelHeadInfo = new TreeMap<>();
    // 需要验证的字段
    List<String> mustbeValidate = new ArrayList<>();
    // 遍历所有的head字段
    for (Map.Entry<Integer, String> me : getExcelHeadInfo().entrySet()) {
      if (me.getValue().startsWith(VALIDATE_FIELD_PREFIX)) {
        String name = canonicName(me.getValue());
        excelHeadInfo.put(me.getKey(), name);
        mustbeValidate.add(name);
      }
      else {
        excelHeadInfo.put(me.getKey(), me.getValue());
      }
    }
    // 重新设置头信息
    setExcelHeadInfo(excelHeadInfo);

    // 生成MapValidator
    if (!mustbeValidate.isEmpty()) {
      mapValidator = new MapValidator(mustbeValidate);
    }
  }

  @Override
  protected Object excelRowMapping(Row currRow) throws ValidationException {
    Map<Integer, String> headInfo = getExcelHeadInfo();
    Map<String, String> result = new LinkedHashMap<>(headInfo.size());
    // 如果读取到的所有的列都是空的，那么这一行数据应该丢弃
    int emptyCells = 0;
    for (Map.Entry<Integer, String> me : headInfo.entrySet()) {
      Cell cell = currRow.getCell(me.getKey(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      if (Objects.nonNull(cell)) {
        result.put(me.getValue(), ExcelUtils.readExcelCellValueAsString(cell));
      }
      else {
        emptyCells++;
      }
    }
    if (emptyCells == headInfo.size()) {
      // 那么表明读取到的是一整行空行，应该丢弃这行数据
      // 此时直接返回{@code SKIP}对象
      return SKIP;
    }
    else {
      // 对这个map进行验证
      if (Objects.nonNull(mapValidator)) {
        mapValidator.validateWithException(result);
      }
      return result;
    }
  }

  private static String canonicName(String name) {
    while (name.startsWith(VALIDATE_FIELD_PREFIX)) {
      name = name.substring(1);
    }
    return name;
  }
}