package org.jyg.gameserver.test.db;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class MaikDB extends BaseMaikDB {



    private String content;


    private int age;

    private boolean pay;

    public MaikDB() {
        int i= 0;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isPay() {
        return pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }
}
