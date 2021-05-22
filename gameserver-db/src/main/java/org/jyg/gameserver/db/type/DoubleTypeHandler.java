package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class DoubleTypeHandler implements TypeHandler<Double> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Double parameter)
            throws SQLException {
        ps.setDouble(i, parameter);
    }

    @Override
    public Double getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        double result = rs.getDouble(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Double getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        double result = rs.getDouble(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Class<Double> getBindClassType() {
        return Double.class;
    }

}
