package com.easydictionary.core.annotation.table.handler;

import java.util.Collection;

/**
 * @Description:
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public interface TableSummaryHandler<T> {
    public Object handler(Collection<T> modelList);
}