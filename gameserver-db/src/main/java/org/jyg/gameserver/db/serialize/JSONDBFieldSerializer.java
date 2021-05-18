package org.jyg.gameserver.db.serialize;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * create by jiayaoguang on 2021/5/16
 */
public class JSONDBFieldSerializer implements DBFieldSerializer<JSONObject> {

    public Object serialize(String str) {
        if (StringUtils.isEmpty(str)) {
            return new JSONObject();
        }

        return JSONObject.parseObject(str, JSONObject.class);
    }

    public String unserialize(Object jsonObject) {
        return JSONObject.toJSONString(jsonObject);
    }

    public Class<JSONObject> getSerializeClass() {
        return JSONObject.class;
    }

}
