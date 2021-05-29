package com.jyg.gameserver;

import org.junit.Test;
import org.jyg.gameserver.core.manager.InstanceManager;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2021/5/1
 */
public class TestInstance {
    public static class ComputerCPU{

        String name;

    }

    public static class Computer{
        private ComputerCPU computerCPU;

        public Computer(ComputerCPU computerCPU) {
            this.computerCPU = computerCPU;
        }
    }


    @Test
    public void testInstance() throws Exception{
        InstanceManager instanceManager = new InstanceManager((Context) null);
        instanceManager.putInstance(ComputerCPU.class);

        instanceManager.putInstance(Computer.class);

        ComputerCPU computerCPU = instanceManager.getInstance(ComputerCPU.class);
        computerCPU.name = "maik";
        Computer computer = instanceManager.getInstance(Computer.class);

        AllUtil.println(computer.computerCPU.name);
    }

}
