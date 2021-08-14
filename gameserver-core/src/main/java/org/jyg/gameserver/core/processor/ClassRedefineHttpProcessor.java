package org.jyg.gameserver.core.processor;

import cn.hutool.core.io.FileUtil;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.util.ClassRedefineUtil;

import java.io.File;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;

/**
 * create by jiayaoguang at 2021/5/29
 */
public class ClassRedefineHttpProcessor extends HttpProcessor {


    public ClassRedefineHttpProcessor() {
        super("/classRedefine");
    }

    @Override
    public void service(Request request, Response response) {

        File rootDir = new File("redeine/");

        if(!rootDir.exists() || !rootDir.isDirectory()){
            response.writeAndFlush("!rootDir.exists() || !rootDir.isDirectory()");
            return;
        }

        try {
            redefineDirClasses(rootDir.getAbsolutePath(),rootDir);
            response.writeAndFlush("redeine success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.write500Error("ClassNotFoundException");
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            response.write500Error("UnmodifiableClassException");
        }


    }


    private void redefineDirClasses(String rootPath ,File file) throws ClassNotFoundException, UnmodifiableClassException {
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if(childFiles == null){
                return;
            }
            for(File childFile : childFiles){
                redefineDirClasses(rootPath , childFile);
            }
            return;
        }

        String className = file.getAbsolutePath().replace(rootPath , "").replace('/','.');

        Class<?> redefineClass = Class.forName(className);

        ClassRedefineUtil.redefine(redefineClass , FileUtil.readBytes(file));
    }

}
