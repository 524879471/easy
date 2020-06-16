package com.easydictionary.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static void main(String[] a){
        System.out.println(chineseCount("订单11234"));
    }

    /**如果数组或者列表 转成逗号隔开字符串  否则返回toString*/
    public static String toStringJoinComma(Object array) {
        StringBuilder sb = new StringBuilder();
        if(array.getClass().isArray()) {
           for (Object obj: (Object[])array) {
               sb.append(obj).append(",");
           }
        }else if( array instanceof Collection){
            for (Object obj: (Collection)array) {
                sb.append(obj).append(",");
            }
        }else{
            sb.append(array);
        }
        if(0 <sb.length()){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static int chineseCount(String str) {
       int count = 0;
        char[] charArray = str.toCharArray();
        for (char c : charArray ) {
            if(isChinese(c)){
                count++;
            }
        }
        return count;
    }

    public static boolean isChinese(char c) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(String.valueOf(c));
        return matcher.find();
    }

    /**
     * 驼峰转下划线
     * @return
     */
    public static String nameFormatTo_(String name) {
        if (name == null) {
            return null;
        }
        char[] charArray = name.toCharArray();
        List<Character> characters = new ArrayList<>();
        boolean lastUp = false;
        boolean serriesUp = false;
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                if(!lastUp){
                    characters.add('_');
                }else{
                    serriesUp = true;
                }
                characters.add(Character.toLowerCase(c));
                lastUp = true;
            } else {
                if(serriesUp){
                    characters.add(characters.size()- 1,'_');
                }
                characters.add(c);

                lastUp = false;
                serriesUp = false;
            }
        }
        return StringUtils.join(characters, "");
    }

    public static String secretString(String str){
        if(str == null){
            return null;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length - 4; i++) {
            charArray[i] = '*';
        }
        return new String(charArray);
    }
}
