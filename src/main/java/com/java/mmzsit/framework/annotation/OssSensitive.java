package com.java.mmzsit.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: OssPrivateAnnotation.java
 * @Author: jinchuanchuan@longfor.com
 * @Date: 2022/1/10 1:53 下午:00
 * @Description: OSS信息脱敏
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OssSensitive {

}
