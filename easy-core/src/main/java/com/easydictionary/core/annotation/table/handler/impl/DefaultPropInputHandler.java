package com.easydictionary.core.annotation.table.handler.impl;

import com.easydictionary.core.annotation.table.handler.PropInputHander;

/**
 * @Description:
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public class DefaultPropInputHandler implements PropInputHander {
    @Override
    public Object handle(Object value, Object model) {
        return value;
    }
}
