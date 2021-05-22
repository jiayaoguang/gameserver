package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class BooleanTypeHandler implements TypeHandler<Boolean> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter)
            throws SQLException {
        ps.setBoolean(i, parameter);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        boolean result = rs.getBoolean(columnName);
        return !result && rs.wasNull() ? null : result;
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        boolean result = rs.getBoolean(columnIndex);
        return !result && rs.wasNull() ? null : result;
    }

    @Override
    public Class<Boolean> getBindClassType() {
        return Boolean.class;
    }

}
