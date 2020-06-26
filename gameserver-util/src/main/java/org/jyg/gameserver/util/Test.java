package org.jyg.gameserver.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * create by jiayaoguang on 2020/5/17
 */

public class Test {

    public static void main(String[] args) throws Exception {
        ParseProto parse = new ParseProto();
        String desc = parse.genProtoDesc("options.proto");
        Map<String, Integer> extendInfo = parse.getExtendInfo(desc);
        Map<String, Object> msgInfo = parse.getMsgInfo(desc);

        System.out.println("扩展信息：");
        for(Entry<String, Integer> e : extendInfo.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }

        System.out.println("\n协议信息：");
        for(Entry<String, Object> e : msgInfo.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }
    }

}
