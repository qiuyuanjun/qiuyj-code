package com.qiuyj.mybatis.checker;

import com.qiuyj.commons.AnnotationUtils;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author qiuyj
 * @since 2017/11/20
 */
public class OrderdCheckerComparator implements Comparator<ConditionChecker> {

  @Override
  public int compare(ConditionChecker o1, ConditionChecker o2) {
    Order order1 = AnnotationUtils.findAnnotation(o1.getClass(), Order.class);
    Order order2 = AnnotationUtils.findAnnotation(o2.getClass(), Order.class);
    if (Objects.isNull(order1) && Objects.isNull(order2))
      return 0;
    else if (Objects.isNull(order1))
      return -1;
    else if (Objects.isNull(order2))
      return 1;
    else
      return Integer.compare(order1.value(), order2.value());
  }
}