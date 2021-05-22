package org.jyg.gameserver.db.type;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class StringTypeHandler implements TypeHandler<String> {

    public void setParameter(PreparedStatement ps, int index, String parameter) throws SQLException {
        ps.setString(index , parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return result;
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return result;
    }

    @Override
    public Class<String> getBindClassType() {
        return String.class;
    }

}
