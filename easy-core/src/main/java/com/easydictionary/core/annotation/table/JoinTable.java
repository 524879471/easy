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
public @interface JoinTable {
    public static final String INNER= "inner";
    public static final String RIGHT= "right";
    public static final String LEFT= "left";

    public String table() default "";
    public String type() default "inner";
    public String on() default "";//  如this.id=id 或者 table1.id = id  右边是目标表得字段
}