package com.easydictionary.core.annotation.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description:
 * @Date: 2019/5/4
 * @Auther: dwy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sort {
    public static String ASC = "asc";
    public static String DESC = "desc";
    public String value() default ASC;
}
