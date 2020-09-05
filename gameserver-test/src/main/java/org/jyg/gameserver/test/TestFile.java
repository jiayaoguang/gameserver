package org.jyg.gameserver.test;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.jyg.gameserver.core.util.AllUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang on 2020/8/22
 */
public class TestFile {

    public static void main(String[] args) {

        String path = "C:\\Users\\jiayaoguang\\Desktop\\abc.txt";
        List<String>  lines = FileUtil.readLines(new File(path) , CharsetUtil.UTF_8);

        Map<String ,KV> map = new HashMap<>();

        for(String line : lines){

            String[] as = line.split("\t");
            String day = as[0];
            String uid = as[1];
            int num = Integer.parseInt(as[2]);
            KV kv = map.getOrDefault(uid , new KV());
            map.put(uid , kv);

            if(day.endsWith("1")){
                kv.k = day;
                kv.num1 = num;
            }else {
                kv.v = day;
                kv.num2 = num;
            }
            kv.uid = uid;
            kv.level = as[3];
            kv.cash = as[4];
        }

        int i = 1;
        for ( KV kv : map.values() ){

            if(kv.num1 + kv.num2 < 200){
                continue;
            }

            if(kv.k != null && kv.v != null){
                AllUtil.println((i++) + "  \t" + kv.num1 + " \t "+ kv.num2 + " \t " + kv.uid + " \t " + kv.level +"\t" + kv.cash + " \t day2 remain :  \t" + (200 - kv.num2));
            }

            if(kv.num1 == 200){
                AllUtil.println((i++) + "  \t" + kv.num1 + " \t " + kv.num2  + " \t " + kv.uid+ " \t " + kv.level +"\t" + kv.cash );
            }

        }


    }


    public static class KV{
        String uid;
        String k;
        String v;

        int num1;
        int num2;

        String level;
        String cash;
    }

}
