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

    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

    public T getNullableResult(ResultSet rs, String columnName)throws SQLException;

    Class<T> getBindClassType();

    default TableFieldType getTableFieldType(){
        return TableFieldType.VARCHAR;
    }

}
