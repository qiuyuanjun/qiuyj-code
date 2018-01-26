package com.qiuyj.asm;

import org.objectweb.asm.Opcodes;

/**
 * @author qiuyj
 * @since 2018/1/26
 */
public interface ASMVersion {

  /**
   * ASM版本，如果升级asm版本，那么只需要修改这里即可
   */
  int ASM_VERSION = Opcodes.ASM5;
}