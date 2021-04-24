package org.jyg.gameserver.core.invoke;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * create by jiayaoguang on 2021/4/24
 */
public class FileClassLoader extends ClassLoader {

    private final String rootDir;

    public FileClassLoader() {
        this("");
    }

    public FileClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 获取类的class文件字节数组
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            //直接生成class对象
            return defineClass(name, classData, 0, classData.length);
        }
    }


    private byte[] getClassData(String className) {

        String classFilePath = classNameToPath(className);

        return FileUtil.readBytes(classFilePath);

    }


    /**
     * 类文件的完全路径
     * @param className className
     * @return pathName
     */
    private String classNameToPath(String className) {
        return rootDir + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";
    }

}
