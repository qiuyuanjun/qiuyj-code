package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Enumerated;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 检查当前字段是否是枚举类型，如果是枚举类型，那么就设置其TypeHandler
 * @author qiuyj
 * @since 2017/12/14
 */
public class EnumTypeConditionChecker implements ConditionChecker {

  @Override
  @SuppressWarnings("unchecked")
  public ReturnValue doCheck(Field field, SqlInfo sqlInfo, ReturnValue preRv) {
    Enumerated enumerated = AnnotationUtils.findAnnotation(field, Enumerated.class);
    if (Objects.isNull(enumerated)) {
      if (Objects.isNull(preRv.fieldMethod)) {
        try {
          preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field));
        } catch (Exception e) {
          // ignore
        }
      }
      if (Objects.nonNull(preRv.fieldMethod))
        enumerated = AnnotationUtils.findAnnotation(preRv.fieldMethod, Enumerated.class);
    }
    if (Objects.nonNull(enumerated)) {
      Class<?> type = getFieldJavaType(field);
      if (type.isEnum()) {
        PropertyColumnMapping enumMapping = sqlInfo.getPropertyColumnMappingByPropertyName(field.getName());
        TypeHandler enumTypeHandlerType = enumerated.type() == Enumerated.ValueType.ORDINAL
            ? new EnumOrdinalTypeHandler(type) : new EnumTypeHandler(type);
        enumMapping.setTypeHandler(enumTypeHandlerType);
        enumMapping.setJdbcType(enumerated.jdbcType());
        sqlInfo.setHasEnumField();
      }
    }
    preRv.intValue = ConditionChecker.CONTINUE_EXECUTION;
    return preRv;
  }
}