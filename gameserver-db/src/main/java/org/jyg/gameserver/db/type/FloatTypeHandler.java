package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class FloatTypeHandler implements TypeHandler<Float> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Float parameter)
            throws SQLException {
        ps.setDouble(i, parameter);
    }

    @Override
    public Float getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        float result = rs.getFloat(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Float getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        float result = rs.getFloat(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }


}
