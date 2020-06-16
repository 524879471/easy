package com.easyframework.easydict.handler;

public abstract class AbstractActionHandler<T> {
    public abstract String handle(Object code,T... object);

}
