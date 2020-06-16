package com.easyframework.easydict.annotation;

import com.easyframework.easydict.handler.DefaultDictionaryDataHandler;
import com.easyframework.easydict.interfaces.DictionaryDataHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字典注解
 * 
 * @author 丁文渊
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DictModel {
//	public static final String CACHE_TYPE_NONE= "none";//不缓存
//	public static final String CACHE_TYPE_NORM = "norm";//缓存类型  内存缓存
//	public static final String CACHE_TYPE_REDIS= "redis";//缓存类型  redis
//	public String sql() default "";  //获取字典的sql 结果级必须有code和name两列
//  public String cacheType() default CACHE_TYPE_NORM;  //缓存类型 norm:缓存在内存中 reids:使用redis
//  public int cacheValid() default 0;  //缓存有效期，默认不失效
	public String value();  //字典名
	public Class<? extends DictionaryDataHandler> dataHandler() default DefaultDictionaryDataHandler.class;  //字典名
	public boolean isAction() default false;  //是否为埋点行为字典
}