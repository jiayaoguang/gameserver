package org.jyg.gameserver.db;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class InsertSQLMaker implements SQLMaker {

    private final StringBuilder insertSqlSb = new StringBuilder(50);

    @Override
    public PrepareSQLAndParams createSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo) {

        insertSqlSb.setLength(0);

        List<Object> params = new ArrayList<>(tableInfo.getFieldInfoMap().size());


        insertSqlSb.append(sqlKeyWord.insert()).append(' ')
                .append(sqlKeyWord.into()).append(' ');
        insertSqlSb.append(tableInfo.getTableName()).append(' ');

        insertSqlSb.append('(').append(' ');

        int insertKeyNameNum  = 0;

        for(String tableFieldName : tableInfo.getFieldInfoMap().keySet() ){
            insertKeyNameNum ++;
            insertSqlSb.append(tableFieldName);
            if(insertKeyNameNum != tableInfo.getFieldInfoMap().size()){
                insertSqlSb.append(',');
            }
        }
        insertSqlSb.append(')').append(' ');

        insertSqlSb.append(sqlKeyWord.values()).append(' ');

        insertSqlSb.append('(').append(' ');

        int insertParamNum = 0;

        for(TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoMap().values() ){
            Object fieldValue = null;

            if(tableFieldInfo.getFiedGetMethod() != null){
                try {
                    fieldValue = tableFieldInfo.getFiedGetMethod().invoke(dbEntity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if(fieldValue == null){
                try {
                    fieldValue = tableFieldInfo.getClassField().get(dbEntity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if(fieldValue == null){
                return null;
            }

            insertParamNum ++;
            insertSqlSb.append('?');
            if(insertParamNum != tableInfo.getFieldInfoMap().size()){
                insertSqlSb.append(',');
            }

            params.add(fieldValue);

        }

        insertSqlSb.append(')').append(';');


        String prepareSql = insertSqlSb.toString();

        insertSqlSb.setLength(0);

        return new PrepareSQLAndParams(prepareSql , params);
    }




}
