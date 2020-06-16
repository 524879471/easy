package com.easyframework.easydict.dictionary;

import com.easyframework.easydict.annotation.DictElement;
import com.easyframework.easydict.annotation.DictModel;

@DictModel("是否")
public class Flag {
    @DictElement("是")
    public static final Short YES = 1;
    @DictElement("否")
    public static final Short NO = 0;
}
