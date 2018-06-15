package com.qiuyj.mybatis;

import com.qiuyj.mybatis.mapper.Mapper;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * 判断一个接口是否是Mapper接口
 * @author qiuyj
 * @since 2018/3/29
 */
public class MapperTest implements Predicate<Class<?>> {

  private final Class<? extends Mapper> baseMapperClass;

  public MapperTest(Class<? extends Mapper> baseMapperClass) {
    this.baseMapperClass = Objects.requireNonNull(baseMapperClass);
  }

  @Override
  public boolean test(Class<?> type) {
    return Objects.nonNull(type)
        && type.isInterface()
        && type != baseMapperClass
        && baseMapperClass.isAssignableFrom(type);
  }
}