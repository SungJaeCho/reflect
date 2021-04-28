package com.cos.reflect.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
//해당 어노테이션 시점 세팅 (컴파일시일지 런타임일지..)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	String value();
}
