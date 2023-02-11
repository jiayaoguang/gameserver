package org.jyg.gameserver.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class ExcelUtil {

//    public static void jsonToExcel(List<JSONObject> jsonObjectList, String excelFilePath, String sheetName) throws IOException {
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.addAll(jsonObjectList);
//
//        jsonArrayToExcel(jsonArray, excelFilePath, sheetName);
//
//    }

    public static void jsonArrayToExcel(JSONArray jsonArray, String excelFilePath, String sheetName) throws IOException {


        if (jsonArray.size() < 1) {
            throw new IllegalArgumentException("jsonArray.size() < 1");
        }


        JSONObject fieldNameJson = jsonArray.getJSONObject(0);
        List<String> fieldNames = new ArrayList<>(fieldNameJson.keySet());


        FileOutputStream fo = null;
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            if (FileUtil.exist(excelFilePath)) {
                fis = new FileInputStream(excelFilePath);
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook();
            }
            fo = new FileOutputStream(excelFilePath);


            Sheet sheet = workbook.createSheet(sheetName);

            {
                Row firstRow = sheet.createRow(0);
                for(int colIndex = 0;colIndex < fieldNames.size();colIndex++ ){
                    String fieldName = fieldNames.get(colIndex);
                    Cell cell = firstRow.createCell(colIndex);
                    cell.setCellValue(fieldName);
                }
            }


            for(int rowIndex =0;rowIndex<jsonArray.size();rowIndex++ ){
                JSONObject jsonObject = (JSONObject) jsonArray.get(rowIndex);

                Row row = sheet.createRow(rowIndex + 1);

                for(int colIndex = 0;colIndex < fieldNames.size();colIndex++ ){
                    String fieldName = fieldNames.get(colIndex);
                    Object filedValue = jsonObject.get(fieldName);
                    Cell cell = row.createCell(colIndex);
                    if(filedValue != null){
                        if (filedValue instanceof Integer) {
                            cell.setCellValue((Integer) filedValue);
                        } else {
                            cell.setCellValue(filedValue.toString());
                        }
                    }

                }


            }

            workbook.write(fo);
            fo.flush();

        }
//        catch (Exception e){
//            FileUtil.del(new File(excelFilePath));
//            throw e;
//        }
        finally {
            if (fo != null) {
                IoUtil.close(fo);
            }
            if(fis != null){
                IoUtil.close(fis);
            }
            if (workbook != null) {
                IoUtil.close(workbook);
            }
        }
    }


    public static void excelToJsonFile(String excelFilePath, String outPutDirPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(excelFilePath);) {

            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            for (Sheet sheet; sheetIterator.hasNext(); ) {
                sheet = sheetIterator.next();

                sheet.getRow(0);

            }
        }


    }

    public void testCreate() throws IOException {


        JSONArray jsonArray = JSONArray.parseArray("[{\"addExp\":30000,\"linkItemSn\":11021030,\"sn\":1,\"type\":1},{\"addExp\":300000,\"linkItemSn\":11021031,\"sn\":2,\"type\":1},{\"addExp\":3000000,\"linkItemSn\":11021032,\"sn\":3,\"type\":1},{\"addExp\":100000,\"linkItemSn\":11019010,\"sn\":4,\"type\":1},{\"addExp\":300000,\"linkItemSn\":11019012,\"sn\":5,\"type\":1},{\"addExp\":1000,\"linkItemSn\":11021070,\"sn\":6,\"type\":2},{\"addExp\":1000,\"linkItemSn\":11019009,\"sn\":7,\"type\":2},{\"addExp\":-6,\"linkItemSn\":11021033,\"sn\":8,\"type\":3}]");

        FileUtil.del(new File("D://tmp/tmp2.xlsx"));

        jsonArrayToExcel(jsonArray, "D://tmp/tmp2.xlsx", "ufc1");
        jsonArrayToExcel(jsonArray, "D://tmp/tmp2.xlsx", "ufc2");
    }

}
