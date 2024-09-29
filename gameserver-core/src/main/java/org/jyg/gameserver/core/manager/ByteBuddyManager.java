package org.jyg.gameserver.core.manager;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;

import java.lang.reflect.Method;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ByteBuddyManager {

//    private final GameContext gameContext;

    ByteBuddy byteBuddy;

    public ByteBuddyManager() {
        this.byteBuddy = new ByteBuddy();
    }


//    public <T> T create(Class<T> tClazz) throws IllegalAccessException, InstantiationException {
//        T t = byteBuddy.subclass(tClazz)
//                .name("org.jyg.db.byteBuddy." + tClazz.getSimpleName())
//                .method(ElementMatchers.any())
//                .intercept(Advice.to(LoggerAdvisor.class))
////                .intercept(MethodDelegation.to(Target2.class))
//                .make()
//                .load(ClassLoader.getSystemClassLoader())
//                .getLoaded()
//                .newInstance();
//        return t;
//    }


    public <T> T createProxy(Class<T> tClazz,Object delegationObj) throws IllegalAccessException, InstantiationException {
        T t = byteBuddy.subclass(tClazz)
                .name(tClazz.getPackageName() + ".delegation." + tClazz.getSimpleName())
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(delegationObj))
//                .intercept(MethodDelegation.to(Target2.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();
        return t;
    }


}
