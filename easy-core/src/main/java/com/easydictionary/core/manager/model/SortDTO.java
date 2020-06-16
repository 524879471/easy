package com.easydictionary.core.manager.model;

import lombok.Data;

/**
 * @author 黄智
 * @version V1.0
 * @description 排序字段传输对象
 **/
@Data
public class SortDTO {

    private String origin;
    private String ordered;
    private String hasSort;

}
