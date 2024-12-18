package org.jyg.gameserver.db.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create by jiayaoguang at 2021/5/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface DBTable {

    String tableName();

    String primaryKey() default "id";

    String comment() default "";

    /**
     * 排序规则
     * @return 排序规则
     */
    String collate() default "utf8mb4_0900_ai_ci";

}
