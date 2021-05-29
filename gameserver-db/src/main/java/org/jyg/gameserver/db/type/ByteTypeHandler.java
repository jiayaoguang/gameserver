package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class ByteTypeHandler implements TypeHandler<Byte> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Byte parameter)
            throws SQLException {
        ps.setByte(i, parameter);
    }

    @Override
    public Byte getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        byte result = rs.getByte(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Byte getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        byte result = rs.getByte(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }


}
