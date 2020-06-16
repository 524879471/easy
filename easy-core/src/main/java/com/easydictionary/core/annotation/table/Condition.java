package com.easydictionary.core.annotation.table;

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
public @interface Condition {
    public static final String TEXT= "text";
    public static final String DATE= "date";
    public static final String SELECT= "select";
    public static final String AUTOINPUT= "autoInput";
    public static final String RADIO= "radio";
    public static final String CHECKBOX= "checkbox";

    public static final String TYPE_IN= "in";
    public static final String TYPE_LIKE= "like";


    public boolean isSearchItem() default false;

    public String searchType() default "text"; //指定查询赛选器的类型，如text,date,autoInput, radio, checkbox

    public String regex() default "text"; //

    public  Class<?> dictModel() default Object.class;

    public  Class<?> tableModel() default Object.class;

    public  String valueKey() default "";

    public  String textKey() default "";

    public  String[] filter() default {};


    public String type() default "=";   //比如 = ,like, >, <,

    public String sql() default "";   //如果比较复杂 可以写sql  比如id = ？

    public String orWith() default "and"; //默认为and 加此属性时和其它条件组成or语句

    public String column() default ""; //

    public String tableTopic() default ""; //
}