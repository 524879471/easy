package com.easydictionary.core.manager.model;

import com.easydictionary.core.interfaces.BeanLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanManager {
    private static BeanLoader loader =  null;

    public void initBeanLoader(BeanLoader loader){
        BeanManager.loader = loader;
    }




    public static <T> T getObject(Class<T> clazz){
        if(loader != null){
            try{
                return loader.load(clazz);
            }catch (RuntimeException e){
            }
        }
        try {
           return  clazz.newInstance();
        } catch (Exception e1) {
           log.error("实例化handler失败", e1);
        }
        return  null;
    }
}
