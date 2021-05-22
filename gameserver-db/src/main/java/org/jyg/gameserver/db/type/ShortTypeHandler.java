package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class ShortTypeHandler implements TypeHandler<Short> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Short parameter)
            throws SQLException {
        ps.setShort(i, parameter);
    }

    @Override
    public Short getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        short result = rs.getShort(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Short getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        short result = rs.getShort(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Class<Short> getBindClassType() {
        return Short.class;
    }

}
