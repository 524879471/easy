package com.easyframework.easydict.annotation;

import com.easyframework.easydict.handler.AbstractActionHandler;
import com.easyframework.easydict.handler.DefaultActionHandler;
import com.easyframework.easydict.handler.DefaultDictionaryDataHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字典值
 * 
 * @author 丁文渊
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DictElement {
	 String value();  //中文显示
	Class<? extends AbstractActionHandler> actionHandlerClass() default DefaultActionHandler.class;  //中文显示
}