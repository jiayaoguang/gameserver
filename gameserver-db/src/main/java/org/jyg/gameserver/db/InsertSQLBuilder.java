package org.jyg.gameserver.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class InsertSQLBuilder extends CachedSQLBuilder {


    //    @Override
   /* public PrepareSQLAndParams qq(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {

        final StringBuilder insertSqlSb = new StringBuilder(50);


        insertSqlSb.append(sqlKeyWord.insert()).append(' ')
                .append(sqlKeyWord.into()).append(' ');
        insertSqlSb.append(tableInfo.getTableName()).append(' ');

        insertSqlSb.append('(').append(' ');

        int insertKeyNameNum = 0;

        for (String tableFieldName : tableInfo.getFieldInfoLinkedMap().keySet()) {
            insertKeyNameNum++;
            insertSqlSb.append(tableFieldName);
            if (insertKeyNameNum != tableInfo.getFieldInfoLinkedMap().size()) {
                insertSqlSb.append(',');
            }
        }
        insertSqlSb.append(')').append(' ');

        insertSqlSb.append(sqlKeyWord.values()).append(' ');

        insertSqlSb.append('(').append(' ');

        int insertParamNum = 0;

        List<Object> valueParams = new ArrayList<>(tableInfo.getFieldInfoLinkedMap().size());

        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {
            Object fieldValue = null;

            if (tableFieldInfo.getFiedGetMethod() != null) {
                fieldValue = tableFieldInfo.getFiedGetMethod().invoke(dbEntity);
            }

            if (fieldValue == null) {
                fieldValue = tableFieldInfo.getClassField().get(dbEntity);
            }

            if (fieldValue == null) {
                return null;
            }

            insertParamNum++;
            insertSqlSb.append('?');
            if (insertParamNum != tableInfo.getFieldInfoLinkedMap().size()) {
                insertSqlSb.append(',');
            }

            valueParams.add(fieldValue);

        }

        insertSqlSb.append(')').append(';');


        String prepareSql = insertSqlSb.toString();

        insertSqlSb.setLength(0);

        return new PrepareSQLAndParams(prepareSql, valueParams, SqlExecuteType.MODIFY);
    }*/

    @Override
    protected String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        final StringBuilder insertSqlSb = new StringBuilder(50);


        insertSqlSb.append(sqlKeyWord.insert()).append(' ')
                .append(sqlKeyWord.into()).append(' ');
        insertSqlSb.append('`').append(tableInfo.getTableName()).append('`').append(' ');

        insertSqlSb.append('(').append(' ');

        int insertKeyNameNum = 0;

        for (String tableFieldName : tableInfo.getFieldInfoLinkedMap().keySet()) {
            insertKeyNameNum++;
            insertSqlSb.append('`').append(tableFieldName).append('`');
            if (insertKeyNameNum != tableInfo.getFieldInfoLinkedMap().size()) {
                insertSqlSb.append(',');
            }
        }
        insertSqlSb.append(')').append(' ');

        insertSqlSb.append(sqlKeyWord.values()).append(' ');

        insertSqlSb.append('(').append(' ');

        final int paramNum = tableInfo.getFieldInfoLinkedMap().size();

        for (int i = 0; i < paramNum ; i++) {

            insertSqlSb.append('?');
            if (i < paramNum - 1) {
                insertSqlSb.append(',');
            }

        }

        insertSqlSb.append(')').append(';');
        return insertSqlSb.toString();
    }

    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.MODIFY;
    }

    @Override
    protected List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        List<Object> valueParams = new ArrayList<>(tableInfo.getFieldInfoLinkedMap().size());

        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {
//            Object fieldValue = null;

//            if (tableFieldInfo.getFiedGetMethod() != null) {
//                try {
//                    fieldValue = tableFieldInfo.getFiedGetMethod().invoke(dbEntity);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }

            Object fieldValue = tableFieldInfo.getFieldOperator().readObject(dbEntity);

            if(fieldValue == null){

            }

            valueParams.add(fieldValue);

        }
        return valueParams;
    }


}
