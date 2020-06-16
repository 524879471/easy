package com.easydictionary.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * @Date: 2019/5/8
 * @Auther: dwy
 */
@Slf4j
public class ModelUtil {
    private static  SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static{
        ConvertUtils.register(new Converter() {
            public Object convert(Class type, Object value) {

                //判断是不是String类型的数据，不是则抛出异常
                if(!(value instanceof String)){
                    throw new ConversionException("不是String数据类型！");
                }
                //是String的话，把Object的value强转成String
                String strValue = (String) value;
                //判断是不是一个空字符串
                if(strValue.trim().equals("")){
                    return null;
                }

                try {
                    if(strValue.length() < 19){
                        return DATE_FORMAT.parse(strValue);
                    }else{
                        return TIME_FORMAT.parse(strValue);
                    }
                } catch (ParseException e) {
                    log.error("参数必须为字符串", e);
                }
                return null;
            }
        }, Date.class);
    }


    public static <T> T map2Bean(Map<String, String> map, Class<T> beanClass){
        T bean =null;
        try {
            //1、创建要封装数据的bean
            bean = beanClass.newInstance();
            //2、把request中的数据整到bean中
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String name = iterator.next();
                try {
                    beanClass.getDeclaredField(name);
                }catch (NoSuchFieldException noField){
                    continue;
                }
                String value = map.get(name);
                BeanUtils.setProperty(bean, name, value);
            }
        } catch (Exception e) {
            log.error("获取请求bean出错", e);
        }
        return bean;
    }
    public static <T> T objectMap2Bean(Map<String, Object> map, Class<T> beanClass){
        T bean =null;
        try {
            //1、创建要封装数据的bean
            bean = beanClass.newInstance();
            //2、把request中的数据整到bean中
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String name = iterator.next();
                if(beanClass.getDeclaredField(name) == null){
                    continue;
                }
                String value = map.get(name).toString();
                BeanUtils.setProperty(bean, name, value);
            }
        } catch (Exception e) {
            log.error("获取请求bean出错", e);
        }
        return bean;
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass){
        T obj = null;
        try {
            if (map == null)
                return null;
            obj = beanClass.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                Object value = map.get(field.getName());
                if (value == null || "".equals(value) || Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                try {

                    if (!value.getClass().equals(field.getType())) {
                        if (field.getType().equals(String.class)) {
                            value = value.toString();
                        } else if (field.getType().equals(Long.class)) {
                            value = Long.valueOf(value.toString());
                        } else if (field.getType().equals(Integer.class)) {
                            value = Integer.valueOf(value.toString());
                        } else if (field.getType().equals(Short.class)) {
                            value = Short.valueOf(value.toString());
                        } else if (field.getType().equals(Float.class)) {
                            value = Float.valueOf(value.toString());
                        } else if (field.getType().equals(Double.class)) {
                            value = Double.valueOf(value.toString());
                        } else if (field.getType().equals(Date.class)) {
                            if (value.getClass().equals(java.sql.Date.class)) {
                                value = new Date(((java.sql.Date) value).getTime());
                            } else if (value.getClass().equals(Long.class)) {
                                Long stamp = (Long) value;
                                if (stamp / 1000000000000L < 1) {
                                    stamp = stamp * 1000;
                                }
                                value = new Date(stamp);
                            }
                        } else if (field.getType().equals(LocalDateTime.class)) {
                            long stamp = 0;
                            if (value.getClass().equals(java.sql.Date.class)) {
                                stamp = ((java.sql.Date) value).getTime();
                            } else if (value.getClass().equals(Long.class)) {
                                stamp = (Long) value;
                                if (stamp / 1000000000000L < 1) {
                                    stamp = stamp * 1000;
                                }
                            }
                            value = TimeUtil.getDateTimeOfTimestamp(stamp);
                        }
                    }
                }catch (Exception e){
                    log.error("转化为实体类属性反射失败  class:：" + beanClass.getName() + ", field："+ field.getName(), e);
                }
                field.setAccessible(true);
                field.set(obj, value);
            }
        }catch(Exception e){
            log.error("map转化实体类失败" + beanClass.getName(), e);
        }
        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if(obj == null)
            return null;
        Map<String, Object> map = new HashMap<String, Object>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter!=null ? getter.invoke(obj) : null;
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, String> objectToStringMap(Object obj) throws Exception {
        if(obj == null)
            return null;
        Map<String, String> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter!=null ? getter.invoke(obj) : null;
            if(value != null){
                map.put(key, value.toString());
            }
        }
        return map;
    }
}
