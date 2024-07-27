package org.jyg.gameserver.db.anno;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.jyg.gameserver.db.FieldIndexType;
import org.jyg.gameserver.db.TableFieldInfo;
import org.jyg.gameserver.db.TableFieldType;

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
@Target({ElementType.FIELD})
@Inherited
public @interface DBTableField {

    String fieldName() default "";

    int fieldLength() default 0;

    TableFieldType fieldType() default TableFieldType.AUTO;

    String comment() default "";

    FieldIndexType indexType() default FieldIndexType.NONE;

    String defaultValue() default "";
}
