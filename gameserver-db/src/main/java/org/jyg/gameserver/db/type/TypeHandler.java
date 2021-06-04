package org.jyg.gameserver.db.type;

import org.jyg.gameserver.db.TableFieldType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by jiayaoguang at 2021/5/17
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement ps, int index, T parameter) throws SQLException;

    @Deprecated
    default T getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return null;
    }

    T getNullableResult(ResultSet rs, String columnName) throws SQLException;

}
