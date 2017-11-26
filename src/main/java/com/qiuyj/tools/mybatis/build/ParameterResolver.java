package com.qiuyj.tools.mybatis.build;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 参数解析器
 * @author qiuyj
 * @since 2017/11/25
 */
public class ParameterResolver {

  /**
   * 解析传入Mybatis的参数
   * @param parameterObject 参数
   * @return 解析参数后的二维数组，数组的第一个是所有参数的类型的数组，数组的第二个所有参数对象的数组
   */
  public static Object[][] resolveParameter(Object parameterObject) {
    // 判断参数类型
    // 如果参数类型是Map，那么有三种可能
    // 第一种是这个方法有多个参数
    // 第二种是该方法只有一个参数，但是这个参数是Collection类型或者数组类型
    // 第三种是该方法也是只有一个参数，但是这个参数原本就是一个Map类型
    Class<?>[] parameterType;
    Object[] parameterObjects;
    if (parameterObject instanceof Map) {
      // 这种情况是有多个参数的情况
      if (MapperMethod.ParamMap.class.isInstance(parameterObject)) {
        MapperMethod.ParamMap<?> pMap = (MapperMethod.ParamMap<?>) parameterObject;
        // 按照param1,param2,param3...下标为5的字符从小到达排序
        List<String> paramList = pMap.keySet()
                                     .stream()
                                     .filter(str -> str.startsWith("param"))
                                     .sorted(new ParamStringIndexComparator())
                                     .collect(Collectors.toList());
        parameterType = new Class<?>[paramList.size()];
        parameterObjects = new Object[paramList.size()];
        int i = 0;
        for (String s : paramList) {
          Object value = pMap.get(s);
          parameterType[i] = value.getClass();
          parameterObjects[i++] = value;
        }
      }
      // 这种情况是只有一个参数，但是该参数是Collection类型或者是数组
      else if (DefaultSqlSession.StrictMap.class.isInstance(parameterObject)) {
        // 首先尝试获取一下数组
        DefaultSqlSession.StrictMap<?> sMap = (DefaultSqlSession.StrictMap<?>) parameterObject;
        Object arr = sMap.get("array");
        if (Objects.nonNull(arr)) {
          parameterType = new Class<?>[] {arr.getClass()};
          parameterObjects = new Object[] {arr};
        }
          // 如果不是数组，那么一定是集合类型
        else {
          Object collection = sMap.get("collection");
          parameterType = new Class<?>[] {collection.getClass()};
          parameterObjects = new Object[] {collection};
        }
      }
      // 这种情况是只有一个参数，并且这个参数的类型就是Map类型
      else {
        parameterType = new Class<?>[] {parameterObject.getClass()};
        parameterObjects = new Object[] {parameterObject};
      }
    } else if (Objects.isNull(parameterObject)) {
      parameterType = new Class<?>[0];
      parameterObjects = new Object[0];
    }
    else {
      parameterType = new Class<?>[] {parameterObject.getClass()};
      parameterObjects = new Object[] {parameterObject};
    }
    return new Object[][] {parameterType, parameterObjects};
  }

  private static final class ParamStringIndexComparator implements Comparator<String> {

    /**
     * 这里传入的是类似 param1,param2,param3...这样的字符串，只需要比较下标为5的字符的大小即可
     */
    @Override
    public int compare(String o1, String o2) {
      return o1.charAt(5) - o2.charAt(5);
    }
  }
}