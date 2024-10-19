package org.jyg.gameserver.core.annotaion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * create by jiayaoguang on 2023/5/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={  METHOD})
public @interface InvokeRemoteMethod {



    String uname() default "";


    int targetConsumerId();

}
