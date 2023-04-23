package org.jyg.gameserver.db;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class UpdateFieldSQLBuilder implements SQLBuilder{


    private final Map<Field,String> updateFieldSqlMap = new HashMap<>(128,0.5f );

    protected String createPrepareSql(SqlKeyWord sqlKeyWord , TableInfo tableInfo, String fieldName) {


        StringBuilder sqlSB = new StringBuilder();

        sqlSB.append(sqlKeyWord.update()).append(' ');
        sqlSB.append('`').append(tableInfo.getTableName()).append('`').append(' ');

        sqlSB.append(sqlKeyWord.set()).append(' ');

        sqlSB.append('`').append(fieldName).append('`').append('=').append('?').append(' ');

        sqlSB.append(sqlKeyWord.where()).append(' ');
        sqlSB.append('`').append(tableInfo.getPrimaryKey()).append('`').append(' ');
        sqlSB.append('=').append('?').append(';');

        return sqlSB.toString();
    }



    @Override
    public PrepareSQLAndParams createSqlInfo(SqlKeyWord sqlKeyWord, Object dbEntity, TableInfo tableInfo, Map<String, Object> params) throws Exception {

        String fieldName = (String) params.get("fieldName");

        if(StringUtils.isEmpty(fieldName)){

            throw new IllegalArgumentException("update field fail,fieldName param not exist");
        }

        TableFieldInfo tableFieldInfo = tableInfo.getFieldInfoLinkedMap().get(fieldName);
        if(tableFieldInfo == null){
            throw new IllegalArgumentException("update field fail,fieldName not found");
        }




        String prepareSql = updateFieldSqlMap.get(tableFieldInfo.getClassField());
        if(StringUtils.isEmpty(prepareSql)){
            prepareSql = createPrepareSql(sqlKeyWord, tableInfo, fieldName);
            updateFieldSqlMap.put(tableFieldInfo.getClassField() , prepareSql);
        }

        List<Object> valueParams = new ArrayList<>(tableInfo.getFieldInfoLinkedMap().size());



        Object fieldNewValue = tableFieldInfo.getFieldOperator().readObject(dbEntity);
        valueParams.add(fieldNewValue);

        Object primaryKeyValue = tableInfo.getPrimaryKeyFieldInfo().getFieldOperator().readObject(dbEntity);

        valueParams.add(primaryKeyValue);





        return new PrepareSQLAndParams(prepareSql ,valueParams , getExecuteType());
    }

    @Override
    public SqlExecuteType getExecuteType() {
        return SqlExecuteType.MODIFY;
    }

}
