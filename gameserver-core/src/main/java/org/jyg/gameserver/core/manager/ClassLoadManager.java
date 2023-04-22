package org.jyg.gameserver.core.manager;

import cn.hutool.core.io.FileUtil;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ClassRedefineUtil;
import org.jyg.gameserver.core.util.Logs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/8/14
 * TODO 改为可选classLoader,默认AppLoader
 */

public class ClassLoadManager {


    private final URLClassLoader urlClassLoader;

    private final File classFileDir;

    public ClassLoadManager(String classFileDirPath) {
        classFileDir = new File(classFileDirPath);

        if(classFileDir.exists() && !classFileDir.isDirectory()){
            throw new RuntimeException("class load path not dir");
        }

        if (classFileDir.exists() && !classFileDir.isDirectory()) {
            throw new RuntimeException();
        }
        URL url = null;
        try {
            url = classFileDir.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        urlClassLoader = new URLClassLoader(new URL[]{url});
    }

    public List<Class<?>> loadClasses() {

        List<Class<?>> classList = new ArrayList<>();


        List<File> childClassFiles = AllUtil.getChildFiles(classFileDir, "class");

        for (File childClassFile : childClassFiles) {


//            String className =
//                    StringUtils.replaceOnce (childClassFile.getAbsolutePath() ,classFileDir.getAbsolutePath(),"" )
//                    .replace("/",".").replace("\\",".").replace(".class","");

            String className = ClassRedefineUtil.readClassName(FileUtil.readBytes(childClassFile));

            Logs.DEFAULT_LOGGER.info("find class: "+className);
            try {
                Class<?> c = urlClassLoader.loadClass(className);
                classList.add(c);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }


        return classList;

    }

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }
}
