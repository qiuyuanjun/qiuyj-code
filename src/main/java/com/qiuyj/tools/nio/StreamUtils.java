package com.qiuyj.tools.nio;

import com.qiuyj.tools.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * 二进制流操作工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class StreamUtils {
  static final int BUF_SIZE = 8192; // 8Kb

  /**
   * 获取指定文件的字节数组
   * @throws IOException 当读取文件或打开文件失败的时候
   */
  public static byte[] getBytesFromFile(String path) throws IOException {
    path = StringUtils.cleanPath(path);
    if (Objects.isNull(path))
      throw new IllegalArgumentException("Parameter 'path' cannot be null");
    FileChannel in = null;
    ByteArrayOutputStream out = null;
    byte[] b;
    try {
      in = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
      long fileLen = in.size();
      MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0L, fileLen);
      int cycle = (int) fileLen / BUF_SIZE,
          last = (int) fileLen % BUF_SIZE;
      if (cycle > 0) {
        out = new ByteArrayOutputStream(BUF_SIZE);
        b = new byte[BUF_SIZE];
        for (int i = 0; i < cycle; i++) {
//          buf.flip();  这里无需flip，因为这个buf一直是读模式，没有从读模式变成写模式
          buf.get(b);
          out.write(b);
//          buf.clear(); // 这里一定不能用clear，这样会将position重新置为0，导致一直重复读
        }
      } else {
        out = new ByteArrayOutputStream(last);
        b = new byte[last];
      }
      if (last > 0) {
        buf.get(b, 0, last);
        out.write(b, 0, last);
      }
      b = out.toByteArray();
    } finally {
      closeQuietly(in);
      closeQuietly(out);
    }
    return b;
  }

  /**
   * 获取输入流的字节数组
   * @param in 输入流
   * @param close 是否需要关闭输入流
   * @return 字节数组
   * @throws IOException 当读取输入流报错的时候
   */
  public static byte[] getBytesFromStream(InputStream in, boolean close) throws IOException {
    ReadableByteChannel inChannel = Channels.newChannel(in);
    ByteArrayOutputStream out = null;
    byte[] b;
    try {
      ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
      out = new ByteArrayOutputStream(BUF_SIZE);
      int len;
      while ((len = inChannel.read(buf)) > -1) {
//        buf.flip();  这里无需flip，因为一直是读模式
        out.write(buf.array(), 0, len);
        buf.clear(); // 这里一定要clear，具体原因参考javaAPI
      }
      b = out.toByteArray();
    } finally {
      closeQuietly(out);
      if (close)
        closeQuietly(inChannel);
    }
    return b;
  }

  /**
   * 安静的关闭一个流（输入流或输出流），如果关闭流的时候抛出异常，那么忽略该异常
   */
  public static void closeQuietly(Closeable stream) {
    if (Objects.nonNull(stream)) {
      try {
        stream.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

}