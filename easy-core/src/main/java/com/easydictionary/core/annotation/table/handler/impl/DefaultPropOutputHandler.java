package com.easydictionary.core.annotation.table.handler.impl;


import com.easydictionary.core.annotation.table.handler.PropOutputHander;

/**
 * @Description:
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public class DefaultPropOutputHandler implements PropOutputHander {
    @Override
    public Object handle(Object value) {
        return value;
    }
}
