package org.jyg.gameserver.db.type;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class FastJSONTypeHandler implements TypeHandler<JSONObject> {

    public void setParameter(PreparedStatement ps, int index, JSONObject parameter) throws SQLException {
        String strParameter = JSONObject.toJSONString(parameter);
        ps.setString(index , strParameter);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String strResult = rs.getString(columnIndex);
        if(StringUtils.isEmpty(strResult)){
            return new JSONObject();
        }
        return JSONObject.parseObject(strResult);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String strResult = rs.getString(columnName);
        if(StringUtils.isEmpty(strResult)){
            return new JSONObject();
        }
        return JSONObject.parseObject(strResult);
    }

    @Override
    public Class<JSONObject> getBindClassType() {
        return JSONObject.class;
    }

}
