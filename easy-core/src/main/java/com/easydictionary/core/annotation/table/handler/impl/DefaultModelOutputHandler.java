package com.easydictionary.core.annotation.table.handler.impl;


import com.easydictionary.core.annotation.table.handler.ModelOutputHandler;

/**
 * @Description:
 * @Date: 2019/5/4
 * @Auther: dwy
 */
public class DefaultModelOutputHandler implements ModelOutputHandler {
    @Override
    public Object handle(Object value) {
        return value;
    }
}
