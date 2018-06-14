package com.qiuyj.commons.resource;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qiuyj
 * @since 2018-06-14
 */
public class ProtocolResourceHandlerFactory {

  private static final ConcurrentMap<String, ProtocolResourceHandler> CACHED_PROTOCOL_RESOURCE_HANDLER
      = new ConcurrentHashMap<>();

  static {
    CACHED_PROTOCOL_RESOURCE_HANDLER.put("file", new ProtocolResourceHandler.FileProtocolResourceHandler());
    CACHED_PROTOCOL_RESOURCE_HANDLER.put("jar", new ProtocolResourceHandler.JarProtocolResourceHandler());
  }

  public static ProtocolResourceHandler getProtocolResourceHandler(String protocol) {
    return Objects.isNull(protocol) ? null : CACHED_PROTOCOL_RESOURCE_HANDLER.get(protocol);
  }
}
