package com.jyg.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;


public class FTLLoader {
    
    private static final String TEMPLATE_PATH = "/src/main/java/";
    private static final String CLASS_PATH = "src/main/java/test";
    
    static Configuration configuration = new Configuration( Configuration.VERSION_2_3_23 );
    
    @Inject
    public FTLLoader(){
    	
    	configuration.setClassForTemplateLoading(FTLLoader.class, "/");
    	
    }
    
    public byte[] getFtl(String ftlName , Map<String, String> params  )  {
    	try {
	    	Template template = configuration.getTemplate(ftlName);
	    	
	    	try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    			Writer out = new OutputStreamWriter(bos);){
	    		template.process(params, out);
	    		return bos.toByteArray();
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    
    public byte[] getIndexFtl(String username) {
    	
    	Map<String, String> params = new HashMap<>();
        params.put("username", username);
    	
		return getFtl("index.ftl",params);
    }
    
    
    
    public void test() {
        // step1 创建freeMarker配置实例
        
        Writer out = null;
        try {
            // step3 创建数据模型
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("classPath", "test");
            dataMap.put("className", "AutoCodeDemo");
            dataMap.put("helloWorld", "通过简单的 <代码自动生产程序> 演示 FreeMarker的HelloWorld！");
            // step4 加载模版文件
            Template template = configuration.getTemplate("hello.ftl");
            // step5 生成数据
            File docFile = new File(CLASS_PATH + "\\" + "AutoCodeDemo.java");
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // step6 输出文件
            template.process(dataMap, out);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^AutoCodeDemo.java 文件创建成功 !");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}