package com.qiuyj.commons.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 所有基于注解的验证规则都必须被当前注解标注
 * @author qiuyj
 * @since 2018-05-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ValidatorBaseAnnotation {
}
