import org.junit.Test;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.DBConfig;
import org.jyg.gameserver.db.SimpleDataSource;
import org.jyg.gameserver.db.SqlExecuteType;
import org.jyg.gameserver.db.SqlExecutor;
import org.jyg.gameserver.db.type.TypeHandlerRegistry;

import java.util.ArrayList;

/**
 * create by jiayaoguang on 2022/10/25
 */
public class SqlTest {

    @Test
    public void sqlTest()throws Exception{


        DBConfig dbConfig = new DBConfig();
        dbConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&createDatabaseIfNotExist=true&serverTimezone=UTC");
        dbConfig.setPassword("123456");
        dbConfig.setUsername("root");


        SimpleDataSource simpleDataSource = new SimpleDataSource(dbConfig);

        SqlExecutor sqlExecutor = new SqlExecutor( simpleDataSource , new TypeHandlerRegistry());
        sqlExecutor.tryConnectIfClose();

        Object obj = sqlExecutor.executeSql("show tables;" , new ArrayList<>() , SqlExecuteType.EXECUTE);

        AllUtil.println("==== :" + obj.getClass().getSimpleName());
    }

}
