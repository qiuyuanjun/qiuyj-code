package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.mybatis.SqlInfo;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author qiuyj
 * @since 2017/11/20
 */
public final class CheckerChain {
  public static final Comparator<ConditionChecker> DEFAULT_ORDERD_COMPARATOR = new OrderdCheckerComparator();
  /**
   * 检查器链
   */
  private final List<ConditionChecker> chain = new ArrayList<>();
  public CheckerChain() {
    chain.add(new TableAnnotationChecker());
    chain.add(new StaticConditionChecker());
    chain.add(new IgnoreAnnotationChecker());
    chain.add(new FieldCountConditionChecker());
    chain.add(new PrimaryKeyAnnotationChecker());
    chain.add(new ColumnAnnotationChecker());
  }

  /**
   * 执行所有的检查器
   */
  public void checkAll(Field field, SqlInfo sqlInfo) {
    Iterator<ConditionChecker> it = chain.iterator();
    while (it.hasNext()) {
      ConditionChecker next = it.next();
      int continueExecution = next.doCheck(field, sqlInfo);
      if (continueExecution < 0)
        break;
      else if (continueExecution > 0)
        skip(continueExecution, it);
    }
  }

  /**
   * 直接在检查器链末尾添加一个检查器
   */
  public void addChecker(ConditionChecker checker) {
    Objects.requireNonNull(checker);
    chain.add(checker);
  }

  public void addCheckerUnsorted(List<ConditionChecker> unsortedCheckerList) {
    Objects.requireNonNull(unsortedCheckerList);
    if (unsortedCheckerList.size() != 0) {
      unsortedCheckerList.sort(DEFAULT_ORDERD_COMPARATOR);
      chain.addAll(unsortedCheckerList);
    }

  }

  /**
   * 跳过检查器
   * @param n 要跳过的检查器的个数
   * @param it 检查器链的迭代器
   */
  private static void skip(int n, Iterator it) {
    for (int i = 0; i < n; i++) {
      it.next();
    }
  }
}