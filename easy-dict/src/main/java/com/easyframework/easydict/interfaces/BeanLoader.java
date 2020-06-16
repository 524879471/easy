package com.easyframework.easydict.interfaces;

public interface BeanLoader<T> {
    T loadBean(Class<? extends T> clazz) ;
}
