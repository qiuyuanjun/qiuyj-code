package com.qiuyj.mybatis;

import com.qiuyj.mybatis.mapper.Mapper;
import org.apache.ibatis.io.ResolverUtil;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2018/3/29
 */
public class MapperTest implements ResolverUtil.Test {

  private final Class<? extends Mapper> baseMapperClass;

  public MapperTest(Class<? extends Mapper> baseMapperClass) {
    this.baseMapperClass = baseMapperClass;
  }

  @Override
  public boolean matches(Class<?> type) {
    return Objects.nonNull(type)
        && type.isInterface()
        && baseMapperClass.isAssignableFrom(type)
        && type != baseMapperClass;
  }
}