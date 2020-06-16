package com.easydictionary.core.annotation.table;

import com.easydictionary.core.annotation.table.handler.ModelOutputHandler;
import com.easydictionary.core.annotation.table.handler.impl.DefaultModelOutputHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>查询表格注解</p>
 * 
 * @author 丁文渊
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableModel {
	public String name();
	public String desc() default "";
	public String table() default "";
	public String sql() default "";
	public boolean ds1() default false;
	public boolean sort() default true;
	public Class<? extends ModelOutputHandler> outputHandler() default DefaultModelOutputHandler.class;
}