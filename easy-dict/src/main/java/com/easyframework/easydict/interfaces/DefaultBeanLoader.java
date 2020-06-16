package com.easyframework.easydict.interfaces;

import com.easyframework.easydict.handler.AbstractActionHandler;

public class DefaultBeanLoader implements BeanLoader<AbstractActionHandler> {


    @Override
    public AbstractActionHandler loadBean(Class<? extends AbstractActionHandler> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
