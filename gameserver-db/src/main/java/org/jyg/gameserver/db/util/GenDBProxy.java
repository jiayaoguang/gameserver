package org.jyg.gameserver.db.util;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.ClassUtil;
import org.jyg.gameserver.core.util.FTLLoader;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang on 2023/4/22
 * 生成db代理,简化update
 */
public class GenDBProxy {


    private GenDBProxy() {
    }

    public static void genDBProxyFile(Class clazz , String srcDir){
        String genClassSuffix = "Proxy";

        String outPutDir = srcDir;

        if(!srcDir.endsWith("\\") && !srcDir.endsWith("/")){
            outPutDir += "/";
        }
        outPutDir += (clazz.getPackage().getName().replace(".","/"));

        outPutDir+=("/"+genClassSuffix+"/");


        FTLLoader ftlLoader = new FTLLoader();

        Map<String, Object> params = new HashMap<>();

        params.put("className", clazz.getSimpleName());
        params.put("packageName", clazz.getPackage().getName());
        params.put("suffix", genClassSuffix);


        List<Map<String, String>> fieldInfoMapList = new ArrayList<Map<String, String>>();

        List<Field> fields = ClassUtil.getClassObjectFields(clazz);
        for (Field field : fields) {
            Map<String, String> fieldInfoMap = new HashMap<String, String>();
            fieldInfoMap.put("fieldNote", "hello");
            fieldInfoMap.put("fieldType", field.getType().getName());
            fieldInfoMap.put("fieldName", field.getName());
            if(field.getAnnotation(DBTableFieldIgnore.class) == null){
                fieldInfoMap.put("needUpdate" , "true");
            }else {
                fieldInfoMap.put("needUpdate" , "false");
            }
            String setMethodName = "set" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            String getMethodName = "get" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            if(field.getType() == Boolean.class || field.getType() == boolean.class){
                if(MethodUtils.getAccessibleMethod(clazz ,setMethodName , field.getType()) == null){
                    setMethodName = "set" + field.getName().substring(2);
                }

                if(MethodUtils.getAccessibleMethod(clazz ,getMethodName) == null){
                    getMethodName = "is" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
                }
                if(MethodUtils.getAccessibleMethod(clazz ,getMethodName) == null){
                    getMethodName =  field.getName();
                }
            }

            if(MethodUtils.getAccessibleMethod(clazz ,setMethodName,field.getType()) == null){
                throw new RuntimeException(" filed set method not found : " + field.getName());
            }

            if(MethodUtils.getAccessibleMethod(clazz ,getMethodName) == null){
                throw new RuntimeException(" filed get method not found : " + field.getName());
            }

            fieldInfoMap.put("getMethodName" , getMethodName);
            fieldInfoMap.put("setMethodName" , setMethodName);
            fieldInfoMapList.add(fieldInfoMap);
        }
        params.put("filedInfos", fieldInfoMapList);

//        List<String> methodNames = new ArrayList<>();

//        for (Method method : clazz.getMethods()) {
//
//            String methodName = method.getName();
//            StringBuilder methodSb = new StringBuilder();
//
//
//            if (methodName.equals("getClass")) {
//                continue;
//            }
//
//            if (methodName.startsWith("get")) {
//                methodSb.append("public ").append(method.getReturnType().getName()).append(" ")
//                        .append(methodName).append("(){\n\t\t return dbEntity.").append(methodName)
//                        .append("(); \n\t}");
//            } else if (methodName.startsWith("set")) {
//                String fieldName = methodName.substring(3, 4).toLowerCase(Locale.ROOT) + methodName.substring(4);
//                Field field = AllUtil.getClassObjectField(clazz, fieldName);
//                methodSb.append("public void ").append(methodName).append("(")
//                        .append(method.getParameterTypes()[0].getName()).append(" ").append(fieldName)
//                        .append(")").append("{\n\t\tdbEntity.").append(methodName).append("(")
//                        .append(fieldName).append("); \n");
//
//                if (field != null && field.getAnnotation(DBTableFieldIgnore.class) == null) {
//                    methodSb.append("\t\tconsumerDBManager.update(dbEntity); \n");
//                }
//                methodSb.append("\t}");
//            } else if (methodName.startsWith("is")) {
//                String fieldName = methodName.substring(2, 3).toLowerCase(Locale.ROOT) + methodName.substring(3);
//                Field field = AllUtil.getClassObjectField(clazz, fieldName);
//                if(field == null){
//                    fieldName = methodName;
//                    field = AllUtil.getClassObjectField(clazz, fieldName);
//                }
//                methodSb.append("public void ").append(methodName).append("(")
//                        .append(method.getParameterTypes()[0].getName()).append(" ").append(fieldName)
//                        .append(")").append("{\n\t\tdbEntity.").append(methodName).append("(")
//                        .append(fieldName).append("); \n");
//
//                if (field != null && field.getAnnotation(DBTableFieldIgnore.class) == null) {
//                    methodSb.append("\t\tconsumerDBManager.update(dbEntity); \n");
//                }
//                methodSb.append("\t}");
//            } else {
//                continue;
//            }
//
//
//            methodNames.add(methodSb.toString());
//        }
//        params.put("methodNameList", methodNames);

        byte[] result = ftlLoader.getFtl("dbProxy.ftl", params);
//        AllUtil.println(new String(result));

        File outFile = new File(outPutDir + clazz.getSimpleName() + genClassSuffix + ".java");
        FileUtil.writeString( new String(result) , outFile , StandardCharsets.UTF_8 );
        AllUtil.println(outFile.getAbsolutePath());
    }

}
