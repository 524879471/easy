package com.easyframework.easydict.manager;

import com.easyframework.easydict.annotation.DictElement;
import com.easyframework.easydict.annotation.DictModel;
import com.easyframework.easydict.exception.DictionaryFeildException;
import com.easyframework.easydict.handler.AbstractActionHandler;
import com.easyframework.easydict.handler.DefaultActionHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ActionManager {


    private static Map<Object, AbstractActionHandler> handlerPool = new ConcurrentHashMap<>();



    public static void checkAndRegistAction(Class<?> clazz){
        if(!clazz.isAnnotationPresent(DictModel.class)){
            return;
        }
        DictModel dictModel = clazz.getAnnotation(DictModel.class);
        if(!dictModel.isAction()){
            return;
        }
        log.info("发现行为埋点dictionary {}",clazz.getName());
        Field[] fields = clazz.getFields();
        for (Field field: fields) {
            if(field.isAnnotationPresent(DictElement.class)){
                DictElement dictElement = field.getAnnotation(DictElement.class);
                if(!dictElement.actionHandlerClass().equals(DefaultActionHandler.class)){
                    try {
                        AbstractActionHandler bean = DictionaryManager.beanLoader.loadBean(dictElement.actionHandlerClass());
                        log.info("注册行为埋点{} hanlder ={}",dictElement.value(),dictElement.actionHandlerClass());
                        regiestActionCodeHandler(field.get(clazz), bean);
                    } catch (IllegalAccessException e) {
                        throw new DictionaryFeildException(String.format("字典属性解析错误 : %s" , clazz.getName()), e);
                    }
                }
            }
        }

    }

    public static <T extends AbstractActionHandler> void regiestActionCodeHandler(Object code, T handler){
        handlerPool.put(code, handler);
    }

    public static <T extends AbstractActionHandler> String excuteAction(Object code,Object... object){
        return handlerPool.get(code).handle(code,object);
    }


}
