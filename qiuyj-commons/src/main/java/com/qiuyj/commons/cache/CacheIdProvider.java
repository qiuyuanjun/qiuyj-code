package com.qiuyj.commons.cache;

/**
 * @author qiuyj
 * @since 2018/1/1
 */
public interface CacheIdProvider {

  /**
   * 设置缓存的唯一id，用于缓存的管理，如果为null，那么不会加入到CacheManager里面
   */
  String getId();
}
