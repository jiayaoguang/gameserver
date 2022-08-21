package org.jyg.gameserver.db;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;

import java.lang.reflect.Method;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ByteBuddyManager {

    private final GameContext gameContext;

    ByteBuddy byteBuddy;

    public ByteBuddyManager(GameContext gameContext) {
        this.gameContext = gameContext;
        byteBuddy = new ByteBuddy();
        LoggerAdvisor.gameContext = gameContext;
    }


    public <T> T create(Class<T> tClazz) throws IllegalAccessException, InstantiationException {
        T t = byteBuddy.subclass(tClazz)
                .name("org.jyg.db.byteBuddy." + tClazz.getSimpleName())
                .method(ElementMatchers.any())
                .intercept(Advice.to(LoggerAdvisor.class))
//                .intercept(MethodDelegation.to(Target2.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();
        return t;
    }

    public static class LoggerAdvisor {

        private static GameContext gameContext;

        @Advice.OnMethodEnter
        public static void onMethodEnter(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments) {
            AllUtil.println("setter ....................");
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments, @Advice.Return Object ret) {
            if(!method.getName().startsWith("set")){
                return;
            }



        }
    }

}
