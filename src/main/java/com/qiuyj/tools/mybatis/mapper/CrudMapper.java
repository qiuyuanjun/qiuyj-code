package com.qiuyj.tools.mybatis.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 单表crud操作接口
 * @author qiuyj
 * @since 2017/11/11
 */
public interface CrudMapper<ID, T> {

  /**
   * 插入一条数据
   * @param entity 要插入的数据
   */
  @InsertProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  void insert(T entity);

  /**
   * 根据主键查询一条数据
   * @param id 要查询的主键
   * @return 查询到的对象
   */
  @SelectProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  T selectOne(ID id);

  /**
   * 根据条件查询，返回一个集合
   * @param example 查询条件，这里仅仅操作不为null的引用数据类型
   */
  @SelectProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  List<T> selectList(T example);

  /**
   * 根据主键更新，注意，该方法仅仅更新引用类型，对于基本数据类型不做处理
   * @param updated 要更新的对象
   */
  @UpdateProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  void update(T updated);

  /**
   * 根据主键删除
   * @param id 要删除的主键
   */
  @DeleteProvider(type = Mapper.SqlProvider.class, method = "dynamicSql")
  void delete(ID id);
}