package com.jyg.gameserver;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    int i = 0;
    AppTest(){
    }
    public static void main(String[] args) {
    	AppTest a = new AppTest();
    	ReferenceQueue<Object> queue = new ReferenceQueue<>();
    	Object o = new Object();
    	PhantomReference<Object> r = new PhantomReference<>(o,queue);
    	System.out.println(r.get());
    	o=null;
    	System.gc();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(queue.poll().get());
    	System.out.println(r.get());

	}
    
}
