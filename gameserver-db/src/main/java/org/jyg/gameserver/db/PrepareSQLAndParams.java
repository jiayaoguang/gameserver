package org.jyg.gameserver.db;

import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class PrepareSQLAndParams {

    public final String prepareSQL;
    public final List<Object> params;

    public PrepareSQLAndParams(String prepareSQL, List<Object> params) {
        this.prepareSQL = prepareSQL;
        this.params = params;
    }
}
