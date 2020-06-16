package com.easydictionary.core.annotation.table.handler;

/**
 * @Description: 数据输出时的适配器接口
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public interface PropOutputHander<T> {
    public Object handle(Object value);
}
