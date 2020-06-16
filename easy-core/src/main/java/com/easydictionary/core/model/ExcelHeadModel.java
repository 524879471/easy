package com.easydictionary.core.model;

import com.easydictionary.core.annotation.table.ModelField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelHeadModel {
    public ExcelHeadModel(String name, String text, int colIndex, int width) {
        this.name = name;
        this.text = text;
        this.colIndex = colIndex;
        this.width = width;
    }
    public ExcelHeadModel() {
    }

    private String name ;
    private String text ;
    private int rowSpan = 1;
    private int colSpan = 1;
    private int width = 0;
    private int colIndex = 0;
    private boolean hidden = false;

    private ExcelHeadModel parent = null;
    private List<ExcelHeadModel> children = new ArrayList<>();
    private String dateFormat;
    private boolean amountFormat = false;
    private boolean makeTotal = false;
    private BigDecimal total = null;
    private int radixPoint;
    private String dict;
    private boolean ch = false;
    private ModelField tableField = null;


    public void addRowSpan(){
        this.rowSpan++;
    }
    public void addRowSpan(int num){
        this.rowSpan += num;
    }
    public void addColSpan(){
        this.colSpan++;
    }
    public void addColSpan(int num){
        this.colSpan = num + this.colSpan;
    }


    @Override
    public String toString() {
        return "ExcelHeadModel{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", rowSpan=" + rowSpan +
                ", colSpan=" + colSpan +
                ", colIndex=" + colIndex +
                '}';
    }
}
