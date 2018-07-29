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
public class BeanExcelImporter<T> extends AbstractExcelImporter<T> {

  private final Class<T> beanCls;

  public BeanExcelImporter(Workbook workbook, Class<T> beanCls) {
    super(verifyBeanClassAndReturnWorkbool(workbook, beanCls));
    this.beanCls = beanCls;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected T excelRowMapping(Row currRow) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(beanCls);
    getExcelHeadInfo().forEach((idx, title) -> {

    });
    return (T) beanWrapper.getWrappedInstance();
  }

  private static Workbook verifyBeanClassAndReturnWorkbool(Workbook workbook, Class<?> beanClass) {
    if (Objects.isNull(beanClass)) {
      throw new NullPointerException("beanClass == null.");
    }
    else if (beanClass.isInterface()) {
      throw new IllegalArgumentException("Bean class cannot be an interface.");
    }
    return workbook;
  }
}