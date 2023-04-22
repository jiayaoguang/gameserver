package ${packageName}.proxy;

import org.jyg.gameserver.db.ConsumerDBManager;
import ${packageName}.${className};

/**
 * generate code
 */
public class ${className}${suffix} {

    private final ${className} dbEntity;

    private final ConsumerDBManager consumerDBManager;

    public ${className}Proxy(${className} dbEntity , ConsumerDBManager consumerDBManager) {
        this.dbEntity = dbEntity;
        this.consumerDBManager = consumerDBManager;
    }


<#list filedInfos as filedInfo>

    public void ${filedInfo.setMethodName} (${filedInfo.fieldType} ${filedInfo.fieldName}){
        dbEntity.${filedInfo.setMethodName}(${filedInfo.fieldName});
        <#if filedInfo.needUpdate=="true" >
        consumerDBManager.update(dbEntity);
        </#if>
    }

    public ${filedInfo.fieldType} ${filedInfo.getMethodName}(){
        return dbEntity.${filedInfo.getMethodName}();
    }

</#list>



}