package com.easyframework.easydict.manager;

import com.easyframework.easydict.ClassUtil;
import com.easyframework.easydict.annotation.DictElement;
import com.easyframework.easydict.annotation.DictModel;
import com.easyframework.easydict.exception.AnnotationPresentException;
import com.easyframework.easydict.exception.DictionaryFeildException;
import com.easyframework.easydict.exception.DictionaryNameRepeatException;
import com.easyframework.easydict.exception.DictionaryNotFoundException;
import com.easyframework.easydict.handler.AbstractActionHandler;
import com.easyframework.easydict.interfaces.BeanLoader;
import com.easyframework.easydict.interfaces.DefaultBeanLoader;
import com.easyframework.easydict.model.DictionaryModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:  数据字典操作
 * @Date: 2019/4/30
 * @Auther: dwy
 */
@Slf4j
public class DictionaryManager {
    private static Map<Class<?>, DictionaryModel>  dictionaryMap = new ConcurrentHashMap<>();
    private static Map<String, Class<?>>  modelClassMap = new ConcurrentHashMap<>();

    static BeanLoader<AbstractActionHandler> beanLoader =new DefaultBeanLoader();


    private static String dictionaryPackageName = "";


    public static  void registBeanLoader(BeanLoader beanLoader){
        DictionaryManager.beanLoader = beanLoader;
    }


    public static  void registPackage(String packageName){
        dictionaryPackageName = packageName;
       List<Class<?>> list = ClassUtil.getClassesWithAnnotationFromPackage(dictionaryPackageName,DictModel.class);
        list.forEach( clazz ->{
            registDictionary(clazz);
            ActionManager.checkAndRegistAction(clazz);
        });

    }

    public static  void registDictionary(Class<?> clazz){
        dictionaryMap.put(clazz, loadDictionary(clazz));
        if(modelClassMap.containsKey(clazz.getSimpleName())){
            Class<?> clazzOld = modelClassMap.get(clazz.getSimpleName());
            if(!clazzOld.equals(clazz)){
                throw new DictionaryNameRepeatException(" new:" + clazz.getName()+"; old:" + clazzOld);
            }
            return;
        }
        modelClassMap.put(clazz.getSimpleName(),clazz);

    }




    public static Object getDictKey(String value, Class<?> modelClass){
        Map<Object, String> map = dictionaryMap.get(modelClass).getDictMap();
        if(map.containsValue(value)){
            Iterator<Object> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                Object key = iterator.next();
                if(map.get(key).equals(value)){
                    return key;
                }
            }
        }
        return null;

    }

    public static Map<Object, String> getDictionaryMap(String modelName) {
        return getDictionaryMap(modelClassMap.get(modelName));
    }

    public static Map<Object, String> getDictionaryMap(String modelName, String searchValue) {
        return getDictionaryMap(modelClassMap.get(modelName), searchValue);
    }

    public static String getDictText(Object key, Class<?> modelClass){
        return dictionaryMap.get(modelClass).getDictMap().get(key);
    }

    public static Map<Object, String> getDictionaryMap(Class<?> modelClass){
        return dictionaryMap.get(modelClass).getDictMap();
    }



    public static Map<Object, String> getDictionaryMap(Class<?> modelClass, String searchValue){
        if(StringUtils.isBlank(searchValue)){
            return getDictionaryMap(modelClass);

        }
        Map<Object, String>  myDictionaryMap  = dictionaryMap.get(modelClass).getDictMap();
        Map<Object, String>  resault = new HashMap<>();
        Iterator<Object> iterator = myDictionaryMap.keySet().iterator();
        while (iterator.hasNext()){
            Object key = iterator.next();
            String value = myDictionaryMap.get(key);
            if(value.contains(searchValue)){
                resault.put(key, value);
            }
        }
        return resault;
    }
    public static  void registDictionaryModel(Class<?> modelClass,Map<Object,String> dictModelProp) {
        dictionaryMap.put(modelClass, new DictionaryModel(modelClass, dictModelProp));

    }

    public static  void registDictionary(Class<?> modelClass,String code, String name) {
        if(dictionaryMap.containsKey(modelClass)){
            dictionaryMap.get(modelClass).getDictMap().put(code,name);
        }else{
            Map<Object, String> dictMap =new ConcurrentHashMap<>();
            dictMap.put(code,name);
            dictionaryMap.put(modelClass, new DictionaryModel(modelClass, dictMap));
        }
    }



    public static  DictionaryModel loadDictionary(Class<?> clazz) {
        log.info("加载数据字典{}", clazz.getName());
        Map<Object, String> dictMap =new ConcurrentHashMap<>();
        DictModel dictModel = clazz.getAnnotation(DictModel.class);
        Field[] fields = clazz.getFields();
        for (Field field: fields) {
            if(field.isAnnotationPresent(DictElement.class)){
                try {
                    dictMap.put(field.get(clazz), field.getAnnotation(DictElement.class).value());
                } catch (IllegalAccessException e) {
                    throw  new DictionaryFeildException(String.format("字典属性解析错误 : %s" , clazz.getName()), e);
                }
            }
        }

        return new DictionaryModel(clazz, dictMap);
    }


}

