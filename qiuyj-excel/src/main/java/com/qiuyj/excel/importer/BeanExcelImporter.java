package com.qiuyj.excel.importer;

import com.qiuyj.commons.bean.wrapper.BeanWrapper;
import com.qiuyj.commons.bean.wrapper.BeanWrapperImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/12/31
 */
public class BeanExcelImporter extends AbstractExcelImporter {

  private final Class<?> beanCls;

  public BeanExcelImporter(Workbook workbook, Class<?> beanCls) {
    super(workbook);
    this.beanCls = Objects.requireNonNull(beanCls);
  }

  @Override
  protected Object excelRowMapping(Row currRow) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    getExcelHeadInfo().forEach((idx, title) -> {

    });
    return beanWrapper.getWrappedInstance();
  }
}