package org.jyg.gameserver.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.jyg.gameserver.test.db.MaikDB;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * create by jiayaoguang at 2021/5/20
 */
public class ByteBuddyTest02 {


    public static void main(String[] args) throws Exception {


        MaikDB source = create(MaikDB.class);


//        Advice.to(BytebuddyTest.LoggerAdvisor.class);

//        AllUtil.println(source.hello("222"));
//
//        AllUtil.println(source.getClass().getName());
    }

    public static <T> T create(Class<T> tClazz) throws IllegalAccessException, InstantiationException {


        T t = new ByteBuddy().subclass(tClazz)
                .name("org.jyg.db.byteBuddy." + tClazz.getSimpleName())
                .method(ElementMatchers.isSetter())
                .intercept(Advice.to(LoggerAdvisor.class))
//                .intercept(MethodDelegation.to(Target2.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();
        return t;
    }

    public static class Source {
        public String hello(String name) { return null; }
    }

    public static class Source02 {

        public int age;

        public Source02(){

        }

        public int age() {
            return 0;
        }

    }


    public static class LoggerAdvisor {
        @Advice.OnMethodEnter
        public static void onMethodEnter(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments) {
            System.out.println("Enter " + method.getName() + " with arguments: " + Arrays.toString(arguments));
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments, @Advice.Return Object ret) {
            System.out.println("Exit " + method.getName() + " with arguments: " + Arrays.toString(arguments) + " return: " + ret);
        }
    }

}
