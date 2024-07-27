package org.jyg.gameserver.db.type;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class JsonTypeHandler<T> implements TypeHandler<T> {

    private final Class<T> jsonClazz;

    public JsonTypeHandler(Class<T> jsonClazz) {
        this.jsonClazz = jsonClazz;
    }

    public void setParameter(PreparedStatement ps, int index, T parameter) throws SQLException {
        final String strParam;
        if(parameter == null){
            strParam = null;
        } else {
            strParam = JSONObject.toJSONString(parameter);
        }

        ps.setString(index , strParam);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if(StringUtils.isEmpty(result)){
            try {
                return jsonClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }

        T resultInstance = JSONObject.parseObject(result , jsonClazz);
        return resultInstance;

    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);

        if(StringUtils.isEmpty(result)){
            try {
                return jsonClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }

        T resultInstance = JSONObject.parseObject(result , jsonClazz);
        return resultInstance;

    }


    @Override
    public String typeToString(T t){
        if(t == null){
            return null;
        }
        return JSONObject.toJSONString(t);
    }


}
