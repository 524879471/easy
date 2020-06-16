package com.easydictionary.core.annotation.table;

import com.easydictionary.core.annotation.table.handler.PropInputHander;
import com.easydictionary.core.annotation.table.handler.PropOutputHander;
import com.easydictionary.core.annotation.table.handler.impl.DefaultPropInputHandler;
import com.easydictionary.core.annotation.table.handler.impl.DefaultPropOutputHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>controller</p>
 * 
 * @author 丁文渊
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelField {
	public static String POLY_SUM = "sum";
	public static String POLY_COUNT = "count";
	public static String POLY_MAX= "max";
	public static String POLY_MIN = "min";
	public static String POLY_AVG = "avg";


	public String name();
    public int width() default 0;
	public  String parent() default "";
	public  boolean makeTotal() default false;
	public Class<? extends PropOutputHander> outputHander() default DefaultPropOutputHandler.class;
	public Class<? extends PropInputHander> inputHander() default DefaultPropInputHandler.class;
	public  boolean amountFormat() default false;
	public  String dateFormat() default "";
	/**保留几位小数*/
	public  int radixPoint() default 0;
	/**将编码显示为文字 格式 1=未开始&2=进行中&3=未完成  */
	public  String dict() default "";
	public  Class<?> dictModel() default Object.class;
	public String column() default "";
	public String tableTopic() default "";


	public boolean key() default false;
	public boolean show() default true;
	/**聚合方式  */
	public String poly() default "";
	/**聚合前是否去重  默认是  */
	public boolean distinct() default true;
	/**分组优先级*/
	public int groupLevel() default 0;
	/**  提取sql*/
	public String sql() default "";
	/**新增时的默认值*/



	/**废弃的*/
	public boolean ch() default false;
}