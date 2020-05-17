package org.jyg.gameserver.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.UnknownFieldSet;

/**
 * create by jiayaoguang on 2020/5/10
 */

public class ParseProto {
    public final String PROTOC_DIR =  "D:\\dev\\protobuf3\\bin\\protoc.exe ";    // protoc.exe所在文件夹
    public final String PROTO_FILE_DIR ="";

    /**
     * 获取自定义扩展的proto信息
     *
     * @param extendDescFileDir 自定义的扩展文件对应的desc文件路径，如options.desc
     * @return
     * @throws Exception
     */
    public Map<String, Integer> getExtendInfo(String extendDescFileDir) throws Exception {
        Map<String, Integer> result = new HashMap<>();

        // 解析desc文件，获取proto文件中的自己扩展
        FileDescriptorSet fdSet = FileDescriptorSet.parseFrom(new FileInputStream(extendDescFileDir));
        List<FileDescriptorProto> fs = fdSet.getFileList();
        for (FileDescriptorProto fdp : fs) {
            // 获取自定义的extend扩展中的name->value
            List<FieldDescriptorProto> as = fdp.getExtensionList();
            for (FieldDescriptorProto a : as) {
                result.put(a.getName(), a.getNumber());
            }
        }

        return result;
    }

    /**
     * 根据proto文件获取其中的message信息
     *
     * @return
     * @throws Exception
     */
    public Map<String, Object> getMsgInfo(String descFileDir) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        // 根据得到的desc文件
        FileDescriptorSet fdSet = FileDescriptorSet.parseFrom(new FileInputStream(descFileDir));
        for (FileDescriptorProto fdp : fdSet.getFileList()) {
            // 遍历获取各message信息
            for (DescriptorProto dp : fdp.getMessageTypeList()) {
                String msgName = dp.getName();        // message名称
                String value = "";

                UnknownFieldSet uf = dp.getOptions().getUnknownFields();
                for (Map.Entry<Integer, UnknownFieldSet.Field> entry : uf.asMap().entrySet()) {
                    UnknownFieldSet.Field val = entry.getValue();
                    value = val.getLengthDelimitedList().get(0).toStringUtf8();
                }

                // 如果存在msgId则记录结果
                if (!uf.asMap().isEmpty()) {
                    result.put(msgName, value);
                }
            }
        }

        return result;
    }

    /**
     * 生成proto文件描述信息desc
     *
     * @param protoName
     * @return
     * @throws Exception
     */
    public String genProtoDesc(String protoName) throws Exception {
        // 目标文件名
        String descFileDir = this.PROTO_FILE_DIR + protoName.replaceAll(".proto", ".desc");

        // 命令：protoc -I=$SRC_DIR descriptor_set_out=$DST_DIR/***.desc $SRC_DIR/***.proto
        StringBuffer cmd = new StringBuffer();
        cmd.append("cmd /c ");
        cmd.append(PROTOC_DIR);
        cmd.append("-I=" + this.PROTO_FILE_DIR).append(" --descriptor_set_out=" + descFileDir).append(" ").append(this.PROTO_FILE_DIR + protoName);

        String failMsg = "生成desc文件命令执行失败!";
        execCommand(cmd.toString(), "生成desc描述信息文件", failMsg);

        return descFileDir;
    }

    /**
     * 执行cmd控制台命令
     *
     * @param cmd     完整命令语句
     * @param failMsg 失败时提示语句
     * @throws Exception
     */
    private void execCommand(String cmd, String execMsg, String failMsg) throws Exception {
        // 执行系统命令
        System.out.println("===" + execMsg + "===执行命令:" + cmd);
        Runtime run = Runtime.getRuntime();
        Process p = run.exec(cmd);

        // 如果不正常终止，则生成desc文件失败
        if (p.waitFor() != 0) {
            if (p.exitValue() == 1) {
                System.err.println(failMsg);

                // 打印输出的错误信息
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                System.out.println();
            }
        }
        System.out.println("命令执行完毕\n");
    }
}
