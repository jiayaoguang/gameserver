package org.jyg.gameserver.db.anno;

import org.jyg.gameserver.db.TableFieldType;

import java.lang.annotation.*;

/**
 * create by jiayaoguang at 2021/5/16
 * db 忽略字段
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface DBTableFieldIgnore {


}
