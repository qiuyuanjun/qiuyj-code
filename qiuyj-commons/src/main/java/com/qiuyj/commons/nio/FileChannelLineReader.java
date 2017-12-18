package com.qiuyj.commons.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * 一行一行的读取文件，类似BufferedReader.readLine方法
 * <note>该类是线程非安全的，应尽量保证不在多线程环境下使用该类</note>
 * @author qiuyj
 * @since 2017/12/6
 */
public class FileChannelLineReader {
  private static final String LINE_END_FLAG = "\r\n";
  private final FileChannel inChannel;// 要读取的文件的输入Channel
  private final boolean closeChannel; // 读完整个文件是否关闭
  private boolean isEOF;              // 是否是文件的结尾
  private final Charset UTF8 = Charset.forName("UTF-8");
  private long position;              // 当前读取文件的位置
  private final ByteBuffer temp = ByteBuffer.allocate(1);
  private final ByteBuffer currLineData = ByteBuffer.allocate(StreamUtils.BUF_SIZE);

  public FileChannelLineReader(FileChannel inChannel, boolean closeChannel) {
    Objects.requireNonNull(inChannel);
    ensureChannelOpen(inChannel);
    this.inChannel = inChannel;
    this.closeChannel = closeChannel;
  }

  public FileChannelLineReader(String path) throws FileNotFoundException {
    FileChannel inChannel;
    try {
      inChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
    } catch (IOException e) {
      throw new FileNotFoundException("Can not find file: " + path);
    }
    this.inChannel = inChannel;
    this.closeChannel = true;
  }

  public FileChannelLineReader(InputStream inStream, boolean closeStream) {
    if (inStream instanceof FileInputStream) {
      FileInputStream in = (FileInputStream) inStream;
      FileChannel channel = in.getChannel();
      ensureChannelOpen(channel);
      inChannel = channel;
      this.closeChannel = closeStream;
    } else
      throw new IllegalStateException("File input stream only");
  }

  private void ensureChannelOpen(FileChannel channel) {
    if (!channel.isOpen())
      throw new IllegalStateException("Closed channel");
  }

  public boolean hasNextLine() {
    return !isEOF;
  }

  public String readLine() {
    String line = null;
    temp.clear();
    currLineData.clear();
    boolean isLine = false;
    try {
      while (!isEOF && !isLine) {
        int idx = inChannel.read(temp);
        if (idx == -1) {
          // 读到了文件尾
          isEOF = true;
          continue;
        }
        temp.flip();
        position += idx;
        currLineData.put(temp);
        currLineData.flip();
        line = UTF8.decode(currLineData).toString();
        int lineIdx;
        if ((lineIdx = line.lastIndexOf(LINE_END_FLAG)) > -1) {
          isLine = true;
          line = line.substring(0, lineIdx);
          continue;
        }
        temp.clear();
        currLineData.position(currLineData.limit());
        currLineData.limit(currLineData.capacity());
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error while read file");
    }
    try {
      inChannel.position(position);
    } catch (IOException e) {
      throw new IllegalStateException("Error when setting the position of the file next reading");
    }
    if (isEOF && closeChannel)
      StreamUtils.closeQuietly(inChannel);
    return line;
  }

  public long position() {
    return position;
  }
}
