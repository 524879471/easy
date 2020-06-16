package com.easydictionary.core.interfaces;

public interface BeanLoader {
   <T> T load(Class<T>  clazz);
}
