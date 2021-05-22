package org.jyg.gameserver.db.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public class CharacterTypeHandler implements TypeHandler<Character> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Character parameter)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public Character getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String columnValue = rs.getString(columnName);
        if (columnValue != null) {
            return columnValue.charAt(0);
        } else {
            return null;
        }
    }

    @Override
    public Character getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String columnValue = rs.getString(columnIndex);
        if (columnValue != null) {
            return columnValue.charAt(0);
        } else {
            return null;
        }
    }

    @Override
    public Class<Character> getBindClassType() {
        return Character.class;
    }

}
