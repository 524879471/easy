package com.easyframework.easydict.exception;

public class DictionaryFeildException extends RuntimeException {
    public DictionaryFeildException(String msg , IllegalAccessException e){
            super(msg);
            e.printStackTrace();
        }
}
