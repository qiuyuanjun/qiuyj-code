package com.qiuyj.tools.mybatis.checker;

import com.qiuyj.tools.AnnotationUtils;
import com.qiuyj.tools.ReflectionUtils;
import com.qiuyj.tools.mybatis.PropertyColumnMapping;
import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.annotation.Enumerated;
import com.qiuyj.tools.mybatis.typehandler.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;

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
    Class<?> type = getFieldJavaType(field);
    if (Objects.isNull(enumerated)) {
      if (Objects.isNull(preRv.fieldMethod)) {
        try {
          preRv.fieldMethod = ReflectionUtils.getDeclaredMethod(sqlInfo.getBeanType(), fieldToGetterName(field), type);
        } catch (Exception e) {
          // ignore
        }
      }
      if (Objects.nonNull(preRv.fieldMethod))
        enumerated = AnnotationUtils.findAnnotation(preRv.fieldMethod, Enumerated.class);
    }
    if (Objects.nonNull(enumerated)) {
      if (type.isEnum()) {
        PropertyColumnMapping enumMapping = sqlInfo.getPropertyColumnMappingByPropertyName(field.getName());
        Enumerated.ValueType valueType = enumerated.type();
        if (valueType == Enumerated.ValueType.ORDINAL)
          enumMapping.setTypeHandler(new EnumOrdinalTypeHandler(type));
        else
          enumMapping.setTypeHandler(new EnumTypeHandler(type));
      }
    }
    preRv.intValue = ConditionChecker.CONTINUE_EXECUTION;
    return preRv;
  }
}