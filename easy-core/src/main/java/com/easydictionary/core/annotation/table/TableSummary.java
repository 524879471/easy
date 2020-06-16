package com.easydictionary.core.annotation.table;


import com.easydictionary.core.annotation.table.handler.TableSummaryHandler;
import com.easydictionary.core.annotation.table.handler.impl.DefaultTableSummaryHandler;

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
public @interface TableSummary {
    public String name();
    public Class<? extends TableSummaryHandler> handler() default DefaultTableSummaryHandler.class;
}
