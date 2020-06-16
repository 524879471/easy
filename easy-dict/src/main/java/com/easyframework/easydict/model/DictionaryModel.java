package com.easyframework.easydict.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
public class DictionaryModel {
    public DictionaryModel(Class<?> modelClass,Map<Object, String> dictMap){
        this.modelClass = modelClass;
        this.dictMap = dictMap;
        this.name = modelClass.getSimpleName();

    }
    Class<?> modelClass;
    String name;
    Map<Object, String> dictMap;
}
