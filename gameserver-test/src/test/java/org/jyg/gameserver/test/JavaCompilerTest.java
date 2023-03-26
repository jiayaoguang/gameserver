package org.jyg.gameserver.test;

import org.junit.Test;
import org.jyg.gameserver.core.invoke.FileClassLoader;
import org.jyg.gameserver.core.util.AllUtil;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

/**
 * create by jiayaoguang on 2021/8/1
 */
public class JavaCompilerTest {

    @Test
    public void test() throws Exception{
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        int num = compiler.run(null , null , null , "D://tmp/Player.java");

        AllUtil.println(num);

        FileClassLoader fileClassLoader = new FileClassLoader("D:/tmp/");
        Class clazz = fileClassLoader.loadClass("Player");
        clazz.getMethod("main",String[].class).invoke(null, (Object) new String[]{""});
    }



}
