package com.easydictionary.core.annotation.table.handler;

/**
 * @Description:   数据写入时的适配器接口
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public interface ModelOutputHandler<T> {
    public T handle(T model);
}
