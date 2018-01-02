package com.qiuyj.commons.cache;

/**
 * 缓存的主接口，所有实现给用户使用的子类均必须在同步环境下使用不会出错
 * @author qiuyj
 * @since 2018/1/1
 */
public interface Cache<K, V> {

  /**
   * 根据key得到对应的缓存，如果没有，那么返回null
   * @param cacheKey 缓存的key
   */
  V getValue(K cacheKey);

  /**
   * 设置缓存值
   * @param cacheKey 缓存的key
   * @param cacheValue 缓存的值
   */
  void setValue(K cacheKey, V cacheValue);

  /**
   * 移除缓存key对应的缓存值
   * @param cacheKey 缓存key
   * @return 如果成功移除了缓存，那么返回true，否则返回false
   */
  boolean removeValue(K cacheKey);

  /**
   * 返回当前缓存中的大小
   */
  int size();

  /**
   * 清空所有的缓存
   */
  void clear();
}