package com.easyframework.easydict.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DictionaryNotFoundException extends RuntimeException {
    public DictionaryNotFoundException(String msg,ClassNotFoundException e){
        super(msg);
        e.printStackTrace();
    }
}
