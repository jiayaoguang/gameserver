package org.jyg.gameserver.db.type;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class JsonTypeHandler<T> implements TypeHandler<T> {

    private final JsonMapper jsonMapper = new JsonMapper();

    private final Class<T> jsonClazz;

    public JsonTypeHandler(Class<T> jsonClazz) {
        this.jsonClazz = jsonClazz;
    }

    public void setParameter(PreparedStatement ps, int index, T parameter) throws SQLException {
        final String strParam;
        if(parameter == null){
            strParam = null;
        } else {
            try {
                strParam = jsonMapper.writeValueAsString(parameter);
            } catch (JsonProcessingException e) {
//            e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
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

        try {
            T resultInstance = jsonMapper.readValue(result , jsonClazz);
            return resultInstance;
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }

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

        try {
            T resultInstance = jsonMapper.readValue(result , jsonClazz);
            return resultInstance;
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }

    }


    @Override
    public String typeToString(T t){
        if(t == null){
            return null;
        }
        try {
            return jsonMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }


}
