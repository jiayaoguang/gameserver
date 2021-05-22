package org.jyg.gameserver.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class UpdateSQLMaker extends CachedSQLMaker {

    @Override
    protected String createPrepareSql(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {

        StringBuilder sqlSB = new StringBuilder();

        sqlSB.append(sqlKeyWord.update()).append(' ');
        sqlSB.append(tableInfo.getTableName()).append(' ');

        List<String> fieldNameList = new ArrayList<>(tableInfo.getFieldInfoLinkedMap().keySet());

        fieldNameList.remove(tableInfo.getPrimaryKey());

        if(fieldNameList.isEmpty()){
            throw new IllegalArgumentException("fieldNameList.isEmpty");
        }

        sqlSB.append(sqlKeyWord.set()).append(' ');
        for (int i = 0; i < fieldNameList.size(); i++) {
            String fieldName = fieldNameList.get(i);

            sqlSB.append(fieldName).append('=').append('?').append(' ');
            if(i+1 < fieldNameList.size()){
                sqlSB.append(',');
            }
        }

        sqlSB.append(sqlKeyWord.where()).append(' ');
        sqlSB.append(tableInfo.getPrimaryKey()).append(' ');
        sqlSB.append('=').append('?').append(';');

        return sqlSB.toString();
    }

    @Override
    protected List<Object> getParamValues(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {
        List<Object> valueParams = new ArrayList<>(tableInfo.getFieldInfoLinkedMap().size());

        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldInfoLinkedMap().values()) {

            if(tableInfo.getPrimaryKey().equals(tableFieldInfo.getClassField().getName())){
                continue;
            }

            Object fieldValue = null;

            if (tableFieldInfo.getFiedGetMethod() != null) {
                try {
                    fieldValue = tableFieldInfo.getFiedGetMethod().invoke(dbEntity);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (fieldValue == null) {
                fieldValue = tableFieldInfo.getClassField().get(dbEntity);
            }

            if (fieldValue == null) {
                return null;
            }

            valueParams.add(fieldValue);
        }

        Object primaryKeyValue = tableInfo.getPrimaryKeyField().get(dbEntity);

        valueParams.add(primaryKeyValue);

        return valueParams;
    }


    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.MODIFY;
    }

}
