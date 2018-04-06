package com.qiuyj.mybatis;

import com.qiuyj.mybatis.mapper.Mapper;
import org.apache.ibatis.io.ResolverUtil;

import java.util.Objects;

/**
 * 判断一个接口是否是Mapper接口
 * @author qiuyj
 * @since 2018/3/29
 */
public class MapperTest implements ResolverUtil.Test {

  private final Class<? extends Mapper> baseMapperClass;

  public MapperTest(Class<? extends Mapper> baseMapperClass) {
    this.baseMapperClass = Objects.requireNonNull(baseMapperClass);
  }

  @Override
  public boolean matches(Class<?> type) {
    return Objects.nonNull(type)
        && type.isInterface()
        && type != baseMapperClass
        && baseMapperClass.isAssignableFrom(type);
  }
}