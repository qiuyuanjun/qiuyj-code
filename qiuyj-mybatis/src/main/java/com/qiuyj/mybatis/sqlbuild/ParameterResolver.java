package com.qiuyj.mybatis.sqlbuild;

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
  public static ParameterResolverResult resolveParameter(Object parameterObject) {
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
        // 按照param1,param2,param3...下标为5的字符从小到大排序
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
        DefaultSqlSession.StrictMap<?> sMap = (DefaultSqlSession.StrictMap<?>) parameterObject;
        Object obj;
        // 根据mybatis对参数的封装，这里的StrictMap的size只有两种可能
        // 要么是2，要么是1
        if (sMap.size() == 2) {
          // 如果size为2，那么表明一定是集合类型
          obj = sMap.get("collection");
        }
        else {
          // 否则，size为1，那么可能有两种情况，
          // 要么是一个数组，要么是一个Collection类型的集合
          // 但是这里可以不用管，直接获取map的values
          obj = sMap.values().toArray()[0];
        }
        parameterType = new Class<?>[] {obj.getClass()};
        parameterObjects = new Object[] {obj};
        /*try {
          // 首先尝试获取一下数组
          obj = sMap.get("array");
          parameterType = new Class<?>[] {obj.getClass()};
          parameterObjects = new Object[] {obj};
        } catch (BindingException e) {
          // 如果抛出异常，那么表明不是数组，而是一个集合
          // 由于mybatis的StrictMap的get方法重写了HashMap的get方法
          // 如果get一个不存在的key，那么就会抛出异常，所以这里需要捕获BindingException异常
          obj = sMap.get("collection");
          parameterType = new Class<?>[] {obj.getClass()};
          parameterObjects = new Object[] {obj};
        }*/
      }
      // 这种情况是只有一个参数，并且这个参数的类型就是Map类型
      else {
        parameterType = new Class<?>[] {parameterObject.getClass()};
        parameterObjects = new Object[] {parameterObject};
      }
    }
    else if (Objects.isNull(parameterObject)) {
      parameterType = new Class<?>[0];
      parameterObjects = new Object[0];
    }
    else {
      parameterType = new Class<?>[] {parameterObject.getClass()};
      parameterObjects = new Object[] {parameterObject};
    }
    return new ParameterResolverResult(parameterType, parameterObjects);
  }

  public static final class ParameterResolverResult {
    private Class<?>[] parameterTypes;
    private Object[] parameterValues;

    public ParameterResolverResult(Class<?>[] paramterTypes, Object[] parameterValues) {
      this.parameterTypes = paramterTypes;
      this.parameterValues = parameterValues;
    }

    public Class<?>[] getParameterTypes() {
      return parameterTypes;
    }

    public Object[] getParameterValues() {
      return parameterValues;
    }
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