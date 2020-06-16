package com.easydictionary.core.manager.model;

import lombok.Data;

/**
 * @Description:
 * @Date: 2019/5/14
 * @Auther: dwy
 */
@Data
public class ColumnModel {
    private String sql;
    private String poly;
    private String name;
    private int groupLevel;
}
