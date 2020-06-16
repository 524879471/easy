package com.easydictionary.core.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.regex.Pattern;
@Slf4j
public class NumberUtil {

    public  static void main(String[] args){
       System.out.println(isNumber("478.1948"));
    }

    public static boolean isNumber(String str) {
        if(str == null){
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String fixAmount(String amount){
        return fixRadixPoint(amount, 2);
    }


    public static String fixRadixPoint(Float amount, int radixPoint){
        return fixRadixPoint(amount.toString(), radixPoint);
    }

    public static String fixRadixPoint(String amount, int radixPoint){
        BigDecimal bigDecimal = new BigDecimal(amount);
        return bigDecimal.setScale(radixPoint, BigDecimal.ROUND_HALF_UP).toString();
    }
}
