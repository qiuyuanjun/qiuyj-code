package com.qiuyj.tools.mybatis.engine;

import com.qiuyj.tools.mybatis.MapperMethodResolver;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import com.qiuyj.tools.mybatis.checker.CheckerChain;
import com.qiuyj.tools.mybatis.key.OracleSequenceDialect;
import com.qiuyj.tools.mybatis.key.SequenceDialect;
import com.qiuyj.tools.mybatis.key.SequenceKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class OracleSqlGeneratorEngine extends AbstractSqlGeneratorEngine {
  private final SequenceDialect oracleSequenceDialect;

  protected OracleSqlGeneratorEngine(CheckerChain chain, SqlProvider sqlProvider, MapperMethodResolver resolver) {
    super(chain, sqlProvider, resolver);
    oracleSequenceDialect = new OracleSequenceDialect();
  }

  @Override
  protected void generateSequenceKey(MappedStatement ms, MetaObject msMetaObject, SqlInfo sqlInfo) {
    // 如果原来的KeyGenerator不是SequenceKeyGenerator
    // 那么需要设置
    // 否则，无需重复设置
    if (!SequenceKeyGenerator.class.isInstance(ms.getKeyGenerator())) {
      String keyGenStr = oracleSequenceDialect.generateSequenceQueryString(sqlInfo.getSequenceName());
      msMetaObject.setValue("keyGenerator", new SequenceKeyGenerator(sqlInfo, keyGenStr));
    }
  }

}