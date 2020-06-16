package com.easydictionary.core.util;

import com.easydictionary.core.annotation.table.ModelField;
import com.easydictionary.core.annotation.table.handler.impl.DefaultPropOutputHandler;
import com.easydictionary.core.manager.model.BeanManager;
import com.easyframework.easydict.manager.DictionaryManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Slf4j
public class TableFieldUtil {


    public static String convertExcelProperty(ModelField tableHeader, Field field, Object value){
        String result = null;
        if (value == null || "".equals(value) || value.equals("null")) {
            return "";
        }

        boolean isNumber = NumberUtil.isNumber(value.toString());
        if (!DefaultPropOutputHandler.class.equals(tableHeader.outputHander())) {
            value = BeanManager.getObject(tableHeader.outputHander()).handle(value);
            if(value == null){
                return "";
            }
        }

        if (!Object.class.equals(tableHeader.dictModel())) {
            result = DictionaryManager.getDictText(value, tableHeader.dictModel());
        }  else if (!StringUtils.isBlank(tableHeader.dict())){
            //log.info("tableHeader.dict()  {},{}", tableHeader.dict(), value);
            String[] dirts = tableHeader.dict().split("&");
            boolean flag = false ;
            for (String dirt : dirts ) {
                String[] kv = dirt.split("=");
                if(value.toString().equals(kv[0])){
                    result = kv[1];
                    flag = true;
                    break;
                }
            }
            if(!flag){
                result = value.toString();
            }
        } else if (tableHeader.amountFormat() && isNumber) {
            result = NumberUtil.fixAmount(value.toString());
        } else if (tableHeader.radixPoint() != 0 && isNumber) {
            result = NumberUtil.fixRadixPoint(value.toString(), tableHeader.radixPoint());
        } else if (!StringUtils.isBlank(tableHeader.dateFormat())) {
            try {
                if (value.getClass().equals(Date.class)) {
                    result = new SimpleDateFormat(tableHeader.dateFormat()).format(((Date) value));
                } else if (value.getClass().equals(java.sql.Date.class)) {
                    result = new SimpleDateFormat(tableHeader.dateFormat()).format(((java.sql.Date) value));
                } else if (value.getClass().equals(Timestamp.class)) {
                    Long stamp = ((Timestamp) value).getTime();
                    if (stamp / 1000000000000L < 1) {
                        stamp = stamp * 1000;
                    }
                    result = new SimpleDateFormat(tableHeader.dateFormat()).format(new Date(stamp));
                } else if (value.getClass().equals(Long.class)) {
                    Long stamp = (Long) value;
                    if (stamp / 1000000000000L < 1) {
                        stamp = stamp * 1000;
                    }
                    result = new SimpleDateFormat(tableHeader.dateFormat()).format(new Date(stamp));
                } else if (value.getClass().equals(LocalDateTime.class)) {
                    result = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(tableHeader.dateFormat()));
                }else{
                    result = value.toString();
                }
            } catch (Exception e) {
                log.info(field.toString() + "字段 从" + value.getClass() + "类型 日期转换失败,值" + value, e);
                result = "";
            }
        } else if (!StringUtils.isBlank(tableHeader.dict())) {
            String[] dirts = tableHeader.dict().split("&");
            for (String dirt : dirts) {
                String[] kv = dirt.split("=");
                if (value.equals(kv[0])) {
                    result = kv[1];
                    break;
                }
            }
        } else {
            result = value.toString();
        }
        return result;
    }

}
