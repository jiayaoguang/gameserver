package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class LongTypeHandler implements TypeHandler<Long> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Long parameter)
            throws SQLException {
        ps.setLong(i, parameter);
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        long result = rs.getLong(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Long getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        long result = rs.getLong(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Class<Long> getBindClassType() {
        return Long.class;
    }

}
