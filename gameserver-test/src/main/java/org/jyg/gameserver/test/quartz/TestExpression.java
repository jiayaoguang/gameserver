package org.jyg.gameserver.test.quartz;

import org.jyg.gameserver.core.util.ExecTimeUtil;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * create by jiayaoguang on 2022/12/4
 */
public class TestExpression {

    public static void main(String[] args) throws ParseException {


        long now = System.currentTimeMillis();

        System.out.println("now : " + now);

        CronExpression cronExpression = new CronExpression("5/10 0 2 1 * ? * ");



        Date t = cronExpression.getNextValidTimeAfter(new Date(now));
        System.out.println(t.getTime());
        t = cronExpression.getNextValidTimeAfter(t);
        System.out.println(t.getTime());

        t = cronExpression.getNextValidTimeAfter(t);
        System.out.println(t.getTime());

        System.out.println(cronExpression.getTimeAfter(new Date(now)).getTime());
        System.out.println(cronExpression.getNextInvalidTimeAfter(new Date(now)).getTime());


        ExecTimeUtil.exec("cronExpression",()->{

            for(int i= 1;i<10000;i++){
                Date t2 = cronExpression.getTimeAfter(new Date(now));
            }
        });

    }

}
