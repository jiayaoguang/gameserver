package org.jyg.gameserver.test.db.proxy;

import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.test.db.MaikDB;

/**
 * generate code
 */
public class MaikDBProxy {

    private final MaikDB dbEntity;

    private final ConsumerDBManager consumerDBManager;

    public MaikDBProxy(MaikDB dbEntity , ConsumerDBManager consumerDBManager) {
        this.dbEntity = dbEntity;
        this.consumerDBManager = consumerDBManager;
    }



    public void setId (int id){
        dbEntity.setId(id);
        consumerDBManager.updateField(this.dbEntity , "id");
    }

    public int getId(){
        return dbEntity.getId();
    }


    public void setNextUpdateTime (long nextUpdateTime){
        dbEntity.setNextUpdateTime(nextUpdateTime);
    }

    public long getNextUpdateTime(){
        return dbEntity.getNextUpdateTime();
    }


    public void setContent (java.lang.String content){
        dbEntity.setContent(content);
        consumerDBManager.updateField(this.dbEntity , "content");
    }

    public java.lang.String getContent(){
        return dbEntity.getContent();
    }


    public void setAge (int age){
        dbEntity.setAge(age);
        consumerDBManager.updateField(this.dbEntity , "age");
    }

    public int getAge(){
        return dbEntity.getAge();
    }


    public void setPay (boolean pay){
        dbEntity.setPay(pay);
        consumerDBManager.updateField(this.dbEntity , "pay");
    }

    public boolean isPay(){
        return dbEntity.isPay();
    }



    public MaikDB getDBEntity(){
        return dbEntity;
    }



}