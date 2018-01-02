package com.qiuyj.commons.cache.impl;

import com.qiuyj.commons.cache.Cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于jvm的软引用的缓存（当jvm堆内存达到上线，那么此缓存将被释放从而达到释放内存）
 * @author qiuyj
 * @since 2018/1/1
 */
public class SoftReferenceCache<K, V> implements Cache<K, V> {
  private final ReferenceQueue<V> garbageQueue;
  private final ConcurrentMap<K, SoftReferenceEntry<K, V>> cache;

  public SoftReferenceCache() {
    garbageQueue = new ReferenceQueue<>();
    cache = new ConcurrentHashMap<>();
  }

  public SoftReferenceCache(int cap) {
    garbageQueue = new ReferenceQueue<>();
    cache = new ConcurrentHashMap<>(cap);
  }

  @Override
  public V getValue(K cacheKey) {
    V result = null;
    SoftReferenceEntry<K, V> entry = cache.get(cacheKey);
    if (Objects.nonNull(entry)) {
      result = entry.get();
      if (Objects.isNull(result)) {
        // 表明此时缓存的值已经被jvm回收了
        // 那么此时，缓存值所引用的SoftReference引用必须从缓存中移除
        cache.remove(cacheKey);
      }
    }
    return result;
  }

  @Override
  public void setValue(K cacheKey, V cacheValue) {
    clearGarbageItemInQueue();
    if (Objects.isNull(cacheValue)) {
      removeValue(cacheKey);
    }
    else {
      cache.put(cacheKey, new SoftReferenceEntry<>(cacheKey, cacheValue, garbageQueue));
    }
  }

  @Override
  public boolean removeValue(K cacheKey) {
    clearGarbageItemInQueue();
    return Objects.nonNull(cache.remove(cacheKey));
  }

  @Override
  public int size() {
    clearGarbageItemInQueue();
    return cache.size();
  }

  @Override
  public void clear() {
    clearGarbageItemInQueue();
    cache.clear();
  }

  @SuppressWarnings("unchecked")
  private void clearGarbageItemInQueue() {
    SoftReferenceEntry<K, V> entry;
    while (Objects.nonNull(entry = (SoftReferenceEntry<K, V>) garbageQueue.poll())) {
      cache.remove(entry.key);
    }
  }

  private static final class SoftReferenceEntry<K, V> extends SoftReference<V> {
    private final K key;

    SoftReferenceEntry(K key, V softValue, ReferenceQueue<V> garbageQueue) {
      super(softValue, garbageQueue);
      this.key = key;
    }
  }
}