package com.easyframework.easydict.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DictionaryNameRepeatException extends RuntimeException {
    public DictionaryNameRepeatException(String msg){
        super(msg);
        log.info(this.getMessage());
    }
}
